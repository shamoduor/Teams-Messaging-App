package com.shamine.teamsmessagingapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shamine.teamsmessagingapp.fragments.chats.ProfileFragment;
import com.shamine.teamsmessagingapp.retrofit.RetrofitApiClient;
import com.shamine.teamsmessagingapp.room.MyDatabase;
import com.shamine.teamsmessagingapp.room.entities.ChatGroup;
import com.shamine.teamsmessagingapp.room.entities.ChatGroupMember;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.entities.GroupMessageDelivery;
import com.shamine.teamsmessagingapp.room.entities.MessageGroup;
import com.shamine.teamsmessagingapp.room.entities.MessagePrivate;
import com.shamine.teamsmessagingapp.room.entities.User;
import com.shamine.teamsmessagingapp.room.models.GenericMessage;
import com.shamine.teamsmessagingapp.room.models.SyncRequest;
import com.shamine.teamsmessagingapp.room.repositories.ChatGroupRepository;
import com.shamine.teamsmessagingapp.room.repositories.ContactRepository;
import com.shamine.teamsmessagingapp.room.repositories.GroupMessageRepository;
import com.shamine.teamsmessagingapp.room.repositories.PrivateMessageRepository;
import com.shamine.teamsmessagingapp.room.repositories.UserRepository;
import com.shamine.teamsmessagingapp.room.viewmodel.ContactViewModel;
import com.shamine.teamsmessagingapp.room.viewmodel.UserViewModel;
import com.shamine.teamsmessagingapp.utils.TinyDb;
import com.shamine.teamsmessagingapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import tech.gusavila92.websocketclient.WebSocketClient;

public class ChatActivity extends AppCompatActivity {
    private AlertDialog dotsDialog;
    private User loggedInUser;
    private WebSocketClient webSocketClient;
    private TextView txtToolbarTitle;
    private CircleImageView civToolbarProfilePic;
    private AutoCompleteTextView tlbAutoCompeteTextView;
    private ContactViewModel contactViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        registerReceiver(networkConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        Toolbar toolbar = findViewById(R.id.toolbar);

        txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
        civToolbarProfilePic = findViewById(R.id.civToolbarProfilePic);
        tlbAutoCompeteTextView = findViewById(R.id.tlbAutoCompeteTextView);
        setSupportActionBar(toolbar);

        contactViewModel = new ViewModelProvider(ChatActivity.this).get(ContactViewModel.class);

        UserViewModel userViewModel = new ViewModelProvider(ChatActivity.this).get(UserViewModel.class);

        userViewModel.getLoggedInUserLiveData().observe(ChatActivity.this, user -> {
            if (user != null) {
                loggedInUser = user;
                contactViewModel.setLoggedInUserId(loggedInUser.getUserId());
                createWebSocketClient();
                Intent intent = new Intent();
                intent.setAction(ProfileFragment.ACTION_PICK_PROFILE_PIC_REQUEST_CODE);
                sendBroadcast(intent);
                updateFirebaseMessageToken();
            } else {
                logOut();
            }
        });
        Utils.requestNotificationPermission(this);
    }

    public WebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    public void createWebSocketClient() {
        if (webSocketClient == null && loggedInUser != null) {
            URI uri;
            try {
                Log.d("WebSocket", "Connect to local host");
                // Connect to local host
                uri = new URI(RetrofitApiClient.WEB_SOCKET_BASE_URL);
            } catch (URISyntaxException e) {
                Log.d("WebSocket", "catch: " + (e.getMessage() != null ? e.getMessage() : ""));
                e.printStackTrace();
                return;
            }
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen() {
                    Log.d("WebSocket", "Session is started");
                    syncMessages();
                }

                @Override
                public void onTextReceived(String s) {
                    Log.d("WebSocket", "onTextReceived");
                    try {
                        Log.d("onTextReceived", s);
                        Type empMapType = new TypeToken<GenericMessage>() {
                        }.getType();
                        GenericMessage message = new Gson().fromJson(s, empMapType);

                        if (message.getChatGroup() != null && message.getChatGroupMembers() != null) {
                            saveChatGroup(message.getChatGroup(), message.getChatGroupMembers());
                        }

                        if (message.getMessageGroup() != null && message.getGroupMessageDeliveries() != null) {
                            saveGroupMessage(message.getMessageGroup(), message.getGroupMessageDeliveries());
                        }

                        if (message.getMessagePrivate() != null) {
                            savePrivateMessage(message.getMessagePrivate());
                            if (message.getMessagePrivate().getRecipientId() == loggedInUser.getUserId() && message.getContact() != null) {
                                saveContact(message.getContact());
                            }
                        }

                        if (message.getSyncRequest() != null && message.getSyncRequest().getLastSyncTimestamp() > 0) {
                            TinyDb.setLastUpdateTimestamp(ChatActivity.this, message.getSyncRequest().getLastSyncTimestamp());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("onTextReceived", "exception");
                    }
                }

                @Override
                public void onBinaryReceived(byte[] data) {
                    Log.d("WebSocket", "onBinaryReceived");
                }

                @Override
                public void onPingReceived(byte[] data) {
                    Log.d("WebSocket", "onPingReceived");
                }

                @Override
                public void onPongReceived(byte[] data) {
                    Log.d("WebSocket", "onPongReceived");
                }

                @Override
                public void onException(Exception e) {
                    Log.d("WebSocket", "onException");
                    Log.d("WebSocket", e.getMessage() != null ? e.getMessage() : "null exception");

                }

                @Override
                public void onCloseReceived() {
                    Log.i("WebSocket", "Closed");
                }
            };
            webSocketClient.addHeader("Authorization", RetrofitApiClient.TOKEN_PREFIX + loggedInUser.getToken());
            webSocketClient.setConnectTimeout(10000);
            webSocketClient.setReadTimeout(60000);
            webSocketClient.enableAutomaticReconnection(3000);
            webSocketClient.connect();
        }
    }

