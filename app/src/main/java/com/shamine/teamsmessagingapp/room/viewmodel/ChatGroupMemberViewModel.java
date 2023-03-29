package com.shamine.teamsmessagingapp.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.shamine.teamsmessagingapp.room.entities.ChatGroupMember;
import com.shamine.teamsmessagingapp.room.repositories.ChatGroupMemberRepository;

import java.util.List;

public class ChatGroupMemberViewModel extends AndroidViewModel {
    private final LiveData<List<ChatGroupMember>> groupMembersByGroupIdLiveData;
    private final MutableLiveData<Integer> chatGroupIdMutableLiveData = new MutableLiveData<>();

    public ChatGroupMemberViewModel(@NonNull Application application) {
        super(application);
        ChatGroupMemberRepository repository = new ChatGroupMemberRepository(application);
        groupMembersByGroupIdLiveData = Transformations.switchMap(chatGroupIdMutableLiveData, repository::getAllByChatGroup);
    }

    public LiveData<List<ChatGroupMember>> getGroupMembersByGroupIdLiveData() {
        return groupMembersByGroupIdLiveData;
    }

    public void setGroupId(int groupId) {
        chatGroupIdMutableLiveData.setValue(groupId);
    }
}
