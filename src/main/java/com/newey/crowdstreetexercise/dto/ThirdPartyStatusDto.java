package com.newey.crowdstreetexercise.dto;

public class ThirdPartyStatusDto {
    public enum Status {
        PROCESSED, COMPLETED, ERROR
    }

    private Status status;
    private String detail;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
