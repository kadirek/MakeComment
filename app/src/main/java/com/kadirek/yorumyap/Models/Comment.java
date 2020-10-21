package com.kadirek.yorumyap.Models;

import com.google.firebase.database.ServerValue;

public class Comment {

    private String content,uid,uimg,uname,showName,instaUserName,endTimePython;
    private Object timestamp;


    public Comment() {
    }

    public Comment(String content, String uid, String uimg, String uname, String showName, String instaUserName, String endTimePython) {
        this.content = content;
        this.uid = uid;
        this.uimg = uimg;
        this.uname = uname;
        this.showName = showName;
        this.instaUserName = instaUserName;
        this.endTimePython = endTimePython;
        this.timestamp = ServerValue.TIMESTAMP;

    }

    public Comment(String content, String uid, String uimg, String uname, String showName, String instaUserName,String endTimePython, Object timestamp) {
        this.content = content;
        this.uid = uid;
        this.uimg = uimg;
        this.uname = uname;
        this.showName = showName;
        this.instaUserName= instaUserName;
        this.endTimePython= endTimePython;
        this.timestamp = timestamp;
    }

    public String getEndTimePython() {
        return endTimePython;
    }

    public void setEndTimePython(String endTimePython) {
        this.endTimePython = endTimePython;
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
