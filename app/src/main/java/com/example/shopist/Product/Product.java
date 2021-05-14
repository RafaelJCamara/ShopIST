package com.example.shopist.Product;

import com.example.shopist.R;

public abstract class Product {

    private String name;
    private String description;
    private int image;

    public Product(String name, String description){

        this.name=name;
        this.description=description;
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

}
