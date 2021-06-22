package com.feifei.testv4.JSON;

public class REST_CourseMapping {

    private String id;
    private String room_id;
    private String building_id;
    private String course;

    public REST_CourseMapping(String id, String room_id, String building_id, String course) {
        this.id = id;
        this.room_id = room_id;
        this.building_id = building_id;
        this.course = course;
    }

    public String getId() {
        return id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public String getBuilding_id() {
        return building_id;
    }

    public String getCourse() {
        return course;
    }
}
