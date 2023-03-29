package com.shamine.teamsmessagingapp.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class ChatGroup
{
    @PrimaryKey
    private int chatGroupId;

    private String title;

    @ColumnInfo(index = true)
    private int createdBy;

    private long createdOn;
    private boolean available;

    @ColumnInfo(index = true)
    private int loggedInUserId;

    private String picUrl;

    @Ignore
    private long lastMessageTime;

    @Ignore
    private String lastMessage;

    @Ignore
    private String lastMessageSenderName;

    @Ignore
    private int lastMessageSenderId;

    public int getChatGroupId()
    {
        return chatGroupId;
    }

    public void setChatGroupId(int chatGroupId)
    {
        this.chatGroupId = chatGroupId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy(int createdBy)
    {
        this.createdBy = createdBy;
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

    public int getLoggedInUserId()
    {
        return loggedInUserId;
    }

    public void setLoggedInUserId(int loggedInUserId)
    {
        this.loggedInUserId = loggedInUserId;
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
        return lastMessage != null ? lastMessage : "";
    }

    public void setLastMessage(String lastMessage)
    {
        this.lastMessage = lastMessage;
    }

    public String getPicUrl()
    {
        return picUrl;
    }

    public void setPicUrl(String picUrl)
    {
        this.picUrl = picUrl;
    }

    public String getLastMessageSenderName()
    {
        return lastMessageSenderName;
    }

    public void setLastMessageSenderName(String lastMessageSenderName)
    {
        this.lastMessageSenderName = lastMessageSenderName;
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
