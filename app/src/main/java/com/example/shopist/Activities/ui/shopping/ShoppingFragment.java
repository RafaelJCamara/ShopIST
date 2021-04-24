package com.example.shopist.Activities.ui.shopping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.shopist.Activities.ui.ListFragment;
import com.example.shopist.R;

public class ShoppingFragment extends ListFragment {

    private ShoppingViewModel shoppingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shoppingViewModel =
                new ViewModelProvider(this).get(ShoppingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_shopping, container, false);

        /*listManager = ShoppingListManager.createShoppingListManager(root);
        super.onCreateView(inflater, container, savedInstanceState);
        */
        return root;
    }
}