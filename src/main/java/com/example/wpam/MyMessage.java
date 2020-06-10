package com.example.wpam;

public class MyMessage implements Comparable<MyMessage>{
    private String msgID;
    private String msgStudentEmail;
    private String msgAuthor;
    private String msgText;
    private String msgDate;
    private String msgTime;

    public MyMessage() {
    }

    public MyMessage(String msgID, String msgStudentEmail, String msgAuthor, String msgText, String msgTime) {
        this.msgID = msgID;
        this.msgStudentEmail = msgStudentEmail;
        this.msgAuthor = msgAuthor;
        this.msgText = msgText;
        this.msgTime = msgTime;
        this.msgDate = TimeFunction.getDate();
    }

    public String getMsgID() {
        return msgID;
    }

    public String getMsgStudentEmail() {
        return msgStudentEmail;
    }

    public String getMsgAuthor() {
        return msgAuthor;
    }

    public String getMsgText() {
        return msgText;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public String getMsgDate() {
        return msgDate;
    }

    @Override
    public int compareTo(MyMessage o) {
        return (Long.parseLong(this.getMsgTime()) < Long.parseLong(o.getMsgTime()) ? 1 :
                (Long.parseLong(this.getMsgTime()) == Long.parseLong(o.getMsgTime()) ? 0 : -1));
    }
}
