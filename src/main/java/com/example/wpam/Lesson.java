package com.example.wpam;

public class Lesson {
    private String lsnId;
    private String lsnName;
    private String lsnStudentEmail;
    private String lsnStudentName;
    private String lsnDay;
    private String lsnStartHour;
    private String lsnEndHour;

    public Lesson() {}

    public Lesson(String lsnId, String lsnName, String lsnStudentName, String lsnStudentEmail, String lsnDay, String lsnStartHour, String lsnEndHour) {
        this.lsnId = lsnId;
        this.lsnName = lsnName;
        this.lsnStudentEmail = lsnStudentEmail;
        this.lsnStudentName = lsnStudentName;
        this.lsnDay = lsnDay;
        this.lsnStartHour = lsnStartHour;
        this.lsnEndHour = lsnEndHour;
    }

    public String getLsnId() {
        return lsnId;
    }

    public void setLsnId(String lsnId) {
        this.lsnId = lsnId;
    }

    public String getLsnName() {
        return lsnName;
    }

    public void setLsnName(String lsnName) {
        this.lsnName = lsnName;
    }

    public String getLsnStudentEmail() {
        return lsnStudentEmail;
    }

    public void setLsnStudentEmail(String lsnStudentEmail) {
        this.lsnStudentEmail = lsnStudentEmail;
    }

    public String getLsnStudentName() {
        return lsnStudentName;
    }

    public void setLsnStudentName(String lsnStudentName) {
        this.lsnStudentName = lsnStudentName;
    }

    public String getLsnDay() {
        return lsnDay;
    }

    public void setLsnDay(String lsnDay) {
        this.lsnDay = lsnDay;
    }

    public String getLsnStartHour() {
        return lsnStartHour;
    }

    public void setLsnStartHour(String lsnStartHour) {
        this.lsnStartHour = lsnStartHour;
    }

    public String getLsnEndHour() {
        return lsnEndHour;
    }

    public void setLsnEndHour(String lsnEndHour) {
        this.lsnEndHour = lsnEndHour;
    }
}
