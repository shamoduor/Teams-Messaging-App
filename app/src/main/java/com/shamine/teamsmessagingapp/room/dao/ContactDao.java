package com.shamine.teamsmessagingapp.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.shamine.teamsmessagingapp.room.entities.Contact;

import java.util.List;

@Dao
public interface ContactDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(Contact contact);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(List<Contact> contacts);

    @Query("SELECT * FROM Contact WHERE loggedInUserId = :loggedInUserId ORDER BY username ASC")
    LiveData<List<Contact>> getContactsLiveData(int loggedInUserId);

    @Query("SELECT c.* FROM Contact c WHERE " +
            "loggedInUserId = :loggedInUserId AND " +
            "c.userId NOT IN (SELECT m.userId FROM ChatGroupMember m WHERE m.chatGroupId = :groupId AND m.available = 1) " +
            "ORDER BY username ASC")
    LiveData<List<Contact>> getContactsExcludeGroupIdLiveData(int groupId, int loggedInUserId);

    @Query("SELECT * FROM Contact WHERE userId = :userId AND loggedInUserId = :loggedInUserId")
    Contact findByUserIdAndLoggedInUserId(int userId, int loggedInUserId);

    @Query("DELETE FROM Contact WHERE userId = :userId")
    void delete(int userId);
}