    private void syncMessages() {
        try {
            long lastSyncTimestamp = 1;
            //long lastSyncTimestamp = TinyDb.getLastUpdateTimestamp(ChatActivity.this);
            int loggedInUserId = getLoggedInUser().getUserId();
            List<MessagePrivate> privateMessagesToSync = MyDatabase.getInstance(ChatActivity.this).messagePrivateDao().findAllToSync(lastSyncTimestamp, loggedInUserId);
            List<GroupMessageDelivery> deliveriesToSync = MyDatabase.getInstance(ChatActivity.this).groupMessageDeliveryDao().findAllToSync(lastSyncTimestamp, loggedInUserId);

            SyncRequest syncRequest = new SyncRequest();
            syncRequest.setLastSyncTimestamp(lastSyncTimestamp);
            if (privateMessagesToSync != null && !privateMessagesToSync.isEmpty()) {
                syncRequest.setPrivateMessagesToSync(privateMessagesToSync);
            }
            if (deliveriesToSync != null && !deliveriesToSync.isEmpty()) {
                syncRequest.setDeliveriesToSync(deliveriesToSync);
            }

            GenericMessage genericMessage = new GenericMessage();
            genericMessage.setSyncRequest(syncRequest);
            getWebSocketClient().send(new Gson().toJson(genericMessage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveContact(Contact contact) {
        ContactRepository contactRepository = new ContactRepository(getApplication());
        contactRepository.addContactSilently(ChatActivity.this, contact);
    }

    private void saveChatGroup(ChatGroup chatGroup, List<ChatGroupMember> chatGroupMembers) {
        ChatGroupRepository chatGroupRepository = new ChatGroupRepository(getApplication());
        chatGroupRepository.addChatGroupSilently(ChatActivity.this, chatGroup, chatGroupMembers);
    }

    private void savePrivateMessage(MessagePrivate messagePrivate) {
        PrivateMessageRepository repository = new PrivateMessageRepository(getApplication());
        repository.updateMessageReceived(ChatActivity.this, messagePrivate);
    }

    private void saveGroupMessage(MessageGroup messageGroup, List<GroupMessageDelivery> deliveries) {
        GroupMessageRepository repository = new GroupMessageRepository(getApplication());
        repository.saveMessage(ChatActivity.this, messageGroup, deliveries);
    }


    public void displayDotsDialog(String message) {
        dismissDotsDialog();
        dotsDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .build();

        dotsDialog.setMessage(message);

        if (!dotsDialog.isShowing()) {
            dotsDialog.show();
        }
    }

    public void dismissDotsDialog() {
        if (dotsDialog != null && dotsDialog.isShowing()) {
            dotsDialog.dismiss();
        }
    }

    public User getLoggedInUser() {
        if (loggedInUser != null) {
            return loggedInUser;
        } else {
            logOut();
            return null;
        }
    }

    public void logOut() {
        displayDotsDialog("Logging out");
        UserRepository repository = new UserRepository(getApplication());
        repository.logOut(ChatActivity.this);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle("");
        civToolbarProfilePic.setVisibility(View.GONE);
        tlbAutoCompeteTextView.setVisibility(View.GONE);
        txtToolbarTitle.setVisibility(View.VISIBLE);
        txtToolbarTitle.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle("");
        setTitle(getString(titleId));
    }

    public void setTitleWithImage(CharSequence title, String picUrl, int defaultImage) {
        tlbAutoCompeteTextView.setVisibility(View.GONE);
        civToolbarProfilePic.setVisibility(View.VISIBLE);
        txtToolbarTitle.setVisibility(View.VISIBLE);
        txtToolbarTitle.setText(title);

        Picasso.get()
                .load(RetrofitApiClient.IMAGE_BASE_URL + picUrl)
                .placeholder(defaultImage)
                .resize(200, 200)
                .centerCrop()
                .onlyScaleDown()
                .error(defaultImage)
                .into(civToolbarProfilePic);
    }

    public void displayToolbarSearch() {
        txtToolbarTitle.setVisibility(View.GONE);
        civToolbarProfilePic.setVisibility(View.GONE);
        tlbAutoCompeteTextView.setVisibility(View.VISIBLE);
    }

    public AutoCompleteTextView getTlbAutoCompeteTextView() {
        return tlbAutoCompeteTextView;
    }

    @Override
    protected void onDestroy() {
        dismissDotsDialog();
        unregisterReceiver(networkConnectivityReceiver);
        super.onDestroy();
    }

    //todo: potentially useless function
    private final BroadcastReceiver networkConnectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (Utils.isNetworkAvailable(context)) {
                    createWebSocketClient();
                }
            }
        }
    };

    public void updateSearchResults(Contact searchResult) {
        displayDotsDialog("Adding contact");
        ContactRepository repository = new ContactRepository(getApplication());
        repository.addContact(this, searchResult);
    }

    public boolean isTablet() {
        boolean xLarge = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);

        return (xLarge || large);
    }

    public boolean isXLargeTablet() {
        return ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
    }

    private void updateFirebaseMessageToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!Utils.stringNullOrEmpty(token)) {
                new UserRepository(getApplication()).updateFCMToken(this, token);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        createWebSocketClient();
    }
}