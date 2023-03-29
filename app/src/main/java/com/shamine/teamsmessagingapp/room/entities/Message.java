package com.shamine.teamsmessagingapp.room.entities;

import androidx.room.PrimaryKey;

public class Message
{
    @PrimaryKey
    private int messageId;

    private String content;

    private long createdOn;

    public int getMessageId()
    {
        return messageId;
    }

    public void setMessageId(int messageId)
    {
        this.messageId = messageId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public long getCreatedOn()
    {
        return createdOn;
    }

    public void setCreatedOn(long createdOn)
    {
        this.createdOn = createdOn;
    }
}
