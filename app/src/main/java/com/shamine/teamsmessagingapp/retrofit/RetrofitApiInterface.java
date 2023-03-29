package com.shamine.teamsmessagingapp.retrofit;

import com.shamine.teamsmessagingapp.retrofit.request.AddGroupMembersRequest;
import com.shamine.teamsmessagingapp.retrofit.request.CreateGroupRequest;
import com.shamine.teamsmessagingapp.retrofit.request.RenameGroupRequest;
import com.shamine.teamsmessagingapp.retrofit.responses.AuthResponse;
import com.shamine.teamsmessagingapp.retrofit.responses.ChatGroupResponse;
import com.shamine.teamsmessagingapp.retrofit.responses.ContactSearchResponse;
import com.shamine.teamsmessagingapp.retrofit.responses.DeleteChatGroupResponse;
import com.shamine.teamsmessagingapp.retrofit.responses.RemoveGroupMemberResponse;
import com.shamine.teamsmessagingapp.retrofit.responses.ResponseDto;
import com.shamine.teamsmessagingapp.retrofit.responses.SenderContactSearchResponse;
import com.shamine.teamsmessagingapp.room.entities.User;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitApiInterface
{
    @POST("user/register")
    Call<AuthResponse> register(@Body User user);

    @POST("user/login")
    Call<AuthResponse> login(@Body User user);

    @POST("user/resetPassword")
    Call<AuthResponse> requestPasswordResetCode(@Body User user);

    @POST("otp")
    Call<ResponseDto> requestOTP(@Body User user);

    @PUT("user/resetPassword")
    Call<AuthResponse> changePassword(@Body User user);

    @Multipart
    @PUT("user/profilePic")
    Call<AuthResponse> changeProfilePicture(@Header("Authorization") String token, @Part MultipartBody.Part pic);

    @PUT("user/profileDetails")
    Call<AuthResponse> updateProfileDetails(@Header("Authorization") String token, @Body User request);

    @GET("user/contacts")
    Call<ContactSearchResponse> searchContact(@Header("Authorization") String token, @Query("searchText") String searchText);

    @GET("user/senderContact")
    Call<SenderContactSearchResponse> searchSender(@Header("Authorization") String token, @Query("searchId") String searchId);

    @PATCH("user")
    Call<ResponseDto> updateFCMToken(@Header("Authorization") String token, @Query("fcmToken") String fcmToken);

    @POST("group")
    Call<ChatGroupResponse> createChatGroup(@Header("Authorization") String token, @Body CreateGroupRequest request);

    @PUT("group")
    Call<ChatGroupResponse> renameChatGroup(@Header("Authorization") String token, @Body RenameGroupRequest request);

    @DELETE("group/{chatGroupId}")
    Call<DeleteChatGroupResponse> deleteChatGroup(@Header("Authorization") String token, @Path("chatGroupId") int chatGroupId);

    @POST("group/member")
    Call<ChatGroupResponse> addChatGroupMembers(@Header("Authorization") String token, @Body AddGroupMembersRequest request);

    @DELETE("group/member/{chatGroupMemberId}")
    Call<RemoveGroupMemberResponse> removeChatGroupMember(@Header("Authorization") String token, @Path("chatGroupMemberId") int chatGroupMemberId);

    @Multipart
    @PUT("group/{groupId}/pic")
    Call<ChatGroupResponse> changeGroupPicture(@Header("Authorization") String token, @Part MultipartBody.Part pic, @Path("groupId") int groupId);
}
