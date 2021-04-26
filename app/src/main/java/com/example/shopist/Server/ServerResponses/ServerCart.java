package com.example.shopist.Server.ServerResponses;

import java.util.ArrayList;

public class ServerCart extends ServerProduct {

    private ArrayList<ServerCartProduct> products;

    private double total;

    public ArrayList<ServerCartProduct> getProducts() { return products; }

    public double getTotal() {
        return total;
    }

}
