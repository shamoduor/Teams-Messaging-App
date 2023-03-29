package com.shamine.teamsmessagingapp.retrofit.responses;

import com.shamine.teamsmessagingapp.room.entities.ChatGroup;
import com.shamine.teamsmessagingapp.room.entities.ChatGroupMember;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.entities.GroupMessageDelivery;
import com.shamine.teamsmessagingapp.room.entities.MessageGroup;
import com.shamine.teamsmessagingapp.room.entities.MessagePrivate;
import com.shamine.teamsmessagingapp.room.entities.User;

import java.util.List;

public class AuthResponse
{
    private User user;
    private ResponseDto response;
    private List<MessagePrivate> privateMessages;
    private List<MessageGroup> groupMessages;
    private List<GroupMessageDelivery> groupMessageDeliveries;
    private List<ChatGroup> chatGroups;
    private List<ChatGroupMember> chatGroupMembers;
    private List<Contact> contacts;

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public ResponseDto getResponse() {
        return response;
    }

    public void setResponse(ResponseDto response) {
        this.response = response;
    }

    public List<MessagePrivate> getPrivateMessages()
    {
        return privateMessages;
    }

    public void setPrivateMessages(List<MessagePrivate> privateMessages)
    {
        this.privateMessages = privateMessages;
    }

    public List<MessageGroup> getGroupMessages()
    {
        return groupMessages;
    }

    public void setGroupMessages(List<MessageGroup> groupMessages)
    {
        this.groupMessages = groupMessages;
    }

    public List<ChatGroup> getChatGroups()
    {
        return chatGroups;
    }

    public void setChatGroups(List<ChatGroup> chatGroups)
    {
        this.chatGroups = chatGroups;
    }

    public List<ChatGroupMember> getChatGroupMembers()
    {
        return chatGroupMembers;
    }

    public void setChatGroupMembers(List<ChatGroupMember> chatGroupMembers)
    {
        this.chatGroupMembers = chatGroupMembers;
    }

    public List<Contact> getContacts()
    {
        return contacts;
    }

    public void setContacts(List<Contact> contacts)
    {
        this.contacts = contacts;
    }

    public List<GroupMessageDelivery> getGroupMessageDeliveries()
    {
        return groupMessageDeliveries;
    }

    public void setGroupMessageDeliveries(List<GroupMessageDelivery> groupMessageDeliveries)
    {
        this.groupMessageDeliveries = groupMessageDeliveries;
    }
}
