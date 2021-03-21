package com.example.shopist;

import com.google.gson.annotations.SerializedName;

public class ServerData {

    //this class represents the content retrieved by the server, in case of a successful login

    private String name;
    private String email;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
