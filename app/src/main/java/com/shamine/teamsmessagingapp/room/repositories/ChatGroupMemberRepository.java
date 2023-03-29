package com.shamine.teamsmessagingapp.room.repositories;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.retrofit.RetrofitApiClient;
import com.shamine.teamsmessagingapp.retrofit.RetrofitApiInterface;
import com.shamine.teamsmessagingapp.retrofit.request.AddGroupMembersRequest;
import com.shamine.teamsmessagingapp.retrofit.responses.ChatGroupResponse;
import com.shamine.teamsmessagingapp.retrofit.responses.RemoveGroupMemberResponse;
import com.shamine.teamsmessagingapp.retrofit.responses.RetResponse;
import com.shamine.teamsmessagingapp.room.MyDatabase;
import com.shamine.teamsmessagingapp.room.dao.ChatGroupMemberDao;
import com.shamine.teamsmessagingapp.room.entities.ChatGroupMember;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ChatGroupMemberRepository {
    private final ChatGroupMemberDao chatGroupMemberDao;

    public ChatGroupMemberRepository(Application application) {
        this.chatGroupMemberDao = MyDatabase.getInstance(application).chatGroupMemberDao();
    }

    public LiveData<List<ChatGroupMember>> getAllByChatGroup(int groupId) {
        return chatGroupMemberDao.findAllByChatGroup(groupId);
    }

    public void removeGroupMember(ChatActivity chatActivity, int chatGroupMemberId) {
        new Thread() {
            @Override
            public void run() {
                final String ERR_RESPONSE = "Group member removal failed, please try again later";
                RetResponse retResponse;
                try {
                    String token = chatActivity.getLoggedInUser().getToken();
                    Call<RemoveGroupMemberResponse> call = RetrofitApiClient.getApiClient()
                            .create(RetrofitApiInterface.class).removeChatGroupMember(RetrofitApiClient.TOKEN_PREFIX + token, chatGroupMemberId);
                    Response<RemoveGroupMemberResponse> response = call.execute();

                    RemoveGroupMemberResponse r = response.body();

                    if (response.isSuccessful() && r != null) {
                        MyDatabase.getInstance(chatActivity).chatGroupMemberDao().add(r.getRemovedMember());
                        retResponse = new RetResponse(r.getResponse().getMessage(), true, response.code());
                    } else if (!response.isSuccessful()) {
                        try {
                            if (response.errorBody() != null) {
                                RemoveGroupMemberResponse errorResponse = new Gson().fromJson(response.errorBody().string(), RemoveGroupMemberResponse.class);
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

    public void addChatGroupMembers(ChatActivity chatActivity, AddGroupMembersRequest request) {
        new Thread() {
            @Override
            public void run() {
                chatActivity.runOnUiThread(() -> chatActivity.displayDotsDialog("Adding group members"));

                final String ERR_RESPONSE = "Group member addition failed, please try again later";
                RetResponse retResponse;
                try {
                    String token = chatActivity.getLoggedInUser().getToken();
                    Call<ChatGroupResponse> call = RetrofitApiClient.getApiClient()
                            .create(RetrofitApiInterface.class).addChatGroupMembers(RetrofitApiClient.TOKEN_PREFIX + token, request);
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
}
