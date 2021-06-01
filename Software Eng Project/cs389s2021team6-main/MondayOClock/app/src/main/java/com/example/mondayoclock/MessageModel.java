package com.example.mondayoclock;

import java.util.Date;

public class MessageModel {

    public String message;
    public boolean isOwn;
    public Date messageTime = new Date();

    public MessageModel(String message, boolean isOwn) {
        this.message = message;
        this.isOwn = isOwn;
    }
}
