package com.example.mjhwa.chat.models;


public class User {
    private String uid, email, name, profileUrl;
    //private boolean selection ;

    public User() {

    }
    public User(String uid, String email, String name, String profileUrl) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.profileUrl = profileUrl;
    }
    public User(String uid, String email, String name) {
        this.uid = uid;
        this.email = email;
        this.name = name;
    }
    public void setUid(String u) {
        uid = u;
    }
    public void setEmail(String e) {
        email = e;
    }
    public void setName(String n) {
        name = n;
    }
    public void setProfileUrl(String p) {
        profileUrl = p;
    }
    /*public void setSelection(boolean s) {
        selection = s;
    }*/
    public String getUid() {
        return uid;
    }
    public String getEmail() {
        return email;
    }
    public String getName() { return name; }
    public String getProfileUrl() { return profileUrl; }
    /*public boolean getSelection() {
        return selection;
    }
    public boolean isSelection() {
        if(selection == true) return true;
        else return false;
    }*/

}

