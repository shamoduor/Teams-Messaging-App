package com.shamine.teamsmessagingapp.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.shamine.teamsmessagingapp.room.entities.BackupResource;

@Dao
public interface BackupResourceDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(BackupResource backupResource);
}
