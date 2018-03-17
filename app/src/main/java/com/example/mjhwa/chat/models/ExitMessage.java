package com.example.mjhwa.chat.models;

/**
 * Created by mjhwa on 2017-11-25.
 */

public class ExitMessage extends Message {
    public ExitMessage() {
        super.setMessageType(Message.MessageType.EXIT);
    }
}
