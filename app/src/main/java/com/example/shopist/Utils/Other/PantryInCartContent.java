package com.example.shopist.Utils.Other;

import java.util.ArrayList;

public class PantryInCartContent {
    private String pantryUuid;
    private ArrayList<ProductBought> contentsBought;

    public PantryInCartContent(String pantryUuid, ArrayList<ProductBought> contentsBought){
        this.pantryUuid = pantryUuid;
        this.contentsBought = contentsBought;
    }

    public String getPantryUuid() {
        return pantryUuid;
    }

    public ArrayList<ProductBought> getContentsBought() {
        return contentsBought;
    }

    public void addProductToList(ProductBought productBought){
        contentsBought.add(productBought);
    }
}
