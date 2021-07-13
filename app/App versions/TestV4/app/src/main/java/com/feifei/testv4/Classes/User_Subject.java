package com.feifei.testv4.Classes;

/*
    Class for storing subjects information imported from local database
    Edit if we want to add or remove properties we need to associate the student with
 */

public class User_Subject {
    String subject;
    String section;
    String days;
    int timestart;
    int timeend;
    String uuid;
    String major;
    String minor;


    public User_Subject(String subject, String section, String days, int timestart, int timeend, String uuid, String major, String minor) {
        this.subject = subject;
        this.section = section;
        this.days = days;
        this.timestart = timestart;
        this.timeend = timeend;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public int getTimestart() {
        return timestart;
    }

    public void setTimestart(int timestart) {
        this.timestart = timestart;
    }

    public int getTimeend() {
        return timeend;
    }

    public void setTimeend(int timeend) {
        this.timeend = timeend;
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
}
