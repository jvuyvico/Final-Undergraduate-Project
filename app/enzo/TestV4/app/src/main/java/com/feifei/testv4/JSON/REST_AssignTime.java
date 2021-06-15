package com.feifei.testv4.JSON;

public class REST_AssignTime {

    private String id;
    private String period;
    private String day;
    private String assign;

    public REST_AssignTime(String id, String period, String day, String assign) {
        this.id = id;
        this.period = period;
        this.day = day;
        this.assign = assign;
    }

    public String getId() {
        return id;
    }

    public String getPeriod() {
        return period;
    }

    public String getDay() {
        return day;
    }

    public String getAssign_id() {
        return assign;
    }
}
