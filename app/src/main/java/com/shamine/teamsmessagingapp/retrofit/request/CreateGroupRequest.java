package com.shamine.teamsmessagingapp.retrofit.request;

import java.util.List;

public class CreateGroupRequest
{
    private final String groupTitle;
    private final List<Integer> memberIds;

    public CreateGroupRequest(String groupTitle, List<Integer> memberIds)
    {
        this.groupTitle = groupTitle;
        this.memberIds = memberIds;
    }

    public String getGroupTitle()
    {
        return groupTitle;
    }

    public List<Integer> getMemberIds()
    {
        return memberIds;
    }
}
