package com.example.shopist.Server.ServerResponses;

import java.util.ArrayList;

public class ServerShoppingList {

    //this class represents what we get from the server when synchronizing with a shopping list

    //pantry list name
    private String name;

    private String uuid;

    //products that belong to the pantry
    private ArrayList<ServerShoppingProduct> products;

    public String getName() {
        return name;
    }

    public String getUuid(){return uuid;}

    public ArrayList<ServerShoppingProduct> getProducts() {
        return products;
    }

}
