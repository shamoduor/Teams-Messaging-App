package com.shamine.teamsmessagingapp.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.shamine.teamsmessagingapp.room.entities.ChatGroup;
import com.shamine.teamsmessagingapp.room.views.ContactGroupDbView;

import java.util.List;

@Dao
public interface ChatGroupDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(List<ChatGroup> chatGroups);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(ChatGroup chatGroup);

    @Transaction
    @Query("SELECT * FROM ContactGroupDbView WHERE userId = (SELECT userId FROM User WHERE token IS NOT NULL LIMIT 1)")
    LiveData<ContactGroupDbView> getUserContactsAndGroupsLiveData();

    @Query("SELECT * FROM ChatGroup WHERE chatGroupId = :groupId")
    LiveData<ChatGroup> findChatGroupByIdLiveData(int groupId);

    @Query("SELECT * FROM ChatGroup WHERE chatGroupId = :groupId")
    ChatGroup findByChatGroupId(int groupId);
}
