package com.shamine.teamsmessagingapp.retrofit.responses;

import com.shamine.teamsmessagingapp.room.entities.Contact;

import java.util.List;

public class ContactSearchResponse
{
    private List<Contact> searchResult;
    private ResponseDto response;

    public List<Contact> getSearchResult()
    {
        return searchResult;
    }

    public void setSearchResult(List<Contact> searchResult)
    {
        this.searchResult = searchResult;
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
