package com.feifei.testv4.Classes;

public class Scan_Data {
    private String uuid;
    private String time;
    private String rssi;

    public Scan_Data(String uuid, String time, String rssi) {
        this.uuid = uuid;
        this.time = time;
        this.rssi = rssi;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }
}
