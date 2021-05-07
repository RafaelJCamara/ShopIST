package com.example.shopist.Utils.Other;

import com.example.shopist.R;

public class OfflineProduct {

    private String name;
    private String description;
    private String barcode;
    private int stock;
    private int needed;

    public OfflineProduct(String name, String description, String barcode,int stock, int needed){
        this.name=name;
        this.description=description;
        this.barcode=barcode;
        this.stock=stock;
        this.needed=needed;
    }

    public void consume(){
        this.needed++;
        this.stock--;
    }


    /*
    *   Getters and setters
    * */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description; }

    public void setDescription(String description) {
        this.description = description; }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getNeeded() {
        return needed;
    }

    public void setNeeded(int needed) {
        this.needed = needed;
    }

}
