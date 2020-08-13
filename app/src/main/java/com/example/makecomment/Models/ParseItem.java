package com.example.makecomment.Models;

import com.google.firebase.database.ServerValue;

public class ParseItem {
    private String imgUrl;
    private String title;
    private String durationMinute;
    private String startTime;
    private Object timestamp;
    private int  commentCount;
    private Boolean isClicked;
    private String remainTime;

    public ParseItem() {
    }

    public ParseItem(String imgUrl, String title, String durationMinute,int commentCount,Boolean isClicked ,String startTime,String remainTime) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.durationMinute = durationMinute;
        this.commentCount = commentCount;
        this.startTime = startTime;
        this.remainTime= remainTime;
        this.isClicked = isClicked;
        this.timestamp = ServerValue.TIMESTAMP;

    }

    public ParseItem(String imgUrl, String title, String durationMinute, int commentCount,Boolean isClicked,String startTime,String remainTime, Object timestamp) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.durationMinute = durationMinute;
        this.commentCount= commentCount;
        this.startTime = startTime;
        this.remainTime = remainTime;
        this.isClicked = isClicked;
        this.timestamp = timestamp;
    }

    public String getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(String remainTime) {
        this.remainTime = remainTime;
    }

    public Boolean getClicked() {
        return isClicked;
    }

    public void setClicked(Boolean clicked) {
        isClicked = clicked;
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
