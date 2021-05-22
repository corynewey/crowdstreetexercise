package com.newey.crowdstreetexercise.dto;

import com.newey.crowdstreetexercise.persistence.entities.RequestEntity;

import java.time.OffsetDateTime;

public class StatusDto {
    private String body;
    private String status;
    private String detail;
    // This is a little confusing. The instructions don't list the create/update times in the returned json object
    // but then they say those timestamps should be returned. So I'm adding them here.
    private OffsetDateTime created;
    private OffsetDateTime lastUpdated;

    public StatusDto() { }

    public StatusDto(String body, String status, String detail) {
        this.body = body;
        this.status = status;
        this.detail = detail;
    }

    public StatusDto(RequestEntity entity) {
        if (null != entity) {
            this.body = entity.getBody();
            this.status = null != entity.getStatus() ? entity.getStatus().name() : null;
            this.detail = entity.getDetail();
            this.created = entity.getCreatedDate();
            this.lastUpdated = entity.getLastUpdateDate();
        }
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public OffsetDateTime getCreated() {
        return created;
    }

    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(OffsetDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
