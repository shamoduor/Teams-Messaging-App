package com.shamine.teamsmessagingapp.retrofit.responses;

import com.shamine.teamsmessagingapp.room.entities.ChatGroupMember;

public class RemoveGroupMemberResponse
{
    private ChatGroupMember removedMember;
    private ResponseDto response;

    public ChatGroupMember getRemovedMember()
    {
        return removedMember;
    }

    public void setRemovedMember(ChatGroupMember removedMember)
    {
        this.removedMember = removedMember;
    }

    public ResponseDto getResponse()
    {
        return response;
    }

    public void setResponse(ResponseDto response)
    {
        this.response = response;
    }
}
