package com.example.shopist.Server.ServerResponses;

public class ServerShoppingProduct extends ServerProduct {
    //this class represents the products stored in the server in the shopping list environment
    //this product has no price, since it is not attached to a store(? may be ?)

    private int needed;
    private int price;

    public int getNeeded() {
        return needed;
    }

    public int getPrice() {
        return price;
    }

}
