package com.example.shopist.Activities.ui.pantries;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class PantriesViewModel extends ViewModel {

    private MutableLiveData<List<String>> pantryListContent;

    private MutableLiveData<List<String>> uuids;

    public PantriesViewModel() {
        pantryListContent = new MutableLiveData<List<String>>();
        pantryListContent.setValue(new ArrayList<>());
        uuids = new MutableLiveData<List<String>>();
        uuids.setValue(new ArrayList<>());
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

    public LiveData<List<String>> getPantryUUIDs() {
        return uuids;
    }

    public void setPantryUUIDs(List<String> uuids) {
        this.uuids.setValue(uuids);
    }

    public void addToPantryUUIDs(String uuid) {
        this.pantryListContent.getValue().add(uuid);
    }

    public void clearContent(){
        this.pantryListContent.getValue().clear();
        this.uuids.getValue().clear();
    }
}