package com.shamine.teamsmessagingapp.room.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity
public class MessageGroup extends Message
{
    @ColumnInfo(index = true)
    private int chatGroupId;

    @ColumnInfo(index = true)
    private int senderId;

    private String senderName;

    public int getChatGroupId()
    {
        return chatGroupId;
    }

    public void setChatGroupId(int chatGroupId)
    {
        this.chatGroupId = chatGroupId;
    }

    public int getSenderId()
    {
        return senderId;
    }

    public void setSenderId(int senderId)
    {
        this.senderId = senderId;
    }

    public String getSenderName()
    {
        return senderName;
    }

    public void setSenderName(String senderName)
    {
        this.senderName = senderName;
    }
}
