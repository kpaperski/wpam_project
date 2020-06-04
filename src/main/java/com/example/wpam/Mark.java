package com.example.wpam;

public class Mark implements Comparable<Mark> {
    private String markId;
    private String markName;
    private String markStudentEmail;
    private String markPoints;
    private String markMaxPoints;
    private String markPercent;
    private String markDetails;
    private String markTime;

    public Mark() {
    }

    public Mark(String markId, String markName, String markStudentEmail, String markPoints, String markMaxPoints, String markPercent, String markDetails, String markTime) {
        this.markId = markId;
        this.markName = markName;
        this.markStudentEmail = markStudentEmail;
        this.markPoints = markPoints;
        this.markMaxPoints = markMaxPoints;
        this.markPercent = markPercent;
        this.markDetails = markDetails;
        this.markTime = markTime;
    }

    public String getMarkId() {
        return markId;
    }

    public String getMarkName() {
        return markName;
    }

    public String getMarkStudentEmail() {
        return markStudentEmail;
    }

    public String getMarkPoints() {
        return markPoints;
    }

    public String getMarkMaxPoints() {
        return markMaxPoints;
    }

    public String getMarkPercent() {
        return markPercent;
    }

    public String getMarkDetails() {
        return markDetails;
    }

    public String getMarkTime() {
        return markTime;
    }

    @Override
    public int compareTo(Mark o) {
        return (Long.parseLong(this.getMarkTime()) < Long.parseLong(o.getMarkTime()) ? 1 :
                (Long.parseLong(this.getMarkTime()) == Long.parseLong(o.getMarkTime()) ? 0 : -1));
    }
}
