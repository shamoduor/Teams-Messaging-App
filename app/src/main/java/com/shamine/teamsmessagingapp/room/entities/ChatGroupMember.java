package com.shamine.teamsmessagingapp.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ChatGroupMember
{
    @PrimaryKey
    private int memberId;

    @ColumnInfo(index = true)
    private int chatGroupId;

    @ColumnInfo(index = true)
    private int userId;

    private long addedOn;
    private boolean available;
    private boolean isAdmin;
    private String fullName;
    private String username;
    private String picUrl;

    public int getMemberId()
    {
        return memberId;
    }

    public void setMemberId(int memberId)
    {
        this.memberId = memberId;
    }

    public int getChatGroupId()
    {
        return chatGroupId;
    }

    public void setChatGroupId(int chatGroupId)
    {
        this.chatGroupId = chatGroupId;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public long getAddedOn()
    {
        return addedOn;
    }

    public void setAddedOn(long addedOn)
    {
        this.addedOn = addedOn;
    }

    public boolean isAvailable()
    {
        return available;
    }

    public void setAvailable(boolean available)
    {
        this.available = available;
    }

    public boolean isAdmin()
    {
        return isAdmin;
    }

    public void setAdmin(boolean admin)
    {
        isAdmin = admin;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPicUrl()
    {
        return picUrl;
    }

    public void setPicUrl(String picUrl)
    {
        this.picUrl = picUrl;
    }
}
