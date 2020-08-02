package com.example.makecomment.Models;

import com.google.firebase.database.ServerValue;

public class ParseItem {
    private String imgUrl;
    private String title;
    private String durationMinute;
    private String startTime;
    private Object timestamp;
    private int  commentCount;

    public ParseItem() {
    }

    public ParseItem(String imgUrl, String title, String durationMinute,int commentCount ,String startTime) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.durationMinute = durationMinute;
        this.commentCount = commentCount;
        this.startTime = startTime;
        this.timestamp = ServerValue.TIMESTAMP;

    }

    public ParseItem(String imgUrl, String title, String durationMinute, int commentCount,String startTime, Object timestamp) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.durationMinute = durationMinute;
        this.commentCount= commentCount;
        this.startTime = startTime;
        this.timestamp = timestamp;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDurationMinute() {
        return durationMinute;
    }

    public void setDurationMinute(String durationMinute) {
        this.durationMinute = durationMinute;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
