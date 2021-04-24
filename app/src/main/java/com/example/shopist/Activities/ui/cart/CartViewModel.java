package com.example.shopist.Activities.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class CartViewModel extends ViewModel {

    private MutableLiveData<Double> total;

    public CartViewModel() {
        total = new MutableLiveData<>();
    }

    public LiveData<Double> getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total.setValue(total > -1 ? total : null);
    }
}