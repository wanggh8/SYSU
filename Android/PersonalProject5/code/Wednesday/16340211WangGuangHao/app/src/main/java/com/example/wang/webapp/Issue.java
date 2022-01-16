package com.example.wang.webapp;

public class Issue {
    private String title;
    private String created_at;
    private String state;
    private String body;

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getState() {
        return state;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}

