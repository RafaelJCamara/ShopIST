package com.example.shopist.Product;

public class ShopProduct extends CartProduct {

    private int needed;
    private double totalRating;
    private double nrRating;

    private float rating;

    public ShopProduct(String name, String description, Double price, long quantity, int needed) {
        super(name, description, price, quantity);
        this.needed = needed;
    }

    public int getNeeded() { return needed; }

    public void setNeeded(int needed) {
        this.needed = needed;
    }

    public double getNrRatings() { return nrRating; }

    public double getTotalRating() { return totalRating; }

    public void setNrRating(double nrRating) {
        this.nrRating = nrRating;
    }

    public void setTotalRating(double totalRating) {
        this.totalRating = totalRating;
    }

    public float getRating() { return this.rating; }

    public void setRating(float rating) {
        this.rating = rating;
    }

}
