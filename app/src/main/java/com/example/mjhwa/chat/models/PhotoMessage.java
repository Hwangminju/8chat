package com.example.mjhwa.chat.models;

/**
 * Created by mjhwa on 2017-11-25.
 */

public class PhotoMessage extends Message{

    private String photoUrl;

    public void setPhotoUrl(String url) {
        this.photoUrl = url;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
}
