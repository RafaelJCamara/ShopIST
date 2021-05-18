package com.example.shopist.Product;

public class ShopProduct extends Product {

    private int needed;
    private float price;
    private double totalRating;
    private int nrRating;

    public ShopProduct(String name, String description, int needed) {
        super(name, description);
        this.needed = needed;
    }

    public int getNeeded() { return needed; }

    public void setNeeded(int needed) {
        this.needed = needed;
    }

    public float getPrice() { return price; }

    public void setPrice(float price) {
        this.price = price;
    }

}
