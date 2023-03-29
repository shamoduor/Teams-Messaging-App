package com.shamine.teamsmessagingapp.room.models;

import java.util.List;

import com.shamine.teamsmessagingapp.room.entities.GroupMessageDelivery;
import com.shamine.teamsmessagingapp.room.entities.MessagePrivate;

public class SyncRequest
{
    private List<MessagePrivate> privateMessagesToSync;
    private List<GroupMessageDelivery> deliveriesToSync;
    private long lastSyncTimestamp;

    public List<MessagePrivate> getPrivateMessagesToSync()
    {
        return privateMessagesToSync;
    }

    public void setPrivateMessagesToSync(List<MessagePrivate> privateMessagesToSync)
    {
        this.privateMessagesToSync = privateMessagesToSync;
    }

    public List<GroupMessageDelivery> getDeliveriesToSync()
    {
        return deliveriesToSync;
    }

    public void setDeliveriesToSync(List<GroupMessageDelivery> deliveriesToSync)
    {
        this.deliveriesToSync = deliveriesToSync;
    }

    public long getLastSyncTimestamp()
    {
        return lastSyncTimestamp;
    }

    public void setLastSyncTimestamp(long lastSyncTimestamp)
    {
        this.lastSyncTimestamp = lastSyncTimestamp;
    }
}
