package com.example.shopist.Activities.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.shopist.Utils.ListManager;

public abstract class ListFragment extends Fragment {

    protected ListManager listManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        addSettings();
        listOperations();

        return null;
    }

    protected void listOperations(){
        listManager.listSettings();
    }

    protected void addSettings(){
        listManager.addListLogic();
    }

}
