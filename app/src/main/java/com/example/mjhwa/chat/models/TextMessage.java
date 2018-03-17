package com.example.mjhwa.chat.models;

/**
 * Created by mjhwa on 2017-11-25.
 */

public class TextMessage extends Message{

    private String messageText;

    public void setMessageText(String m) {
        this.messageText = m;
    }
    public String getMessageText() {
        return messageText;
    }

}
