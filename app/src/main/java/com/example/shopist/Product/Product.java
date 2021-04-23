package com.example.shopist.Product;

import com.example.shopist.R;

public class Product {

    private String name;
    private String description;
    private int image;
    private int stock;
    private int needed;

    public Product(String name, String description, int stock, int needed){

        this.name=name;
        this.description=description;
        this.stock=stock;
        this.needed=needed;
        this.image= R.drawable.box;
    }

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

    public int getImage() {
        return this.image;
    }

    public void setImage(int image) {
        this.image = image;
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
