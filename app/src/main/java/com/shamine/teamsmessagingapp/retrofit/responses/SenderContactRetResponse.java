package com.shamine.teamsmessagingapp.retrofit.responses;

import com.shamine.teamsmessagingapp.room.entities.Contact;

public class SenderContactRetResponse extends RetResponse
{
    private Contact searchResult;

    public SenderContactRetResponse(String message, boolean successful, int statusCode)
    {
        super(message, successful, statusCode);
    }

    public Contact getSearchResult()
    {
        return searchResult;
    }

    public void setSearchResult(Contact searchResult)
    {
        this.searchResult = searchResult;
    }
}