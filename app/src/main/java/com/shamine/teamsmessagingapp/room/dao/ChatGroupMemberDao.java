package com.shamine.teamsmessagingapp.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.shamine.teamsmessagingapp.room.entities.ChatGroupMember;

import java.util.List;

@Dao
public interface ChatGroupMemberDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(ChatGroupMember chatGroupMember);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(List<ChatGroupMember> chatGroupMembers);

    @Query("SELECT * FROM ChatGroupMember WHERE chatGroupId = :groupId AND available = 1 ORDER BY isAdmin DESC, fullName ASC")
    LiveData<List<ChatGroupMember>> findAllByChatGroup(int groupId);
}
