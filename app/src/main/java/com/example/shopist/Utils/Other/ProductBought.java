package com.example.shopist.Utils.Other;

public class ProductBought {
    private String productName;
    private int amountBought;

    public ProductBought(String productName, int amountBought){
        this.productName=productName;
        this.amountBought=amountBought;
    }

    public String getProductName() {
        return productName;
    }

    public int getAmountBought() {
        return amountBought;
    }
}
