package com.feifei.testv3;

/*
    Class for storing the info from detected BLE devices.
 */

public class BLE_Device {
    private String BLE_name;
    private String BLE_address;
    private String BLE_uuid;
    private String BLE_major;
    private String BLE_minor;

    public BLE_Device(String BLE_name, String BLE_address, String BLE_uuid, String BLE_major, String BLE_minor) {
        this.BLE_name = BLE_name;
        this.BLE_address = BLE_address;
        this.BLE_uuid = BLE_uuid;
        this.BLE_major = BLE_major;
        this.BLE_minor = BLE_minor;
    }

    public String getBLE_name() {
        return BLE_name;
    }

    public void setBLE_name(String BLE_name) {
        this.BLE_name = BLE_name;
    }

    public String getBLE_address() {
        return BLE_address;
    }

    public void setBLE_address(String BLE_address) {
        this.BLE_address = BLE_address;
    }

    public String getBLE_uuid() {
        return BLE_uuid;
    }

    public void setBLE_uuid(String BLE_uuid) {
        this.BLE_uuid = BLE_uuid;
    }

    public String getBLE_major() {
        return BLE_major;
    }

    public void setBLE_major(String BLE_major) {
        this.BLE_major = BLE_major;
    }

    public String getBLE_minor() {
        return BLE_minor;
    }

    public void setBLE_minor(String BLE_minor) {
        this.BLE_minor = BLE_minor;
    }
}