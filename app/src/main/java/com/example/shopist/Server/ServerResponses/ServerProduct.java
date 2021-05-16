package com.example.shopist.Server.ServerResponses;

import java.io.Serializable;

public abstract class ServerProduct implements Serializable {

    private String productId;
    private String name;
    private String description;
    private double total_rating;
    private double nr_ratings;

    public String getProductId() {
        return productId;
    }

    public String getName() { return name; }

    public String getDescription() {
        return description;
    }

    public double getTotalRating() { return total_rating; }

    public double getNrRatings() { return  nr_ratings; }

}
