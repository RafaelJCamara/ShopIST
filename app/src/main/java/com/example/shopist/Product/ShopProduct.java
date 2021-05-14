package com.example.shopist.Product;

public class ShopProduct extends Product {

    private int needed;

    public ShopProduct(String name, String description, int needed) {
        super(name, description);
        this.needed = needed;
    }

    public int getNeeded() {
        return needed;
    }

    public void setNeeded(int needed) {
        this.needed = needed;
    }
}
