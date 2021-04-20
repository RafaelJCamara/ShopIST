package com.example.shopist.Server.ServerResponses;

import java.util.ArrayList;

public class ServerPantryList {
    //this class represents what we get from the server when synchronizing with a pantry list

    //pantry list name
    private String name;

    //products that belong to the pantry
    private ServerPantryProduct[] products;

    public String getName() {
        return name;
    }

    public ServerPantryProduct[] getProducts() {
        return products;
    }
}
