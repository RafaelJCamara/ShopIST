package com.example.shopist.Server.ServerResponses;

public class ServerProduct {
    //this class represents the products stored in the server
    private String name;
    private double price;
    private String description;
    private int quantity;

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }
}
