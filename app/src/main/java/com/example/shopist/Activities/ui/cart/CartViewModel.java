package com.example.shopist.Activities.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopist.Product.CartProduct;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel {

    private MutableLiveData<Double> total;

    private MutableLiveData<Long> quantity;

    private MutableLiveData<List<CartProduct>> productList;

    public CartViewModel() {
        total = new MutableLiveData<>();
        quantity = new MutableLiveData<>();
        productList = new MutableLiveData<>();
        productList.setValue(new ArrayList<>());
    }

    public LiveData<Double> getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total.setValue(total > -1 ? total : null);
    }

    public LiveData<Long> getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity.setValue(quantity > -1 ? quantity : null);
    }

    public MutableLiveData<List<CartProduct>> getProductList() {
        return productList;
    }

    public void setProductList(List<CartProduct> productList) {
        this.productList.setValue(productList);
    }
}