package com.shamine.teamsmessagingapp.retrofit.responses;

import com.shamine.teamsmessagingapp.room.entities.ChatGroup;

public class DeleteChatGroupResponse
{
    private ChatGroup deletedGroup;
    private ResponseDto response;

    public ChatGroup getDeletedGroup()
    {
        return deletedGroup;
    }

    public void setDeletedGroup(ChatGroup deletedGroup)
    {
        this.deletedGroup = deletedGroup;
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
