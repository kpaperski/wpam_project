package com.example.wpam;

public class Notice implements Comparable<Notice> {
    private String noticeID;
    private String noticeName;
    private String noticeText;
    private String noticeTime;

    public Notice() {}

    public Notice(String noticeID, String noticeName, String noticeText, String noticeTime) {
        this.noticeID = noticeID;
        this.noticeName = noticeName;
        this.noticeText = noticeText;
        this.noticeTime = noticeTime;
    }

    public String getNoticeID() {
        return noticeID;
    }

    public void setNoticeID(String noticeID) {
        this.noticeID = noticeID;
    }

    public String getNoticeName() {
        return noticeName;
    }

    public void setNoticeName(String noticeName) {
        this.noticeName = noticeName;
    }

    public String getNoticeText() {
        return noticeText;
    }

    public void setNoticeText(String noticeText) {
        this.noticeText = noticeText;
    }

    public String getNoticeTime() {
        return noticeTime;
    }

    @Override
    public int compareTo(Notice o) {
        return (Long.parseLong(this.getNoticeTime()) < Long.parseLong(o.getNoticeTime()) ? 1 :
                (Long.parseLong(this.getNoticeTime()) == Long.parseLong(o.getNoticeTime()) ? 0 : -1));
    }
}
