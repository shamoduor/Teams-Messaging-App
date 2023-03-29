package com.shamine.teamsmessagingapp.room.repositories;

import android.app.Application;
import android.net.Uri;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.retrofit.RetrofitApiClient;
import com.shamine.teamsmessagingapp.retrofit.RetrofitApiInterface;
import com.shamine.teamsmessagingapp.retrofit.request.CreateGroupRequest;
import com.shamine.teamsmessagingapp.retrofit.request.RenameGroupRequest;
import com.shamine.teamsmessagingapp.retrofit.responses.ChatGroupResponse;
import com.shamine.teamsmessagingapp.retrofit.responses.DeleteChatGroupResponse;
import com.shamine.teamsmessagingapp.retrofit.responses.RetResponse;
import com.shamine.teamsmessagingapp.room.MyDatabase;
import com.shamine.teamsmessagingapp.room.dao.ChatGroupDao;
import com.shamine.teamsmessagingapp.room.dao.ChatGroupMemberDao;
import com.shamine.teamsmessagingapp.room.entities.ChatGroup;
import com.shamine.teamsmessagingapp.room.entities.ChatGroupMember;
import com.shamine.teamsmessagingapp.room.views.ContactGroupDbView;
import com.shamine.teamsmessagingapp.utils.FileUtils;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;

public class ChatGroupRepository {
    private final ChatGroupDao chatGroupDao;
    private final ChatGroupMemberDao chatGroupMemberDao;

    public ChatGroupRepository(Application application) {
        this.chatGroupDao = MyDatabase.getInstance(application).chatGroupDao();
        this.chatGroupMemberDao = MyDatabase.getInstance(application).chatGroupMemberDao();
    }

