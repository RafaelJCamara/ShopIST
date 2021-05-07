package com.example.shopist.Utils.Other;

import com.example.shopist.Product.Product;

import java.util.ArrayList;

public class ShoppingList {
    private String name;
    private String listAddress;
    private String uuid;
    private ArrayList<Product> currentProducts;

    public ShoppingList(String name, String listAddress){
        this.name = name;
        this.listAddress = listAddress;
    }




    /*
     *   Getters
     * */

    public String getName() {
        return name;
    }

    public String getListAddress() {
        return listAddress;
    }

    public String getUuid() {
        return uuid;
    }

    public ArrayList<Product> getCurrentProducts() {
        return currentProducts;
    }

    /*
     *   Setters
     * */

    public void setName(String name) {
        this.name = name;
    }

    public void setListAddress(String listAddress) {
        this.listAddress = listAddress;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


}
