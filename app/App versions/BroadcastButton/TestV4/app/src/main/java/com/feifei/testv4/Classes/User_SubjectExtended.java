package com.feifei.testv4.Classes;

public class User_SubjectExtended {
    String subject;
    String section;
    String days;
    int timestart;
    int timeend;
    String uuid;
    String major;
    String minor;

    public User_SubjectExtended(String subject, String section, String days, int timestart, int timeend, String uuid, String major, String minor) {
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

    public String getSection() {
        return section;
    }

    public String getDays() {
        return days;
    }

    public int getTimestart() {
        return timestart;
    }

    public int getTimeend() {
        return timeend;
    }

    public String getUuid() {
        return uuid;
    }

    public String getMajor() {
        return major;
    }

    public String getMinor() {
        return minor;
    }
}
