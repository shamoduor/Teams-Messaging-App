package com.shamine.teamsmessagingapp.retrofit.responses;

import com.shamine.teamsmessagingapp.room.entities.ChatGroup;
import com.shamine.teamsmessagingapp.room.entities.ChatGroupMember;

import java.util.List;

public class ChatGroupResponse
{
    private ChatGroup chatGroup;
    private List<ChatGroupMember> chatGroupMembers;
    private ResponseDto response;

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

    public ResponseDto getResponse() {
        return response;
    }

    public void setResponse(ResponseDto response) {
        this.response = response;
    }
}
