package com.example.mjhwa.chat.models;

/**
 * Created by eomtaeyoon on 2017. 11. 26..
 */

public class EmoticonMessage extends Message {

    private String emoticonUrl;

    public void setEmoticonUrl(String url) {
        this.emoticonUrl = url;
    }
    public String getEmoticonUrl() {
        return emoticonUrl;
    }
}
