package com.shamine.teamsmessagingapp.room.repositories;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.MainActivity;
import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.fragments.auth.ForgotPasswordFragment;
import com.shamine.teamsmessagingapp.retrofit.RetrofitApiClient;
import com.shamine.teamsmessagingapp.retrofit.RetrofitApiInterface;
import com.shamine.teamsmessagingapp.retrofit.responses.AuthResponse;
import com.shamine.teamsmessagingapp.retrofit.responses.ResponseDto;
import com.shamine.teamsmessagingapp.retrofit.responses.RetResponse;
import com.shamine.teamsmessagingapp.room.MyDatabase;
import com.shamine.teamsmessagingapp.room.dao.UserDao;
import com.shamine.teamsmessagingapp.room.entities.User;
import com.shamine.teamsmessagingapp.utils.FileUtils;
import com.shamine.teamsmessagingapp.utils.HttpCode;
import com.shamine.teamsmessagingapp.utils.TinyDb;
import com.shamine.teamsmessagingapp.utils.Utils;

import java.util.Date;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;

public class UserRepository {
    private final Application application;
    private final UserDao userDao;

    public UserRepository(Application application) {
        this.application = application;
        this.userDao = MyDatabase.getInstance(application).userDao();
    }

    public LiveData<User> getLoggedInUserLiveData() {
        return userDao.getLoggedInUserLiveData();
    }

