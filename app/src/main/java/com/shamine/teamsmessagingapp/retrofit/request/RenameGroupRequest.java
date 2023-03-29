package com.shamine.teamsmessagingapp.retrofit.request;

public class RenameGroupRequest
{
    private final String title;
    private final int groupId;

    public RenameGroupRequest(String title, int groupId)
    {
        this.title = title;
        this.groupId = groupId;
    }

    public String getTitle()
    {
        return title;
    }

    public int getGroupId()
    {
        return groupId;
    }
}
