package com.shamine.teamsmessagingapp.room.views;

import androidx.room.DatabaseView;
import androidx.room.Embedded;
import androidx.room.Relation;

import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.entities.MessagePrivate;
import com.shamine.teamsmessagingapp.utils.Utils;

import java.util.List;

@DatabaseView("SELECT * FROM Contact")
public class ContactView {
    @Embedded
    private Contact contact;

    @Relation(parentColumn = "userId", entityColumn = "recipientId", entity = MessagePrivate.class)
    private List<MessagePrivate> sentPrivateMessages;

    @Relation(parentColumn = "userId", entityColumn = "senderId", entity = MessagePrivate.class)
    private List<MessagePrivate> receivedPrivateMessages;

    public Contact getContact() {
        long lastMessageTime = 0;
        MessagePrivate lastMessage = null;
        for (MessagePrivate message : sentPrivateMessages) {
            if (message.getCreatedOn() > lastMessageTime) {
                lastMessageTime = message.getCreatedOn();
                lastMessage = message;
            }
        }

        for (MessagePrivate message : receivedPrivateMessages) {
            if (message.getCreatedOn() > lastMessageTime) {
                lastMessageTime = message.getCreatedOn();
                lastMessage = message;
            }
        }

        String lastMessageString = "Click to start chatting";
        if (lastMessage != null) {
            lastMessageString = Utils.decrypt(lastMessage.getContent(), String.valueOf(lastMessage.getCreatedOn()),
                    String.valueOf(lastMessage.getSenderId()), String.valueOf(lastMessage.getRecipientId()));
            contact.setLastMessageSenderId(lastMessage.getSenderId());
            contact.setLastMessageTime(lastMessageTime);

        }
        contact.setLastMessage(lastMessageString);
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public List<MessagePrivate> getSentPrivateMessages() {
        return sentPrivateMessages;
    }

    public void setSentPrivateMessages(List<MessagePrivate> sentPrivateMessages) {
        this.sentPrivateMessages = sentPrivateMessages;
    }

    public List<MessagePrivate> getReceivedPrivateMessages() {
        return receivedPrivateMessages;
    }

    public void setReceivedPrivateMessages(List<MessagePrivate> receivedPrivateMessages) {
        this.receivedPrivateMessages = receivedPrivateMessages;
    }
}
