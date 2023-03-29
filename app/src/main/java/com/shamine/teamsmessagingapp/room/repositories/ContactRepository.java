package com.shamine.teamsmessagingapp.room.repositories;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.fragments.chats.ContactAddFragment;
import com.shamine.teamsmessagingapp.retrofit.RetrofitApiClient;
import com.shamine.teamsmessagingapp.retrofit.RetrofitApiInterface;
import com.shamine.teamsmessagingapp.retrofit.responses.ContactSearchResponse;
import com.shamine.teamsmessagingapp.retrofit.responses.ContactsRetResponse;
import com.shamine.teamsmessagingapp.retrofit.responses.SenderContactRetResponse;
import com.shamine.teamsmessagingapp.retrofit.responses.SenderContactSearchResponse;
import com.shamine.teamsmessagingapp.room.MyDatabase;
import com.shamine.teamsmessagingapp.room.dao.ContactDao;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.utils.HttpCode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ContactRepository {
    private final ContactDao contactDao;

    public ContactRepository(Application application) {
        this.contactDao = MyDatabase.getInstance(application).contactDao();
    }

    public LiveData<List<Contact>> getContactsLiveData(int loggedInUserId) {
        return contactDao.getContactsLiveData(loggedInUserId);
    }

    public LiveData<List<Contact>> getContactsExcludeGroupIdLiveData(int groupId, int loggedInUserId) {
        return contactDao.getContactsExcludeGroupIdLiveData(groupId, loggedInUserId);
    }

    public void searchContacts(ChatActivity chatActivity, ContactAddFragment contactAddFragment, String searchText) {
        new Thread() {
            @Override
            public void run() {
                final String ERR_RESPONSE = "Contact search failed, please try again later";
                ContactsRetResponse retResponse;
                try {
                    String token = chatActivity.getLoggedInUser().getToken();
                    Call<ContactSearchResponse> call = RetrofitApiClient.getApiClient()
                            .create(RetrofitApiInterface.class)
                            .searchContact(RetrofitApiClient.TOKEN_PREFIX + token, searchText);

                    Response<ContactSearchResponse> response = call.execute();
                    ContactSearchResponse r = response.body();

                    if (response.isSuccessful() && r != null) {
                        ContactsRetResponse contactsRetResponse = new ContactsRetResponse(r.getResponse().getMessage(), true, response.code());
                        contactsRetResponse.setSearchResult(r.getSearchResult() != null ? r.getSearchResult() : new ArrayList<>());
                        retResponse = contactsRetResponse;
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                ContactSearchResponse errorResponse = new Gson().fromJson(response.errorBody().string(), ContactSearchResponse.class);
                                retResponse = new ContactsRetResponse(errorResponse.getResponse().getMessage(), false, response.code());
                            } else {
                                retResponse = new ContactsRetResponse(ERR_RESPONSE, false, response.code());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            retResponse = new ContactsRetResponse(ERR_RESPONSE, false, response.code());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    retResponse = new ContactsRetResponse(ERR_RESPONSE, false, 500);
                }

                ContactsRetResponse finalRetResponse = retResponse;
                chatActivity.runOnUiThread(() -> {
                    chatActivity.dismissDotsDialog();

                    if (finalRetResponse.isSuccessful()) {
                        contactAddFragment.updateSearchResults(finalRetResponse.getSearchResult());
                    } else if (finalRetResponse.getStatusCode() == HttpCode.UNAUTHORIZED.getCode()) {
                        Toast.makeText(chatActivity, "Unauthorized", Toast.LENGTH_LONG).show();
                        chatActivity.logOut();
                    } else {
                        Toast.makeText(chatActivity, finalRetResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.start();
    }

    public void searchSender(ChatActivity chatActivity, int searchId) {
        new Thread() {
            @Override
            public void run() {

                final String ERR_RESPONSE = "Contact search failed, please try again later";
                SenderContactRetResponse retResponse;
                try {
                    String token = chatActivity.getLoggedInUser().getToken();
                    Call<SenderContactSearchResponse> call = RetrofitApiClient.getApiClient()
                            .create(RetrofitApiInterface.class)
                            .searchSender(RetrofitApiClient.TOKEN_PREFIX + token, String.valueOf(searchId));

                    Response<SenderContactSearchResponse> response = call.execute();
                    SenderContactSearchResponse r = response.body();

                    if (response.isSuccessful() && r != null) {
                        SenderContactRetResponse contactsRetResponse = new SenderContactRetResponse(r.getResponse().getMessage(), true, response.code());
                        contactsRetResponse.setSearchResult(r.getSearchResult() != null ? r.getSearchResult() : new Contact());
                        retResponse = contactsRetResponse;
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                ContactSearchResponse errorResponse = new Gson().fromJson(response.errorBody().string(), ContactSearchResponse.class);
                                retResponse = new SenderContactRetResponse(errorResponse.getResponse().getMessage(), false, response.code());
                            } else {
                                retResponse = new SenderContactRetResponse(ERR_RESPONSE, false, response.code());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            retResponse = new SenderContactRetResponse(ERR_RESPONSE, false, response.code());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    retResponse = new SenderContactRetResponse(ERR_RESPONSE, false, 500);
                }

                SenderContactRetResponse finalRetResponse = retResponse;
                chatActivity.runOnUiThread(() -> {
                    chatActivity.dismissDotsDialog();

                    if (finalRetResponse.isSuccessful()) {
                        chatActivity.updateSearchResults(finalRetResponse.getSearchResult());
                    } else if (finalRetResponse.getStatusCode() == HttpCode.UNAUTHORIZED.getCode()) {
                        Toast.makeText(chatActivity, "Unauthorized", Toast.LENGTH_LONG).show();
                        chatActivity.logOut();
                    } else {
                        Toast.makeText(chatActivity, finalRetResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.start();
    }

    public void addContact(ChatActivity chatActivity, Contact contact) {
        new Thread() {
            @Override
            public void run() {
                contact.setLoggedInUserId(chatActivity.getLoggedInUser().getUserId());
                contactDao.add(contact);

                chatActivity.runOnUiThread(() -> {
                    chatActivity.dismissDotsDialog();
                    chatActivity.getOnBackPressedDispatcher().onBackPressed();
                });
            }
        }.start();
    }

    public void addContactSilently(ChatActivity chatActivity, Contact contact) {
        new Thread() {
            @Override
            public void run() {
                contact.setLoggedInUserId(chatActivity.getLoggedInUser().getUserId());
                contactDao.add(contact);
            }
        }.start();
    }

    public void deleteContact(ChatActivity chatActivity, int userId) {
        new Thread() {
            @Override
            public void run() {
                contactDao.delete(userId);
                chatActivity.runOnUiThread(chatActivity::dismissDotsDialog);

            }
        }.start();
    }
}
