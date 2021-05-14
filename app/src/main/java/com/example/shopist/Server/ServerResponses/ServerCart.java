package com.example.shopist.Server.ServerResponses;

import java.util.ArrayList;

public class ServerCart extends ServerProduct {

    private ArrayList<ServerCartProduct> products;

    private double total;

    private long quantity;

    public ArrayList<ServerCartProduct> getProducts() { return products; }

    public double getTotal() {
        return total;
    }

    public long getQuantity() { return quantity; }

}
