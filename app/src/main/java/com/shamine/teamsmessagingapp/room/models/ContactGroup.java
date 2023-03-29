package com.shamine.teamsmessagingapp.room.models;

import com.shamine.teamsmessagingapp.room.views.ChatGroupView;
import com.shamine.teamsmessagingapp.room.views.ContactView;

public class ContactGroup {
    private final ContactView contactView;
    private final ChatGroupView chatGroupView;
    private final long lastMessageTime;
    private final int numNewMsgs;

    public ContactGroup(ContactView contactView, long lastMessageTime, int numNewMsgs) {
        this.contactView = contactView;
        this.chatGroupView = null;
        this.lastMessageTime = lastMessageTime;
        this.numNewMsgs = numNewMsgs;
    }

    public ContactGroup(ChatGroupView chatGroupView, long lastMessageTime, int numNewMsgs) {
        this.chatGroupView = chatGroupView;
        this.contactView = null;
        this.lastMessageTime = lastMessageTime;
        this.numNewMsgs = numNewMsgs;
    }

    public ContactView getContactView() {
        return contactView;
    }

    public ChatGroupView getChatGroupView() {
        return chatGroupView;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public int getNumNewMsgs() {
        return numNewMsgs;
    }

    public boolean isContact() {
        return contactView != null && contactView.getContact() != null;
    }

    public boolean isChatGroup() {
        return chatGroupView != null && chatGroupView.getChatGroup() != null;
    }
}
