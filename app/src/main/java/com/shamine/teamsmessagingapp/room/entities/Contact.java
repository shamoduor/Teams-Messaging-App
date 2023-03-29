package com.shamine.teamsmessagingapp.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Contact
{
    @PrimaryKey
    private int userId;

    private String name;
    private String username;
    private String picUrl;

    @ColumnInfo(index = true)
    private int loggedInUserId;

    @Ignore
    private boolean checked;

    @Ignore
    private long lastMessageTime;

    @Ignore
    private String lastMessage;

    @Ignore
    private int lastMessageSenderId;

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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

    public int getLoggedInUserId()
    {
        return loggedInUserId;
    }

    public void setLoggedInUserId(int loggedInUserId)
    {
        this.loggedInUserId = loggedInUserId;
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    public long getLastMessageTime()
    {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime)
    {
        this.lastMessageTime = lastMessageTime;
    }

    public String getLastMessage()
    {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage)
    {
        this.lastMessage = lastMessage;
    }

    public int getLastMessageSenderId()
    {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(int lastMessageSenderId)
    {
        this.lastMessageSenderId = lastMessageSenderId;
    }
}
