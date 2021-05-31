package com.example.shopist.Product;

public class ShopProduct extends CartProduct {

    private int needed;
    private double totalRating;
    private int nrRating;

    private float rating;

    public ShopProduct(String name, String description, Double price, long quantity, int needed) {
        super(name, description, price, quantity);
        this.needed = needed;
    }

    public int getNeeded() { return needed; }

    public void setNeeded(int needed) {
        this.needed = needed;
    }


}
