package com.newey.crowdstreetexercise.dto;

public class RequestDto {
    private String body;
    private String callback;

    public RequestDto() { }

    public RequestDto(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }
}
