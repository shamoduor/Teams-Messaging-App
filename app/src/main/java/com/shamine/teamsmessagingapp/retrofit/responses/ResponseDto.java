package com.shamine.teamsmessagingapp.retrofit.responses;

public class ResponseDto {
    private final String title;
    private final String message;

    public ResponseDto(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
