package com.example.shopist.Utils.CacheManager;

import com.example.shopist.Utils.Other.OfflineProduct;
import com.example.shopist.Utils.Other.PantryList;
import com.example.shopist.Utils.Other.ShoppingList;

import java.util.ArrayList;

public class SmallDataCacheManager {

    private ArrayList<PantryList> currentPantryLists;
    private ArrayList<ShoppingList> currentShoppingLists;

    public SmallDataCacheManager(){
        this.currentPantryLists = new ArrayList<PantryList>();
        this.currentShoppingLists = new ArrayList<ShoppingList>();
    }

    /*
    *   Pantry list operations
    * */

    public void createPantryList(String name, String listAddress){
        currentPantryLists.add(new PantryList(name, listAddress));
    }

    public void addProductToPantryList(String listName, OfflineProduct product){
        PantryList list = currentPantryLists.get(getListByName(listName));
        list.addProductToPantry(product);
    }

    public void consumeProductFromPantry(String listName, String productName){
        this.currentPantryLists.get(getListByName(listName)).consumeProductFromPantry(productName);
    }


    public int getListByName(String name){
        int p = 0;
        for(PantryList pantry:this.currentPantryLists){
            if(pantry.getName().equals(name)){
                break;
            }
            p++;
        }
        return p;
    }


    /*
    *   Shopping list operations
    * */

    public void createShoppingList(String name, String listAddress){
        currentShoppingLists.add(new ShoppingList(name,listAddress));
    }

}
