package com.feifei.testv3;

public class Attendance_Data {
    private String subject;
    private String status;
    private String uuid;
    private String major;
    private String minor;
    private String date;
    private String time;

    public Attendance_Data(String subject, String status, String uuid, String major, String minor, String date, String time) {
        this.subject = subject;
        this.status = status;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.date = date;
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
