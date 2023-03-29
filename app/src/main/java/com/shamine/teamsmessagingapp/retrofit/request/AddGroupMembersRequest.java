package com.shamine.teamsmessagingapp.retrofit.request;

import java.util.List;

public class AddGroupMembersRequest extends CreateGroupRequest
{
    private final int groupId;

    public AddGroupMembersRequest(int groupId, List<Integer> memberIds)
    {
        super("", memberIds);
        this.groupId = groupId;
    }

    public int getGroupId()
    {
        return groupId;
    }
}
