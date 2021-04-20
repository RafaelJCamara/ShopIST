package com.example.shopist.Server.ServerResponses;

public class ServerShoppingList {

    //this class represents what we get from the server when synchronizing with a shopping list

    //pantry list name
    private String name;

    //products that belong to the pantry
    private ServerShoppingProduct[] products;

    public String getName() {
        return name;
    }

    public ServerShoppingProduct[] getProducts() {
        return products;
    }

}
