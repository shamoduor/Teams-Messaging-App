package com.shamine.teamsmessagingapp.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.shamine.teamsmessagingapp.room.entities.ChatGroup;
import com.shamine.teamsmessagingapp.room.repositories.ChatGroupRepository;
import com.shamine.teamsmessagingapp.room.views.ContactGroupDbView;

public class ChatGroupViewModel extends AndroidViewModel {
    private final LiveData<ContactGroupDbView> userContactsAndGroupsLiveData;
    private final LiveData<ChatGroup> chatGroupByIdLiveData;
    private final MutableLiveData<Integer> chatGroupIdMutableLiveData = new MutableLiveData<>();

    public ChatGroupViewModel(@NonNull Application application) {
        super(application);
        ChatGroupRepository repository = new ChatGroupRepository(application);
        userContactsAndGroupsLiveData = repository.getUserContactsAndGroupsLiveData();
        chatGroupByIdLiveData = Transformations.switchMap(chatGroupIdMutableLiveData, repository::getChatGroupByIdLiveData);
    }

    public LiveData<ContactGroupDbView> getUserContactsAndGroupsLiveData() {
        return userContactsAndGroupsLiveData;
    }

    public LiveData<ChatGroup> getChatGroupByIdLiveData() {
        return chatGroupByIdLiveData;
    }

    public void setGroupId(int groupId) {
        chatGroupIdMutableLiveData.setValue(groupId);
    }
}
