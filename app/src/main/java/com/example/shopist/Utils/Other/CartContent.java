package com.example.shopist.Utils.Other;

import java.util.ArrayList;

public class CartContent {
    ArrayList<PantryInCartContent> cartContents;

    public CartContent(ArrayList<PantryInCartContent> cartContents){
        this.cartContents = cartContents;
    }

    public ArrayList<PantryInCartContent> getCartContents() {
        return cartContents;
    }

}
