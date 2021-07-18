package com.feifei.testv3;
import com.google.gson.annotations.SerializedName;


// General class to edit which parts you want to GET or POST in JSON to/from server
public class REST_Post {

    private String username;
    private String email;

    public REST_Post(String email, String username) {
        this.email = email;
        this.username = username;
    }



    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
