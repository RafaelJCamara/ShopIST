package com.example.shopist.Server.ServerResponses;

public class ServerCartProduct extends ServerProduct {
    //this class represents the products stored in the server in the cart environment
    //this product has a price

    private int quantity;
    private double price;

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() { return price; }
}
