package com.example.shopist.Product;

public class CartProduct extends Product {

    private String id;

    private Double price;
    private long quantity;

    public CartProduct(String name, String description, Double price, long quantity) {
        super(name, description);
        this.price = price;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
