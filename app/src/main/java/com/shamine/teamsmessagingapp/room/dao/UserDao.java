package com.shamine.teamsmessagingapp.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.shamine.teamsmessagingapp.room.entities.User;

@Dao
public abstract class UserDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void add(User user);

    @Query("DELETE FROM User")
    public abstract void empty();

    @Query("UPDATE User SET picUrl = :picUrl WHERE userId = :userId")
    public abstract void updatePicUrl(int userId, String picUrl);

    @Query("UPDATE User SET name = :name WHERE userId = :userId")
    public abstract void updateDisplayName(int userId, String name);

    @Query("UPDATE User SET username = :username WHERE userId = :userId")
    public abstract void updateUsername(int userId, String username);

    @Query("SELECT * FROM User WHERE token IS NOT NULL LIMIT 1")
    public abstract LiveData<User> getLoggedInUserLiveData();

    @Transaction
    public void emptyThenInsert(User user)
    {
        empty();
        add(user);
    }
}
