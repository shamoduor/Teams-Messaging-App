package com.shamine.teamsmessagingapp.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.repositories.ContactRepository;

import java.util.List;

public class ContactViewModel extends AndroidViewModel {
    private final LiveData<List<Contact>> contactsLiveData;
    private final LiveData<List<Contact>> contactsExcludeGroupIdLiveData;
    private final MutableLiveData<Integer> loggedInUserIdMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<GroupUser> groupUserMutableLiveData = new MutableLiveData<>();

    public ContactViewModel(@NonNull Application application) {
        super(application);
        ContactRepository repository = new ContactRepository(application);
        contactsLiveData = Transformations.switchMap(loggedInUserIdMutableLiveData, repository::getContactsLiveData);
        contactsExcludeGroupIdLiveData = Transformations.switchMap(groupUserMutableLiveData,
                (a -> repository.getContactsExcludeGroupIdLiveData(a.getGroupId(), a.getLoggedInUserId())));
    }

    public void setLoggedInUserId(int loggedInUserId) {
        loggedInUserIdMutableLiveData.setValue(loggedInUserId);
    }

    public void setGroupUser(int groupId, int loggedInUserId) {
        groupUserMutableLiveData.setValue(new GroupUser(groupId, loggedInUserId));
    }

    public LiveData<List<Contact>> getContactsLiveData() {
        return contactsLiveData;
    }

    public LiveData<List<Contact>> getContactsExcludeGroupIdLiveData() {
        return contactsExcludeGroupIdLiveData;
    }

    private static class GroupUser {
        private final int groupId;
        private final int loggedInUserId;

        public GroupUser(int groupId, int loggedInUserId) {
            this.groupId = groupId;
            this.loggedInUserId = loggedInUserId;
        }

        public int getGroupId() {
            return groupId;
        }

        public int getLoggedInUserId() {
            return loggedInUserId;
        }
    }
}
