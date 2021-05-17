package com.example.shopist.Product;

public class ShopProduct extends CartProduct {

    private int needed;

    private float rating;

    public ShopProduct(String name, String description, Double price, long quantity, int needed) {
        super(name, description, price, quantity);
        this.needed = needed;
    }

    public int getNeeded() {
        return needed;
    }

    public void setNeeded(int needed) {
        this.needed = needed;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

}
