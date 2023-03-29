package com.shamine.teamsmessagingapp.retrofit.responses;

import com.shamine.teamsmessagingapp.room.entities.Contact;

import java.util.List;

public class ContactsRetResponse extends RetResponse
{
    private List<Contact> searchResult;

    public ContactsRetResponse(String message, boolean successful, int statusCode)
    {
        super(message, successful, statusCode);
    }

    public List<Contact> getSearchResult()
    {
        return searchResult;
    }

    public void setSearchResult(List<Contact> searchResult)
    {
        this.searchResult = searchResult;
    }
}
