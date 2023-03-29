package com.shamine.teamsmessagingapp.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.shamine.teamsmessagingapp.room.entities.MessageGroup;
import com.shamine.teamsmessagingapp.room.repositories.GroupMessageRepository;

import java.util.List;

public class GroupMessageViewModel extends AndroidViewModel {

    private final GroupMessageRepository repository;
    private final LiveData<List<MessageGroup>> groupMessagesLiveData;
    private final MutableLiveData<SenderGroup> senderGroupMutableLiveData = new MutableLiveData<>();

    public GroupMessageViewModel(@NonNull Application application) {
        super(application);
        repository = new GroupMessageRepository(application);
        groupMessagesLiveData = Transformations.switchMap(senderGroupMutableLiveData,
                (a -> repository.getGroupMessagesLiveData(a.getSenderId(), a.getGroupId())));
    }

    public LiveData<List<MessageGroup>> getGroupMessagesLiveData() {
        return groupMessagesLiveData;
    }

    public void setSenderIdAndGroupId(int senderId, int groupId) {
        senderGroupMutableLiveData.setValue(new SenderGroup(senderId, groupId));
    }

    public static class SenderGroup {
        private final int senderId;
        private final int GroupId;

        SenderGroup(int senderId, int GroupId) {
            this.senderId = senderId;
            this.GroupId = GroupId;
        }

        public int getSenderId() {
            return senderId;
        }

        public int getGroupId() {
            return GroupId;
        }
    }
}
