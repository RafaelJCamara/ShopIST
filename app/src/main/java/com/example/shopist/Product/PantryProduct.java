package com.example.shopist.Product;

public class PantryProduct extends Product {

    private int stock;
    private int needed;

    public PantryProduct(String name, String description, int stock, int needed) {
        super(name, description);
        this.stock = stock;
        this.needed = needed;
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

}