    public void createChatGroup(ChatActivity chatActivity, CreateGroupRequest request) {
        new Thread() {
            @Override
            public void run() {
                String ERR_RESPONSE = "Group creation failed, please try again later";
                RetResponse retResponse;
                try {
                    String token = chatActivity.getLoggedInUser().getToken();
                    Call<ChatGroupResponse> call = RetrofitApiClient.getApiClient()
                            .create(RetrofitApiInterface.class).createChatGroup(RetrofitApiClient.TOKEN_PREFIX + token, request);
                    Response<ChatGroupResponse> response = call.execute();
                    ChatGroupResponse r = response.body();

                    if (response.isSuccessful() && r != null) {
                        if (r.getChatGroup() != null) {
                            MyDatabase.getInstance(chatActivity).chatGroupDao().add(r.getChatGroup());
                        }

                        if (r.getChatGroupMembers() != null && !r.getChatGroupMembers().isEmpty()) {
                            MyDatabase.getInstance(chatActivity).chatGroupMemberDao().add(r.getChatGroupMembers());
                        }
                        retResponse = new RetResponse(r.getResponse().getMessage(), true, response.code());
                    } else if (!response.isSuccessful()) {
                        try {
                            if (response.errorBody() != null) {
                                ChatGroupResponse errorResponse = new Gson().fromJson(response.errorBody().string(), ChatGroupResponse.class);
                                retResponse = new RetResponse(errorResponse.getResponse().getMessage(), false, response.code());
                            } else {
                                retResponse = new RetResponse(ERR_RESPONSE, false, response.code());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            retResponse = new RetResponse(ERR_RESPONSE, false, response.code());
                        }
                    } else {
                        retResponse = new RetResponse(ERR_RESPONSE, false, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    retResponse = new RetResponse(ERR_RESPONSE, false, 500);
                }

                RetResponse finalRetResponse = retResponse;
                chatActivity.runOnUiThread(() -> {
                    chatActivity.dismissDotsDialog();
                    Toast.makeText(chatActivity, finalRetResponse.getMessage(), finalRetResponse.isSuccessful() ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
                    chatActivity.getOnBackPressedDispatcher().onBackPressed();
                });

            }
        }.start();
    }

    public LiveData<ContactGroupDbView> getUserContactsAndGroupsLiveData() {
        return chatGroupDao.getUserContactsAndGroupsLiveData();
    }

    public LiveData<ChatGroup> getChatGroupByIdLiveData(int groupId) {
        return chatGroupDao.findChatGroupByIdLiveData(groupId);
    }

    public void deleteGroup(ChatActivity chatActivity, int groupId) {
        new Thread() {
            @Override
            public void run() {
                chatActivity.runOnUiThread(() -> chatActivity.displayDotsDialog("Deleting group"));

                String ERR_RESPONSE = "Group deletion failed, please try again later";
                RetResponse retResponse;
                try {
                    String token = chatActivity.getLoggedInUser().getToken();
                    Call<DeleteChatGroupResponse> call = RetrofitApiClient.getApiClient()
                            .create(RetrofitApiInterface.class).deleteChatGroup(RetrofitApiClient.TOKEN_PREFIX + token, groupId);
                    Response<DeleteChatGroupResponse> response = call.execute();
                    DeleteChatGroupResponse r = response.body();

                    if (response.isSuccessful() && r != null) {
                        if (r.getDeletedGroup() != null) {
                            MyDatabase.getInstance(chatActivity).chatGroupDao().add(r.getDeletedGroup());
                            retResponse = new RetResponse(r.getResponse().getMessage(), true, response.code());
                        } else {
                            retResponse = new RetResponse(ERR_RESPONSE, false, response.code());
                        }

                    } else if (!response.isSuccessful()) {
                        try {
                            if (response.errorBody() != null) {
                                DeleteChatGroupResponse errorResponse = new Gson().fromJson(response.errorBody().string(), DeleteChatGroupResponse.class);
                                retResponse = new RetResponse(errorResponse.getResponse().getMessage(), false, response.code());
                            } else {
                                retResponse = new RetResponse(ERR_RESPONSE, false, response.code());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            retResponse = new RetResponse(ERR_RESPONSE, false, response.code());
                        }
                    } else {
                        retResponse = new RetResponse(ERR_RESPONSE, false, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    retResponse = new RetResponse(ERR_RESPONSE, false, 500);
                }

                RetResponse finalRetResponse = retResponse;
                chatActivity.runOnUiThread(() -> {
                    chatActivity.dismissDotsDialog();
                    Toast.makeText(chatActivity, finalRetResponse.getMessage(), finalRetResponse.isSuccessful() ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
                    if (finalRetResponse.isSuccessful()) {
                        Navigation.findNavController(chatActivity, R.id.navHostFragment).navigate(R.id.actionGroupInfoBackToHome);
                    }
                });
            }
        }.start();
    }

    public void renameGroup(ChatActivity chatActivity, RenameGroupRequest request) {
        new Thread() {
            @Override
            public void run() {
                chatActivity.runOnUiThread(() -> chatActivity.displayDotsDialog("Renaming group"));

                String ERR_RESPONSE = "Group title editing failed, please try again later";
                RetResponse retResponse;
                try {
                    String token = chatActivity.getLoggedInUser().getToken();
                    Call<ChatGroupResponse> call = RetrofitApiClient.getApiClient()
                            .create(RetrofitApiInterface.class).renameChatGroup(RetrofitApiClient.TOKEN_PREFIX + token, request);
                    Response<ChatGroupResponse> response = call.execute();

                    ChatGroupResponse r = response.body();

                    if (response.isSuccessful() && r != null) {
                        if (r.getChatGroup() != null) {
                            MyDatabase.getInstance(chatActivity).chatGroupDao().add(r.getChatGroup());
                        }

                        if (r.getChatGroupMembers() != null && !r.getChatGroupMembers().isEmpty()) {
                            MyDatabase.getInstance(chatActivity).chatGroupMemberDao().add(r.getChatGroupMembers());
                        }
                        retResponse = new RetResponse(r.getResponse().getMessage(), true, response.code());
                    } else if (!response.isSuccessful()) {
                        try {
                            if (response.errorBody() != null) {
                                ChatGroupResponse errorResponse = new Gson().fromJson(response.errorBody().string(), ChatGroupResponse.class);
                                retResponse = new RetResponse(errorResponse.getResponse().getMessage(), false, response.code());
                            } else {
                                retResponse = new RetResponse(ERR_RESPONSE, false, response.code());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            retResponse = new RetResponse(ERR_RESPONSE, false, response.code());
                        }
                    } else {
                        retResponse = new RetResponse(ERR_RESPONSE, false, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    retResponse = new RetResponse(ERR_RESPONSE, false, 500);
                }

                RetResponse finalRetResponse = retResponse;
                chatActivity.runOnUiThread(() -> {
                    chatActivity.dismissDotsDialog();
                    Toast.makeText(chatActivity, finalRetResponse.getMessage(), finalRetResponse.isSuccessful() ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
                });
            }
        }.start();
    }

    public void changeGroupPicture(ChatActivity chatActivity, Uri picUri, int groupId) {
        new Thread() {
            @Override
            public void run() {
                final String ERR_RESPONSE = "Group picture change failed, please try again later";
                RetResponse retResponse;
                try {
                    String token = chatActivity.getLoggedInUser().getToken();
                    MultipartBody.Part filePart = FileUtils.prepareFilePart(chatActivity, "pic", picUri);
                    Call<ChatGroupResponse> call = RetrofitApiClient.getApiClient()
                            .create(RetrofitApiInterface.class)
                            .changeGroupPicture(RetrofitApiClient.TOKEN_PREFIX + token, filePart, groupId);
                    Response<ChatGroupResponse> response = call.execute();
                    ChatGroupResponse r = response.body();

                    if (response.isSuccessful() && r != null) {
                        if (r.getChatGroup() != null) {
                            MyDatabase.getInstance(chatActivity).chatGroupDao().add(r.getChatGroup());
                        }

                        if (r.getChatGroupMembers() != null && !r.getChatGroupMembers().isEmpty()) {
                            MyDatabase.getInstance(chatActivity).chatGroupMemberDao().add(r.getChatGroupMembers());
                        }
                        retResponse = new RetResponse(r.getResponse().getMessage(), true, response.code());
                    } else if (!response.isSuccessful()) {
                        try {
                            if (response.errorBody() != null) {
                                ChatGroupResponse errorResponse = new Gson().fromJson(response.errorBody().string(), ChatGroupResponse.class);
                                retResponse = new RetResponse(errorResponse.getResponse().getMessage(), false, response.code());
                            } else {
                                retResponse = new RetResponse(ERR_RESPONSE, false, response.code());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            retResponse = new RetResponse(ERR_RESPONSE, false, response.code());
                        }
                    } else {
                        retResponse = new RetResponse(ERR_RESPONSE, false, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    retResponse = new RetResponse(ERR_RESPONSE, false, 500);
                }

                RetResponse finalRetResponse = retResponse;
                chatActivity.runOnUiThread(() -> {
                    chatActivity.dismissDotsDialog();
                    Toast.makeText(chatActivity, finalRetResponse.getMessage(), finalRetResponse.isSuccessful() ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
                });
            }

        }.start();
    }

    public void addChatGroupSilently(ChatActivity chatActivity, ChatGroup chatGroup, List<ChatGroupMember> chatGroupMembers) {
        new Thread() {
            @Override
            public void run() {
                try {
                    ChatGroup cg = chatGroupDao.findByChatGroupId(chatGroup.getChatGroupId());
                    if (cg == null ||
                            (cg.getPicUrl() != null && chatGroup.getPicUrl() != null && !cg.getPicUrl().equals(chatGroup.getPicUrl())) ||
                            cg.isAvailable() != chatGroup.isAvailable()) {
                        chatGroup.setLoggedInUserId(chatActivity.getLoggedInUser().getUserId());
                        chatGroupDao.add(chatGroup);
                    }
                    chatGroupMemberDao.add(chatGroupMembers);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
