package com.example.shopist.Server.ServerResponses;

public class ServerShoppingProduct extends ServerProduct {
    //this class represents the products stored in the server in the shopping list environment
    //this product has no price, since it is not attached to a store

    private int needed;

    public int getNeeded() {
        return needed;
    }

}
