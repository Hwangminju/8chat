package com.example.mjhwa.chat.models;

/**
 * Created by mjhwa on 2017-12-13.
 */

public class LocationMessage extends Message {

    private String latitude;
    private String lontitude;
    private String messageText;

    public void setLatitude(String lat) {
        this.latitude = lat;
    }
    public void setLontitude(String lon) {
        this.lontitude = lon;
    }
    public String getLatitude() {
        return latitude;
    }
    public String getLontitude() {
        return lontitude;
    }
    public void setMessageText(String m) {
        this.messageText = m;
    }
    public String getMessageText() {
        return messageText;
    }
}
