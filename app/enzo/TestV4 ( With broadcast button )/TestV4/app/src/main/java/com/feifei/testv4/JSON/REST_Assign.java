package com.feifei.testv4.JSON;

public class REST_Assign {

    private String id;
    private String class_id;
    private String course;
    private String teacher;

    public REST_Assign(String id, String class_id, String course, String teacher) {
        this.id = id;
        this.class_id = class_id;
        this.course = course;
        this.teacher = teacher;
    }

    public String getId() {
        return id;
    }

    public String getClass_id() {
        return class_id;
    }

    public String getCourse() {
        return course;
    }

    public String getTeacher() {
        return teacher;
    }
}
