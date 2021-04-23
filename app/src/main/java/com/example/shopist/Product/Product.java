package com.example.shopist.Product;

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

        return image;
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

    public String toString()
    {
        //return name + " " + age + " " + college + " " + course + " " + address;
        return "";
    }
}
