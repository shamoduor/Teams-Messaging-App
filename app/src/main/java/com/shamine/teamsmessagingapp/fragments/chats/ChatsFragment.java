package com.shamine.teamsmessagingapp.fragments.chats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.adapters.recyclerview.ChatAdapter;
import com.shamine.teamsmessagingapp.databinding.FragmentChatsBinding;
import com.shamine.teamsmessagingapp.room.entities.ChatGroup;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.entities.MessageGroup;
import com.shamine.teamsmessagingapp.room.entities.MessagePrivate;
import com.shamine.teamsmessagingapp.room.models.GenericContact;
import com.shamine.teamsmessagingapp.room.models.GenericMessage;
import com.shamine.teamsmessagingapp.room.viewmodel.ChatGroupViewModel;
import com.shamine.teamsmessagingapp.room.viewmodel.GroupMessageViewModel;
import com.shamine.teamsmessagingapp.room.viewmodel.PrivateMessageViewModel;
import com.shamine.teamsmessagingapp.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatsFragment extends Fragment {
    private ChatActivity chatActivity;
    public final static String CONTACT_KEY = "CONTACT_KEY";
    private ChatGroup currentChatGroup = null;

    private FragmentChatsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        chatActivity = (ChatActivity) requireActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(CONTACT_KEY)) {
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(chatActivity));
            ChatAdapter adapter = new ChatAdapter(chatActivity);
            binding.recyclerView.setAdapter(adapter);
            currentChatGroup = null;

            try {
                GenericContact genericContact = new Gson().fromJson(bundle.getString(CONTACT_KEY), GenericContact.class);

                if (genericContact.getContact() != null) {
                    Contact contact = genericContact.getContact();
                    chatActivity.setTitleWithImage(contact.getName(), contact.getPicUrl(), R.drawable.temp_user);

                    PrivateMessageViewModel messageViewModel = new ViewModelProvider(ChatsFragment.this).
                            get(PrivateMessageViewModel.class);

                    messageViewModel.setSenderIdAndRecipientId(chatActivity.getLoggedInUser().getUserId(), contact.getUserId());
                    messageViewModel.getPrivateMessagesLiveData().observe(getViewLifecycleOwner(), new Observer<List<MessagePrivate>>() {
                        @Override
                        public void onChanged(List<MessagePrivate> newPrivateMessages) {
                            if (newPrivateMessages != null && !newPrivateMessages.isEmpty()) {
                                if (adapter.getGenericMessageList().isEmpty()) {
                                    List<GenericMessage> messageList = new ArrayList<>();
                                    for (MessagePrivate mp : newPrivateMessages) {
                                        GenericMessage genericMessage = new GenericMessage();
                                        genericMessage.setMessagePrivate(mp);
                                        messageList.add(genericMessage);
                                    }
                                    adapter.getGenericMessageList().addAll(messageList);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    for (int a = 0; a < newPrivateMessages.size(); a++) {
                                        GenericMessage newMsg = new GenericMessage();
                                        newMsg.setMessagePrivate(newPrivateMessages.get(a));
                                        boolean add = true; //whether or not to add the new message got from db

                                        //for each message in list, check whether the new message matches it
                                        for (GenericMessage message : adapter.getGenericMessageList()) {
                                            //if a match is found break the loop
                                            if (message.getMessagePrivate().getMessageId() ==
                                                    newMsg.getMessagePrivate().getMessageId()) {
                                                add = false;
                                                break; //break loop
                                            }
                                        }

                                        //if a match was not found, add the message to the list
                                        if (add) {
                                            adapter.getGenericMessageList().add(newMsg);
                                            adapter.notifyItemInserted(adapter.getGenericMessageList().size() - 1);
                                        }
                                    }
                                }
                                if (!adapter.getGenericMessageList().isEmpty()) {
                                    binding.recyclerView.smoothScrollToPosition(adapter.getGenericMessageList().size() - 1);
                                }
                            }
                        }
                    });

                    binding.btnSend.setOnClickListener(view1 -> {
                        if (chatActivity.getWebSocketClient() != null) {
                            String message = binding.edtChat.getText().toString().trim();
                            if (!Utils.stringNullOrEmpty(message)) {
                                MessagePrivate messagePrivate = new MessagePrivate();
                                messagePrivate.setRecipientId(contact.getUserId());
                                messagePrivate.setCreatedOn(new Date().getTime());
                                messagePrivate.setContent(Utils.encrypt(message,
                                        String.valueOf(messagePrivate.getCreatedOn()),
                                        String.valueOf(chatActivity.getLoggedInUser().getUserId()),
                                        String.valueOf(messagePrivate.getRecipientId())));
                                if (adapter.getGenericMessageList().size() > 1) {
                                    binding.recyclerView.smoothScrollToPosition(adapter.getGenericMessageList().size() - 1);
                                }

                                GenericMessage gMessage = new GenericMessage();
                                gMessage.setMessagePrivate(messagePrivate);
                                chatActivity.getWebSocketClient().send(new Gson().toJson(gMessage));
                                binding.edtChat.setText("");
                            }
                        } else {
                            Toast.makeText(requireContext(), "Unable to initialize websocket. Please try again later.", Toast.LENGTH_SHORT).show();
                            chatActivity.createWebSocketClient();
                        }

                    });
                } else if (genericContact.getChatGroup() != null) {
                    setHasOptionsMenu(true);
                    currentChatGroup = genericContact.getChatGroup();
                    chatActivity.setTitleWithImage(currentChatGroup.getTitle(), currentChatGroup.getPicUrl(), R.drawable.ic_group);

                    GroupMessageViewModel messageViewModel = new ViewModelProvider(ChatsFragment.this).
                            get(GroupMessageViewModel.class);
                    ChatGroupViewModel chatGroupViewModel = new ViewModelProvider(ChatsFragment.this).get(ChatGroupViewModel.class);
                    messageViewModel.setSenderIdAndGroupId(chatActivity.getLoggedInUser().getUserId(), currentChatGroup.getChatGroupId());
                    messageViewModel.getGroupMessagesLiveData().observe(getViewLifecycleOwner(), new Observer<List<MessageGroup>>() {
                        @Override
                        public void onChanged(List<MessageGroup> newGroupMessages) {
                            if (newGroupMessages != null && !newGroupMessages.isEmpty()) {
                                if (adapter.getGenericMessageList().isEmpty()) {
                                    List<GenericMessage> messageList = new ArrayList<>();
                                    for (MessageGroup mg : newGroupMessages) {
                                        GenericMessage genericMessage = new GenericMessage();
                                        genericMessage.setMessageGroup(mg);
                                        messageList.add(genericMessage);
                                    }
                                    adapter.getGenericMessageList().addAll(messageList);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    for (int a = 0; a < newGroupMessages.size(); a++) {
                                        GenericMessage newMsg = new GenericMessage();
                                        newMsg.setMessageGroup(newGroupMessages.get(a));
                                        boolean add = true; //whether or not to add the new message got from db

                                        //for each message in list, check whether the new message matches it
                                        for (GenericMessage message : adapter.getGenericMessageList()) {
                                            //if a match is found break the loop
                                            if (message.getMessageGroup().getMessageId() == newMsg.getMessageGroup().getMessageId()) {
                                                add = false;
                                                break;
                                            }
                                        }
                                        //if a match was not found, add the message to the list
                                        if (add) {
                                            adapter.getGenericMessageList().add(newMsg);
                                            adapter.notifyItemInserted(adapter.getGenericMessageList().size() - 1);
                                        }
                                    }
                                }
                                if (!adapter.getGenericMessageList().isEmpty()) {
                                    binding.recyclerView.smoothScrollToPosition(adapter.getGenericMessageList().size() - 1);
                                }
                            }
                        }
                    });

                    chatGroupViewModel.getChatGroupByIdLiveData().observe(getViewLifecycleOwner(), new Observer<ChatGroup>() {
                        @Override
                        public void onChanged(ChatGroup chatGroup) {
                            currentChatGroup = chatGroup;
                            chatActivity.setTitleWithImage(currentChatGroup.getTitle(), currentChatGroup.getPicUrl(), R.drawable.ic_group);
                        }
                    });
                    chatGroupViewModel.setGroupId(currentChatGroup.getChatGroupId());

                    binding.btnSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String message = binding.edtChat.getText().toString().trim();
                            if (!Utils.stringNullOrEmpty(message)) {
                                MessageGroup messageGroup = new MessageGroup();
                                messageGroup.setChatGroupId(currentChatGroup.getChatGroupId());
                                messageGroup.setCreatedOn(new Date().getTime());
                                messageGroup.setContent(Utils.encrypt(message,
                                        String.valueOf(messageGroup.getCreatedOn()),
                                        String.valueOf(chatActivity.getLoggedInUser().getUserId()),
                                        String.valueOf(messageGroup.getChatGroupId())));
                                binding.edtChat.setText("");
                                Utils.dismissKeypad(chatActivity);
                                if (adapter.getGenericMessageList().size() > 1) {
                                    binding.recyclerView.smoothScrollToPosition(adapter.getGenericMessageList().size() - 1);
                                }

                                GenericMessage gMessage = new GenericMessage();
                                gMessage.setMessageGroup(messageGroup);
                                chatActivity.getWebSocketClient().send(new Gson().toJson(gMessage));
                            }
                        }
                    });
                } else {
                    chatActivity.onBackPressed();
                }
            } catch (Exception e) {
                e.printStackTrace();
                chatActivity.onBackPressed();
            }
        } else {
            chatActivity.onBackPressed();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat_group, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuInfo) {
            if (currentChatGroup != null) {
                Bundle bundle = new Bundle();
                bundle.putString(GroupInfoFragment.GROUP_KEY, new Gson().toJson(currentChatGroup));
                Navigation.findNavController(chatActivity, R.id.navHostFragment).navigate(R.id.actionChatsToGroupInfo, bundle);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
