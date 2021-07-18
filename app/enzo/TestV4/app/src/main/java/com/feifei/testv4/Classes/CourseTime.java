package com.feifei.testv4.Classes;

public class CourseTime {

    private String day;
    private String starttime;
    private String endtime;
    private String assign_id;

    public CourseTime(String day, String starttime, String endtime, String assign_id) {
        this.day = day;
        this.starttime = starttime;
        this.endtime = endtime;
        this.assign_id = assign_id;
    }

    public String getDay() {
        return day;
    }

    public String getStarttime() {
        return starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public String getAssign_id() {
        return assign_id;
    }
}
