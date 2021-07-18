package com.feifei.testv4.JSON;


// General class to edit which parts you want to GET or POST in JSON to/from server
public class REST_Post {

    private String course;
    private String student;
    private String attendanceclass;
    private String date;
    private Boolean status;

    public REST_Post(String course, String student, String attendanceclass, String date, boolean status) {
        this.course = course;
        this.student = student;
        this.attendanceclass = attendanceclass;
        this.date = date;
        this.status = status;
    }

    public String getCourse() {
        return course;
    }

    public String getStudent() {
        return student;
    }

    public String getAttendanceclass() {
        return attendanceclass;
    }

    public String getDate() {
        return date;
    }

    public Boolean getStatus() {
        return status;
    }
}
