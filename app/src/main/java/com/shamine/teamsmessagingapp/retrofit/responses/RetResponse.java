package com.shamine.teamsmessagingapp.retrofit.responses;

public class RetResponse
{
    private final String message;
    private final boolean successful;
    private final int statusCode;

    public RetResponse(String message, boolean successful, int statusCode)
    {
        this.message = message;
        this.successful = successful;
        this.statusCode = statusCode;
    }

    public String getMessage()
    {
        return message;
    }

    public boolean isSuccessful()
    {
        return successful;
    }

    public int getStatusCode()
    {
        return statusCode;
    }
}
