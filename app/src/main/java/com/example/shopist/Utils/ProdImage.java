package com.example.shopist.Utils;

public class ProdImage {

    private String productName;
    private String productImageUrl;

    public ProdImage(String productName, String productImageUrl){
        this.productName = productName;
        this.productImageUrl = productImageUrl;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }
}
