package com.shamine.teamsmessagingapp.retrofit.responses;

import com.shamine.teamsmessagingapp.room.entities.Contact;

public class SenderContactSearchResponse {

    private Contact searchResult;
    private ResponseDto response;

    public Contact getSearchResult()
    {
        return searchResult;
    }

    public void setSearchResult(Contact searchResult)
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
