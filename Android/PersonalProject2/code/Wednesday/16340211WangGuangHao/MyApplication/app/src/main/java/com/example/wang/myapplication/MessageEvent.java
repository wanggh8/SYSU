package com.example.wang.myapplication;

public class MessageEvent {
    /* Additional fields if needed */
    private String name;
    private String kind;
    public MessageEvent(String kind,String name) {
        super();
        this.name = name;
        this.kind = kind;
    }
    public String getName() {
        return name;
    }
    public String getKind() {
        return kind;
    }
}
