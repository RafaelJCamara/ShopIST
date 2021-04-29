package com.example.shopist.Activities.ui.shopping;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ShoppingViewModel extends ViewModel {


    private MutableLiveData<List<String>> shoppingListContent;

    public ShoppingViewModel() {
        shoppingListContent = new MutableLiveData<List<String>>();
        shoppingListContent.setValue(new ArrayList<>());
    }

    public LiveData<List<String>> getShoppingListContent() {
        return shoppingListContent;
    }

    public void setShoppingListContent(List<String> shoppingListContent) {
        this.shoppingListContent.setValue(shoppingListContent);
    }

    public void addToShoppingListContent(String pantryList) {
        this.shoppingListContent.getValue().add(pantryList);
    }
}