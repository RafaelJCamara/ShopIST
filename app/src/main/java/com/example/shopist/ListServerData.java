package com.example.shopist;

import java.util.LinkedList;

public class ListServerData {
    //this class represents the list retrieved by the server
    //UUID stands for Universally Unique Identifier
    private String uuid;
    private String name;
    private LinkedList<ServerProduct> products;

    public String getListName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public LinkedList<ServerProduct> getProducts() {
        return products;
    }
}
