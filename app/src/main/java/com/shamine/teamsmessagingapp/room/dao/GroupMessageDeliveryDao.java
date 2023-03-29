package com.shamine.teamsmessagingapp.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.shamine.teamsmessagingapp.room.entities.GroupMessageDelivery;

import java.util.List;

@Dao
public interface GroupMessageDeliveryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(GroupMessageDelivery messageDelivery);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(List<GroupMessageDelivery> messageDeliveries);

    @Query("SELECT * FROM GroupMessageDelivery WHERE messageGroupId = :messageId LIMIT 1")
    GroupMessageDelivery getGroupMessageDelivery(int messageId);

    @Query("SELECT * FROM GroupMessageDelivery WHERE userId = :userId AND messageGroupId = :messageGroupId LIMIT 1")
    GroupMessageDelivery findByUserIdAndMessageGroupId(int userId, int messageGroupId);

    @Query("SELECT * FROM GroupMessageDelivery WHERE (readOn >= :lastSyncTimestamp OR receivedOn >= :lastSyncTimestamp) " +
            "AND userId = :loggedInUserId")
    List<GroupMessageDelivery> findAllToSync(long lastSyncTimestamp, int loggedInUserId);
}
