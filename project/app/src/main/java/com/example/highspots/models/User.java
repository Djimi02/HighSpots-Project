package com.example.highspots.models;

public class User {

    private String dbID;
    private String nickName;
    private String email;

    public User() { }

    public User(String dbID, String nickName, String email) {
        this.dbID = dbID;
        this.nickName = nickName;
        this.email = email;
    }

    public String getDbID() {
        return dbID;
    }

    public void setDbID(String dbID) {
        this.dbID = dbID;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
