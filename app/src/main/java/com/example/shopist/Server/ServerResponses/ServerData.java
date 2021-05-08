package com.example.shopist.Server.ServerResponses;

public class ServerData {

    //this class represents the content retrieved by the server, in case of a successful login

    private String name;
    private String email;
    private String userId;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }
}
