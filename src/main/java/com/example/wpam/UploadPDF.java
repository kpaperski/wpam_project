package com.example.wpam;

public class UploadPDF implements Comparable<UploadPDF> {
    private String pdfName;
    private String pdfUrl;
    private String pdfOwnerEmail;
    private String pdfTime;

    public UploadPDF() {
    }

    public UploadPDF(String pdfName, String pdfUrl) {
        this.pdfName = pdfName;
        this.pdfUrl = pdfUrl;
        this.pdfOwnerEmail = Consts.TEACHER_EMAIL;
        this.pdfTime = TimeFunction.getTime();
    }

    public UploadPDF(String pdfName, String pdfUrl, String pdfOwnerEmail) {
        this.pdfName = pdfName;
        this.pdfUrl = pdfUrl;
        this.pdfOwnerEmail = pdfOwnerEmail;
        this.pdfTime = TimeFunction.getTime();
    }

    public String getPdfName() {
        return pdfName;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public String getPdfTime() {
        return pdfTime;
    }

    public String getPdfOwnerEmail() {
        return pdfOwnerEmail;
    }

    @Override
    public int compareTo(UploadPDF o) {
        return (Long.parseLong(this.getPdfTime()) < Long.parseLong(o.getPdfTime()) ? 1 :
                (Long.parseLong(this.getPdfTime()) == Long.parseLong(o.getPdfTime()) ? 0 : -1));
    }
}
