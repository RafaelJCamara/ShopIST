package com.example.shopist.Server.ServerResponses;

import java.io.Serializable;

public class ServerPantryProduct extends ServerProduct {
    //this class represents the products stored in the server in the pantry environment
    //this product has no price, since it is not attached to a store

    private int stock;
    private int needed;

    public int getStock() {
        return stock;
    }

    public int getNeeded() {
        return needed;
    }
}
