package com.example.shopist.Server.ServerResponses;

import java.io.Serializable;

public abstract class ServerProduct implements Serializable {

    private String productId;
    private String name;
    private String description;
    private float rating;

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public float getRating() { return rating; }

}
