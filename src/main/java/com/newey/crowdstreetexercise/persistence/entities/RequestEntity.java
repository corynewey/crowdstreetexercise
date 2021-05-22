package com.newey.crowdstreetexercise.persistence.entities;

import javax.persistence.*;

@Entity
@Table(name = "REQUEST")
public class RequestEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Integer id;
    private String body;
    private String callbackurl;

    protected RequestEntity() { }

    public RequestEntity(String body, String callbackUrl) {
        this.body = body;
        this.callbackurl = callbackUrl;
    }

    @Override
    public String toString() {
        return "Request[id=" + this.id + ", body='" + this.body + "', callbackUrl='" + this.callbackurl + "']";
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

    public String getCallbackurl() {
        return callbackurl;
    }

    public void setCallbackurl(String callbackUrl) {
        this.callbackurl = callbackUrl;
    }
}
