package com.shamine.teamsmessagingapp.room.models;

import java.util.List;

import com.shamine.teamsmessagingapp.room.entities.ChatGroup;
import com.shamine.teamsmessagingapp.room.entities.ChatGroupMember;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.entities.GroupMessageDelivery;
import com.shamine.teamsmessagingapp.room.entities.MessageGroup;
import com.shamine.teamsmessagingapp.room.entities.MessagePrivate;

public class GenericMessage {

    private MessageGroup messageGroup;
    private MessagePrivate messagePrivate;
    private DeliveryReport deliveryReport;
    private GroupMessageDelivery groupMessageDelivery;
    private List<DeliveryReport> unReceivedReports;
    private List<GroupMessageDelivery> groupMessageDeliveries;
    private SyncRequest syncRequest;
    private Contact contact;
    private ChatGroup chatGroup;
    private List<ChatGroupMember> chatGroupMembers;

    public MessageGroup getMessageGroup() {
        return messageGroup;
    }

    public void setMessageGroup(MessageGroup messageGroup) {
        this.messageGroup = messageGroup;
    }

    public MessagePrivate getMessagePrivate() {
        return messagePrivate;
    }

    public void setMessagePrivate(MessagePrivate messagePrivate) {
        this.messagePrivate = messagePrivate;
    }

    public DeliveryReport getDeliveryReport() {
        return deliveryReport;
    }

    public void setDeliveryReport(DeliveryReport deliveryReport) {
        this.deliveryReport = deliveryReport;
    }

    public GroupMessageDelivery getGroupMessageDelivery() {
        return groupMessageDelivery;
    }

    public void setGroupMessageDelivery(GroupMessageDelivery groupMessageDelivery) {
        this.groupMessageDelivery = groupMessageDelivery;
    }

    public List<DeliveryReport> getUnReceivedReports() {
        return unReceivedReports;
    }

    public void setUnReceivedReports(List<DeliveryReport> unReceivedReports) {
        this.unReceivedReports = unReceivedReports;
    }

    public List<GroupMessageDelivery> getGroupMessageDeliveries()
    {
        return groupMessageDeliveries;
    }

    public void setGroupMessageDeliveries(List<GroupMessageDelivery> groupMessageDeliveries)
    {
        this.groupMessageDeliveries = groupMessageDeliveries;
    }

    public SyncRequest getSyncRequest()
    {
        return syncRequest;
    }

    public void setSyncRequest(SyncRequest syncRequest)
    {
        this.syncRequest = syncRequest;
    }

    public Contact getContact()
    {
        return contact;
    }

    public void setContact(Contact contact)
    {
        this.contact = contact;
    }

    public ChatGroup getChatGroup()
    {
        return chatGroup;
    }

    public void setChatGroup(ChatGroup chatGroup)
    {
        this.chatGroup = chatGroup;
    }

    public List<ChatGroupMember> getChatGroupMembers()
    {
        return chatGroupMembers;
    }

    public void setChatGroupMembers(List<ChatGroupMember> chatGroupMembers)
    {
        this.chatGroupMembers = chatGroupMembers;
    }
}
