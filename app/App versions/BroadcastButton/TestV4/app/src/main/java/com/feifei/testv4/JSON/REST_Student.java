package com.feifei.testv4.JSON;

public class REST_Student {
    private String USN;
    private String name;
    private String DOB;
    private String user;
    private String class_id;

    public REST_Student(String USN, String name, String DOB, String user, String class_id) {
        this.USN = USN;
        this.name = name;
        this.DOB = DOB;
        this.user = user;
        this.class_id = class_id;
    }

    public String getUSN() {
        return USN;
    }

    public String getName() {
        return name;
    }

    public String getDOB() {
        return DOB;
    }

    public String getUser() {
        return user;
    }

    public String getClass_id() {
        return class_id;
    }
}
