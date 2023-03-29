package com.shamine.teamsmessagingapp.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GroupMessageDelivery
{
    @PrimaryKey
    private int deliveryId;

    @ColumnInfo(index = true)
    private int messageGroupId;

    @ColumnInfo(index = true)
    private int chatGroupMemberId;

    @ColumnInfo(index = true)
    private int userId;

    private long receivedOn;

    private Long readOn;

    public int getDeliveryId()
    {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId)
    {
        this.deliveryId = deliveryId;
    }

    public int getMessageGroupId()
    {
        return messageGroupId;
    }

    public void setMessageGroupId(int messageGroupId)
    {
        this.messageGroupId = messageGroupId;
    }

    public int getChatGroupMemberId()
    {
        return chatGroupMemberId;
    }

    public void setChatGroupMemberId(int chatGroupMemberId)
    {
        this.chatGroupMemberId = chatGroupMemberId;
    }

    public long getReceivedOn()
    {
        return receivedOn;
    }

    public void setReceivedOn(long receivedOn)
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

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }
}
