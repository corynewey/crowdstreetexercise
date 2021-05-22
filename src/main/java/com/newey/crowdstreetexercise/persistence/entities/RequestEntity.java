package com.newey.crowdstreetexercise.persistence.entities;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "REQUEST")
public class RequestEntity {
    public enum Status {
        STARTED, PROCESSED, COMPLETED, ERROR
    }

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Integer id;
    private String body;
    private Status status;
    private String detail;
    private OffsetDateTime createdDate;
    private OffsetDateTime lastUpdateDate;

    protected RequestEntity() { }

    public RequestEntity(String body) {
        this.body = body;
    }

    public RequestEntity(String body, Status status, String detail) {
        this.body = body;
        this.status = status;
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "Request[id=" + this.id + ", body='" + this.body + "']";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

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

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public OffsetDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(OffsetDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}
