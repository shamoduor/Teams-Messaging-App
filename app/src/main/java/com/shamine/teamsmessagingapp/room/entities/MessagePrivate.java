package com.shamine.teamsmessagingapp.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity
public class
MessagePrivate extends Message
{
    @ColumnInfo(index = true)
    private int recipientId;

    @ColumnInfo(index = true)
    private int senderId;

    private boolean availableForRecipient;

    private Long receivedOn;

    private Long readOn;

    public int getRecipientId()
    {
        return recipientId;
    }

    public void setRecipientId(int recipientId)
    {
        this.recipientId = recipientId;
    }

    public boolean isAvailableForRecipient()
    {
        return availableForRecipient;
    }

    public void setAvailableForRecipient(boolean availableForRecipient)
    {
        this.availableForRecipient = availableForRecipient;
    }

    public int getSenderId()
    {
        return senderId;
    }

    public void setSenderId(int senderId)
    {
        this.senderId = senderId;
    }

    public Long getReceivedOn()
    {
        return receivedOn;
    }

    public void setReceivedOn(Long receivedOn)
    {
        this.receivedOn = receivedOn;
    }

    public Long getReadOn()
    {
        return readOn;
    }

    public void setReadOn(Long readOn)
    {
        this.readOn = readOn;
    }
}
