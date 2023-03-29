package com.shamine.teamsmessagingapp.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.shamine.teamsmessagingapp.room.entities.MessagePrivate;

import java.util.List;

@Dao
public interface MessagePrivateDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(MessagePrivate messagePrivate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(List<MessagePrivate> messagePrivateList);

    @Query("SELECT * FROM MessagePrivate WHERE messageId = :messageId LIMIT 1")
    MessagePrivate getMessage(int messageId);

    @Query("SELECT * FROM MessagePrivate WHERE (senderId = :party1Id AND recipientId = :party2Id) " +
            "OR (senderId = :party2Id AND recipientId = :party1Id) ORDER BY createdOn ASC")
    LiveData<List<MessagePrivate>> getPrivateMessagesLiveData(int party1Id, int party2Id);

    @Query("SELECT * FROM MessagePrivate WHERE senderId = :contactId AND readOn = null")
    List<MessagePrivate> getUnreadMessages(int contactId);

    @Query("SELECT * FROM MessagePrivate WHERE receivedOn = null")
    LiveData<List<MessagePrivate>> getUnreceivedMessages();


    @Query("SELECT * FROM MessagePrivate WHERE receivedOn = null")
    List<MessagePrivate> getUnreceivedMessageList();

    @Query("SELECT * FROM MessagePrivate WHERE (readOn >= :lastSyncTimestamp OR receivedOn >= :lastSyncTimestamp) " +
            "AND recipientId = :loggedInUserId")
    List<MessagePrivate> findAllToSync(long lastSyncTimestamp, int loggedInUserId);
}
