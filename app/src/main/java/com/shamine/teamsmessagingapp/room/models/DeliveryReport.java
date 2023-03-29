package com.shamine.teamsmessagingapp.room.models;

public class DeliveryReport {

    private final int sender;
    private final int messageId;
    private final long receivedOn;

    public DeliveryReport(int sender, int messageId, long receivedOn) {
        this.sender = sender;
        this.messageId = messageId;
        this.receivedOn = receivedOn;
    }

    public int getSender() {
        return sender;
    }

    public int getMessageId() {
        return messageId;
    }

    public long getReceivedOn() {
        return receivedOn;
    }
}
