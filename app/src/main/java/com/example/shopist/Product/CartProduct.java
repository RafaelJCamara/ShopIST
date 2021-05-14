package com.example.shopist.Product;

public class CartProduct extends Product {

    private double price;
    private long quantity;

    public CartProduct(String name, String description, double price, long quantity) {
        super(name, description);
        this.price = price;
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}
