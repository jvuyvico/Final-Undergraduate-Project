package com.feifei.testv4.Classes;

/*
    Class for storing user credentials stored in shared preferences
    (not really used right now)
    Edit if we want to add or remove properties we need to associate the user with
 */

public class UserCredentials {
    private String username;
    private String studentnumber;

    public UserCredentials(String username, String studentnumber) {
        this.username = username;
        this.studentnumber = studentnumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStudentnumber() {
        return studentnumber;
    }

    public void setStudentnumber(String studentnumber) {
        this.studentnumber = studentnumber;
    }
}
