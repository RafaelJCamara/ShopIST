package com.example.shopist.Activities.ui.pantries;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PantriesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PantriesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is pantries fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}