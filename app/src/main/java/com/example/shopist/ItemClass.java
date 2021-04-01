package com.example.shopist;

public class ItemClass {
    private String name;
    private int image;

    public ItemClass(String name){
        this.name=name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
