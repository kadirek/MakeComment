package com.example.makecomment.Models;

import com.google.firebase.database.ServerValue;

public class Comment {

    private String content,uid,uimg,uname,showName,instaUserName;
    private Object timestamp;


    public Comment() {
    }

    public Comment(String content, String uid, String uimg, String uname, String showName, String instaUserName) {
        this.content = content;
        this.uid = uid;
        this.uimg = uimg;
        this.uname = uname;
        this.showName = showName;
        this.instaUserName = instaUserName;
        this.timestamp = ServerValue.TIMESTAMP;

    }

    public Comment(String content, String uid, String uimg, String uname, String showName, String instaUserName, Object timestamp) {
        this.content = content;
        this.uid = uid;
        this.uimg = uimg;
        this.uname = uname;
        this.showName = showName;
        this.instaUserName= instaUserName;
        this.timestamp = timestamp;
    }

    public String getInstaUserName() {
        return instaUserName;
    }

    public void setInstaUserName(String instaUserName) {
        this.instaUserName = instaUserName;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUimg() {
        return uimg;
    }

    public void setUimg(String uimg) {
        this.uimg = uimg;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
