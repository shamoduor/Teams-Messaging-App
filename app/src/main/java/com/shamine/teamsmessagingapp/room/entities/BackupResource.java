package com.shamine.teamsmessagingapp.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class BackupResource
{
    @PrimaryKey
    private int resourceId;

    private String storageUrl;

    @ColumnInfo(index = true)
    private int userId;

    private long createdOn;

    private boolean available;

    public int getResourceId()
    {
        return resourceId;
    }

    public void setResourceId(int resourceId)
    {
        this.resourceId = resourceId;
    }

    public String getStorageUrl()
    {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl)
    {
        this.storageUrl = storageUrl;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public long getCreatedOn()
    {
        return createdOn;
    }

    public void setCreatedOn(long createdOn)
    {
        this.createdOn = createdOn;
    }

    public boolean isAvailable()
    {
        return available;
    }

    public void setAvailable(boolean available)
    {
        this.available = available;
    }
}
