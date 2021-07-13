package com.feifei.testv4.Classes;

public class CourseSection {

    private String course;
    private String section;
    private String id;

    public CourseSection(String course, String section, String id) {
        this.course = course;
        this.section = section;
        this.id = id;
    }

    public String getCourse() {
        return course;
    }

    public String getSection() {
        return section;
    }

    public String getId() {
        return id;
    }
}
