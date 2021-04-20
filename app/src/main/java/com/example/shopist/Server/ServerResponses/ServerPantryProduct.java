package com.example.shopist.Server.ServerResponses;

public class ServerPantryProduct {
    //this class represents the products stored in the server in the pantry environment
    //this product has no price, since it is not attached to a store

    private String productId;
    private String name;
    private String description;
    private int stock;
    private int needed;

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getStock() {
        return stock;
    }

    public int getNeeded() {
        return needed;
    }
}