    public void register(MainActivity mainActivity, String name, String username, String email, String password, int otpCode) {
        new Thread() {
            @Override
            public void run() {
                final String ERR_RESPONSE = "User registration failed, please try again later";
                RetResponse retResponse;
                try {
                    String authTime = String.valueOf(new Date().getTime());

                    User request = new User();
                    request.setAuthTime(authTime);
                    request.setName(Utils.authCrypt(name, authTime));
                    request.setUsername(Utils.authCrypt(username, authTime));
                    request.setEmail(Utils.authCrypt(email, authTime));
                    request.setPassword(Utils.authCrypt(password, authTime));
                    request.setOtpCode(otpCode);

                    Call<AuthResponse> call = RetrofitApiClient.getApiClient().create(RetrofitApiInterface.class).register(request);
                    Response<AuthResponse> response = call.execute();
                    AuthResponse r = response.body();

                    if (response.isSuccessful() && r != null) {
                        MyDatabase.getInstance(mainActivity).userDao().emptyThenInsert(r.getUser());
                        retResponse = new RetResponse("Welcome " + r.getUser().getName(), true, response.code());
                    } else if (!response.isSuccessful()) {
                        try {
                            if (response.errorBody() != null) {
                                ResponseDto errorResponse = new Gson().fromJson(response.errorBody().string(), ResponseDto.class);
                                retResponse = new RetResponse(errorResponse.getMessage(), false, response.code());
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
                    Log.d("MSG", e.getMessage() != null ? e.getMessage() : "null message");
                    e.printStackTrace();
                    retResponse = new RetResponse(ERR_RESPONSE, false, 500);
                }

                RetResponse finalRetResponse = retResponse;
                mainActivity.runOnUiThread(() -> {
                    mainActivity.dismissDotsDialog();

                    Toast.makeText(mainActivity, finalRetResponse.getMessage(), finalRetResponse.isSuccessful() ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();

                    if (finalRetResponse.isSuccessful()) {
                        mainActivity.toChatActivity();
                    }
                });
            }
        }.start();
    }

    public void login(MainActivity mainActivity, String email, String password) {
        new Thread() {
            @Override
            public void run() {
                final String ERR_RESPONSE = "Login failed, please try again later";
                RetResponse retResponse;
                try {
                    User request = new User();
                    request.setEmail(email);
                    request.setPassword(password);

                    Call<AuthResponse> call = RetrofitApiClient.getApiClient().create(RetrofitApiInterface.class).login(request);
                    Response<AuthResponse> response = call.execute();
                    AuthResponse r = response.body();

                    if (response.isSuccessful() && r != null) {
                        MyDatabase.getInstance(mainActivity).userDao().emptyThenInsert(r.getUser());
                        if (r.getPrivateMessages() != null) {
                            MyDatabase.getInstance(mainActivity).messagePrivateDao().add(r.getPrivateMessages());
                        }
                        if (r.getGroupMessages() != null) {
                            MyDatabase.getInstance(mainActivity).messageGroupDao().add(r.getGroupMessages());
                        }
                        if (r.getGroupMessageDeliveries() != null) {
                            MyDatabase.getInstance(mainActivity).groupMessageDeliveryDao().add(r.getGroupMessageDeliveries());
                        }
                        if (r.getChatGroups() != null) {
                            MyDatabase.getInstance(mainActivity).chatGroupDao().add(r.getChatGroups());
                        }
                        if (r.getChatGroupMembers() != null) {
                            MyDatabase.getInstance(mainActivity).chatGroupMemberDao().add(r.getChatGroupMembers());
                        }
                        if (r.getContacts() != null) {
                            MyDatabase.getInstance(mainActivity).contactDao().add(r.getContacts());
                        }
                        retResponse = new RetResponse("Welcome back " + r.getUser().getName(), true, response.code());
                    } else if (!response.isSuccessful()) {
                        try {
                            if (response.errorBody() != null) {
                                ResponseDto errorResponse = new Gson().fromJson(response.errorBody().string(), ResponseDto.class);
                                retResponse = new RetResponse(errorResponse.getMessage(), false, response.code());
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
                mainActivity.runOnUiThread(() -> {
                    mainActivity.dismissDotsDialog();

                    Toast.makeText(mainActivity, finalRetResponse.getMessage(), finalRetResponse.isSuccessful() ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();

                    if (finalRetResponse.isSuccessful()) {
                        mainActivity.toChatActivity();
                    }
                });
            }
        }.start();
    }

    public void requestPasswordRestCode(MainActivity mainActivity, ForgotPasswordFragment forgotPasswordFragment, String email) {
        new Thread() {
            @Override
            public void run() {
                final String ERR_RESPONSE = "Password reset failed, please try again later";
                RetResponse retResponse;
                try {
                    User request = new User();
                    request.setEmail(email);

                    Call<AuthResponse> call = RetrofitApiClient.getApiClient().create(RetrofitApiInterface.class)
                            .requestPasswordResetCode(request);
                    Response<AuthResponse> response = call.execute();
                    AuthResponse r = response.body();

                    if (response.isSuccessful() && r != null) {
                        retResponse = new RetResponse(r.getResponse().getMessage(), true, response.code());
                    } else if (!response.isSuccessful()) {
                        try {
                            if (response.errorBody() != null) {
                                AuthResponse errorResponse = new Gson().fromJson(response.errorBody().string(), AuthResponse.class);
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
                    Log.d("MSG", e.getMessage() != null ? e.getMessage() : "null message");
                    e.printStackTrace();
                    retResponse = new RetResponse(ERR_RESPONSE, false, 500);
                }

                RetResponse finalRetResponse = retResponse;
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.dismissDotsDialog();

                        Toast.makeText(mainActivity, finalRetResponse.getMessage(), Toast.LENGTH_LONG).show();

                        if (finalRetResponse.isSuccessful()) {
                            forgotPasswordFragment.flipPasswordResetRequest();
                        }
                    }
                });
            }
        }.start();
    }

    public void requestOTP(MainActivity mainActivity, String email, Bundle bundle, boolean goToOTPFragment) {
        new Thread() {
            @Override
            public void run() {
                final String ERR_RESPONSE = "Password reset failed, please try again later";
                RetResponse retResponse;
                try {
                    User request = new User();
                    request.setEmail(email);

                    Call<ResponseDto> call = RetrofitApiClient.getApiClient().create(RetrofitApiInterface.class)
                            .requestOTP(request);
                    Response<ResponseDto> response = call.execute();
                    ResponseDto r = response.body();

                    if (response.isSuccessful() && r != null) {
                        retResponse = new RetResponse(r.getMessage(), true, response.code());
                    } else if (!response.isSuccessful()) {
                        try {
                            if (response.errorBody() != null) {
                                RetResponse errorResponse = new Gson().fromJson(response.errorBody().string(), RetResponse.class);
                                retResponse = new RetResponse(errorResponse.getMessage(), false, response.code());
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
                    Log.d("MSG", e.getMessage() != null ? e.getMessage() : "null message");
                    e.printStackTrace();
                    retResponse = new RetResponse(ERR_RESPONSE, false, 500);
                }

                RetResponse finalRetResponse = retResponse;
                mainActivity.runOnUiThread(() -> {
                    mainActivity.dismissDotsDialog();

                    if (finalRetResponse.isSuccessful()) {
                        if (goToOTPFragment) {
                            Navigation.findNavController(mainActivity, R.id.navHostFragment).navigate(R.id.actionRegisterToOTP, bundle);
                        }
                    } else {
                        Toast.makeText(mainActivity, finalRetResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.start();
    }

    public void changePassword(MainActivity mainActivity, ForgotPasswordFragment forgotPasswordFragment, String email, int otpCode, String password) {
        new Thread() {
            @Override
            public void run() {
                final String ERR_RESPONSE = "Password reset failed, please try again later";
                RetResponse retResponse;
                try {
                    User request = new User();
                    request.setEmail(email);
                    request.setOtpCode(otpCode);
                    request.setPassword(password);

                    Call<AuthResponse> call = RetrofitApiClient.getApiClient().create(RetrofitApiInterface.class)
                            .changePassword(request);
                    Response<AuthResponse> response = call.execute();
                    AuthResponse r = response.body();

                    if (response.isSuccessful() && r != null) {
                        retResponse = new RetResponse(r.getResponse().getMessage(), true, response.code());
                    } else if (!response.isSuccessful()) {
                        try {
                            if (response.errorBody() != null) {
                                AuthResponse errorResponse = new Gson().fromJson(response.errorBody().string(), AuthResponse.class);
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
                    Log.d("MSG", e.getMessage() != null ? e.getMessage() : "null message");
                    e.printStackTrace();
                    retResponse = new RetResponse(ERR_RESPONSE, false, 500);
                }

                RetResponse finalRetResponse = retResponse;
                mainActivity.runOnUiThread(() -> {
                    mainActivity.dismissDotsDialog();

                    Toast.makeText(mainActivity, finalRetResponse.getMessage(), Toast.LENGTH_LONG).show();

                    if (finalRetResponse.isSuccessful()) {
                        forgotPasswordFragment.toLogin();
                    }
                });

            }
        }.start();
    }

    public void logOut(ChatActivity chatActivity) {
        new Thread() {
            @Override
            public void run() {
                MyDatabase.getInstance(chatActivity).clearAllTables();

                chatActivity.runOnUiThread(() -> {
                    chatActivity.dismissDotsDialog();
                    TinyDb.setLastUpdateTimestamp(chatActivity, 0);
                    Intent intent = new Intent(chatActivity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    chatActivity.startActivity(intent);
                    chatActivity.finish();
                });
            }
        }.start();
    }

    public void changeProfilePicture(ChatActivity chatActivity, Uri picUri) {
        new Thread() {
            @Override
            public void run() {
                final String ERR_RESPONSE = "Profile picture change failed, please try again later";
                RetResponse retResponse;
                try {
                    String token = chatActivity.getLoggedInUser().getToken();
                    MultipartBody.Part filePart = FileUtils.prepareFilePart(chatActivity, "pic", picUri);
                    Call<AuthResponse> call = RetrofitApiClient.getApiClient()
                            .create(RetrofitApiInterface.class)
                            .changeProfilePicture(RetrofitApiClient.TOKEN_PREFIX + token, filePart);
                    Response<AuthResponse> response = call.execute();
                    AuthResponse r = response.body();

                    if (response.isSuccessful() && r != null) {
                        MyDatabase.getInstance(chatActivity).userDao().updatePicUrl(r.getUser().getUserId(), r.getUser().getPicUrl());
                        retResponse = new RetResponse(r.getResponse().getMessage(), true, response.code());
                    } else if (!response.isSuccessful()) {
                        try {
                            if (response.errorBody() != null) {
                                AuthResponse errorResponse = new Gson().fromJson(response.errorBody().string(), AuthResponse.class);
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

                    if (finalRetResponse.getStatusCode() == HttpCode.UNAUTHORIZED.getCode()) {
                        Toast.makeText(chatActivity, "Unauthorized", Toast.LENGTH_LONG).show();
                        chatActivity.logOut();
                    } else {
                        Toast.makeText(chatActivity, finalRetResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.start();
    }

    public void updateProfileDetails(ChatActivity chatActivity, String displayName, String username) {
        new Thread() {
            @Override
            public void run() {
                chatActivity.runOnUiThread(() -> {
                    final String ERR_RESPONSE = "Profile update failed, please try again later";
                    RetResponse retResponse;
                    try {
                        String token = chatActivity.getLoggedInUser().getToken();
                        User request = new User();
                        request.setUsername(username);
                        request.setName(displayName);

                        Call<AuthResponse> call = RetrofitApiClient.getApiClient()
                                .create(RetrofitApiInterface.class)
                                .updateProfileDetails(RetrofitApiClient.TOKEN_PREFIX + token, request);
                        Response<AuthResponse> response = call.execute();
                        AuthResponse r = response.body();

                        if (response.isSuccessful() && r != null) {
                            UserDao userDao = MyDatabase.getInstance(chatActivity).userDao();
                            if (username != null) {
                                userDao.updateUsername(r.getUser().getUserId(), r.getUser().getUsername());
                            }
                            if (displayName != null) {
                                userDao.updateDisplayName(r.getUser().getUserId(), r.getUser().getName());
                            }
                            retResponse = new RetResponse(r.getResponse().getMessage(), true, response.code());
                        } else if (!response.isSuccessful()) {
                            try {
                                if (response.errorBody() != null) {
                                    AuthResponse errorResponse = new Gson().fromJson(response.errorBody().string(), AuthResponse.class);
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

                        if (finalRetResponse.getStatusCode() == HttpCode.UNAUTHORIZED.getCode()) {
                            Toast.makeText(chatActivity, "Unauthorized", Toast.LENGTH_LONG).show();
                            chatActivity.logOut();
                        } else {
                            Toast.makeText(chatActivity, finalRetResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                });
            }
        }.start();
    }

    public void updateFCMToken(ChatActivity chatActivity, String fcmToken) {
        String token = chatActivity.getLoggedInUser().getToken();
        new Thread() {
            @Override
            public void run() {
                try {
                    Call<ResponseDto> call = RetrofitApiClient.getApiClient()
                            .create(RetrofitApiInterface.class)
                            .updateFCMToken(RetrofitApiClient.TOKEN_PREFIX + token, fcmToken);

                    Response<ResponseDto> response = call.execute();

                    if (response.isSuccessful()) {
                        Log.d("FCM_TOKEN", "updated successfully");
                    } else {
                        Log.d("FCM_TOKEN", "updated failed");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
