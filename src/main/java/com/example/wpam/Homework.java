package com.example.wpam;

public class Homework implements Comparable<Homework>{
    private String hwID;
    private String hwStudEmail;
    private String hwTitle;
    private String hwDescription;
    private String hwFileName;
    private String hwFileUrl;
    private String hwDate;
    private String hwTime;

    public Homework() {
    }

    public Homework(String hwID, String hwStudEmail, String hwTitle, String hwDescription, String hwFileName, String hwFileUrl, String hwTime) {
        this.hwID = hwID;
        this.hwStudEmail = hwStudEmail;
        this.hwTitle = hwTitle;
        this.hwDescription = hwDescription;
        this.hwFileName = hwFileName;
        this.hwFileUrl = hwFileUrl;
        this.hwTime = TimeFunction.getTime();
        this.hwDate = TimeFunction.getDate();
    }

    public String getHwID() {
        return hwID;
    }

    public String getHwStudEmail() {
        return hwStudEmail;
    }

    public String getHwTitle() {
        return hwTitle;
    }

    public String getHwDescription() {
        return hwDescription;
    }

    public String getHwFileName() {
        return hwFileName;
    }

    public String getHwTime() {
        return hwTime;
    }

    public String getHwFileUrl() {
        return hwFileUrl;
    }

    public String getHwDate() {
        return hwDate;
    }

    @Override
    public int compareTo(Homework o) {
        return (Long.parseLong(this.getHwTime()) < Long.parseLong(o.getHwTime()) ? 1 :
                (Long.parseLong(this.getHwTime()) == Long.parseLong(o.getHwTime()) ? 0 : -1));
    }
}
