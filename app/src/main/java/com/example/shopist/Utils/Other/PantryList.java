package com.example.shopist.Utils.Other;


import java.util.ArrayList;

public class PantryList {
    private String name;
    private String listAddress;
    private String uuid;
    private ArrayList<OfflineProduct> currentProducts;

    public PantryList(String name, String listAddress){
        this.name = name;
        this.listAddress = listAddress;
    }

    public void addProductToPantry(OfflineProduct product){
        currentProducts.add(product);
    }

    public void consumeProductFromPantry(String productName){
        currentProducts.get(getProductByName(productName)).consume();
    }

    public int getProductByName(String productName){
        int i =0;
        for(OfflineProduct ofp:currentProducts){
            if(ofp.getName().equals(productName)){
                break;
            }
            i++;
        }
        return i;
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

    public ArrayList<OfflineProduct> getCurrentProducts() {
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
