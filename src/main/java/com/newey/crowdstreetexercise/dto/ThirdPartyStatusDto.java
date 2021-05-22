package com.newey.crowdstreetexercise.dto;

public class ThirdPartyStatusDto {
    // By making the status an enum, we are guaranteeing that the status value will be one of the enum values. If not,
    // we'll throw an exception.
    public enum Status {
        STARTED, PROCESSED, COMPLETED, ERROR
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
