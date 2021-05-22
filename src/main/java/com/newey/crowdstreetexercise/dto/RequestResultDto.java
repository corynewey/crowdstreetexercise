package com.newey.crowdstreetexercise.dto;

public class RequestResultDto {
    private String status;
    private Integer id;

    public RequestResultDto() {
    }

    public RequestResultDto(String status, Integer id) {
        this.status = status;
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
