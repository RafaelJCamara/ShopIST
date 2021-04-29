package com.example.shopist.Activities.ui.pantries;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class PantriesViewModel extends ViewModel {

    private MutableLiveData<List<String>> pantryListContent;

    public PantriesViewModel() {
        pantryListContent = new MutableLiveData<List<String>>();
        pantryListContent.setValue(new ArrayList<>());
    }

    public LiveData<List<String>> getPantryListContent() {
        return pantryListContent;
    }

    public void setPantryListContent(List<String> pantryListContent) {
        this.pantryListContent.setValue(pantryListContent);
    }

    public void addToPantryListContent(String pantryList) {
        this.pantryListContent.getValue().add(pantryList);
    }
}