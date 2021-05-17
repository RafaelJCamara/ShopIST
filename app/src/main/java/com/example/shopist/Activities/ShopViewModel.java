package com.example.shopist.Activities;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopist.Product.ShopProduct;

import java.util.ArrayList;
import java.util.List;

public class ShopViewModel extends ViewModel {

    private MutableLiveData<List<ShopProduct>> productList;

    public ShopViewModel() {
        productList = new MutableLiveData<>();
        productList.setValue(new ArrayList<>());
    }

    public MutableLiveData<List<ShopProduct>> getProductList() {
        return productList;
    }

    public void setProductList(List<ShopProduct> productList) {
        this.productList.setValue(productList);
    }

}
