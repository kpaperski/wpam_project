package com.example.wpam;

public class Users {
    private String usrId;
    private String usrName;
    private String usrRole;
    private String usrEmail;
    private String usrPhone;

    public Users() {
    }

    public Users(String usrId, String usrName, String usrRole, String usrEmail, String usrPhone) {
        this.usrId = usrId;
        this.usrName = usrName;
        this.usrRole = usrRole;
        this.usrEmail = usrEmail;
        this.usrPhone = usrPhone;
    }

    public void setUsrId(String usrId) {
        this.usrId = usrId;
    }

    public void setUsrName(String usrName) {
        this.usrName = usrName;
    }

    public void setUsrRole(String usrRole) {
        this.usrRole = usrRole;
    }

    public void setUsrEmail(String usrEmail) {
        this.usrEmail = usrEmail;
    }

    public void setUsrPhone(String usrPhone) {
        this.usrPhone = usrPhone;
    }

    public String getUsrId() {
        return usrId;
    }

    public String getUsrName() {
        return usrName;
    }

    public String getUsrRole() {
        return usrRole;
    }

    public String getUsrEmail() {
        return usrEmail;
    }

    public String getUsrPhone() {
        return usrPhone;
    }
}
