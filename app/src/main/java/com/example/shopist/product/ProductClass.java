package com.example.shopist.product;

public class ProductClass {
    private String name;
    private String description;
    private int price;
    private int image;

    public ProductClass(String name){
        this.name=name;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public int getPrice() { return price; }

    public void setPrice(int price) { this.price = price; }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
