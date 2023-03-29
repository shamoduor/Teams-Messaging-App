package com.shamine.teamsmessagingapp.room.views;

import androidx.room.DatabaseView;
import androidx.room.Embedded;
import androidx.room.Relation;

import com.shamine.teamsmessagingapp.room.entities.ChatGroup;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.entities.User;

import java.util.ArrayList;
import java.util.List;

@DatabaseView("SELECT * FROM User WHERE token IS NOT NULL LIMIT 1")
public class ContactGroupDbView {
    @Embedded
    private User user;

    @Relation(parentColumn = "userId", entityColumn = "loggedInUserId", entity = Contact.class)
    private List<ContactView> contacts;

    @Relation(parentColumn = "userId", entityColumn = "loggedInUserId", entity = ChatGroup.class)
    private List<ChatGroupView> chatGroups;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ContactView> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactView> contacts) {
        this.contacts = contacts;
    }

    public List<ChatGroupView> getChatGroups() {
        List<ChatGroupView> chatGroupViews = new ArrayList<>();
        for (ChatGroupView cv : chatGroups) {
            if (cv.getChatGroup() != null && cv.getChatGroup().isAvailable()) {
                chatGroupViews.add(cv);
            }
        }
        return chatGroupViews;
    }

    public void setChatGroups(List<ChatGroupView> chatGroups) {
        this.chatGroups = chatGroups;
    }
}
