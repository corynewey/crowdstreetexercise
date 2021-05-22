package com.newey.crowdstreetexercise.persistence.entities;

import javax.persistence.*;

@Entity
@Table(name = "REQUEST")
public class RequestEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Integer id;
    private String body;
    private String status;
    private String detail;

    protected RequestEntity() { }

    public RequestEntity(String body) {
        this.body = body;
    }

    public RequestEntity(String body, String status, String detail) {
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
}
