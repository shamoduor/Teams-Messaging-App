package com.shamine.teamsmessagingapp.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.shamine.teamsmessagingapp.room.entities.MessageGroup;

import java.util.List;

@Dao
public interface MessageGroupDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(MessageGroup messageGroup);

    @Query("SELECT * FROM MessageGroup WHERE messageId = :messageId LIMIT 1")
    MessageGroup getMessage(int messageId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(List<MessageGroup> messageGroupsList);

    @Query("SELECT DISTINCT g.* FROM MessageGroup g " +
            "INNER JOIN GroupMessageDelivery d ON d.messageGroupId = g.messageId " +
            "WHERE (g.senderId = :from OR d.userId = :from) AND g.chatGroupId = :chama ORDER BY g.createdOn ASC")
    LiveData<List<MessageGroup>> getGroupMessagesLiveData(int from, int chama);


    @Query("SELECT g.* FROM MessageGroup g " +
            "INNER JOIN GroupMessageDelivery d ON d.messageGroupId = g.messageId " +
            "WHERE g.chatGroupId = :chatGroupId AND readOn = null")
    List<MessageGroup> getUnreadMessages(int chatGroupId);
}
