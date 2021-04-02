package com.feifei.testv3;

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
