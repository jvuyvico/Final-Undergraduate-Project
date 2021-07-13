package com.feifei.testv4.JSON;

public class REST_Course {

    private String id;
    private String name;
    private String shortname;
    private String dept;

    public REST_Course(String id, String name, String shortname, String dept) {
        this.id = id;
        this.name = name;
        this.shortname = shortname;
        this.dept = dept;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortname() {
        return shortname;
    }

    public String getDept() {
        return dept;
    }
}
