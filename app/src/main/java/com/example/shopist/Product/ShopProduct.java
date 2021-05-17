package com.example.shopist.Product;

public class ShopProduct extends Product {

    private int needed;
    private float price;
    private double totalRating;
    private double nrRating;

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

    public double getNrRatings() { return nrRating; }

    public double getTotalRating() { return totalRating; }

    public void setNrRating(double nrRating) {
        this.nrRating = nrRating;
    }

    public void setTotalRating(double totalRating) {
        this.totalRating = totalRating;
    }

    public double getRating(){
        return this.totalRating/this.nrRating;
    }
}
