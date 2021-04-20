package com.example.shopist.Activities.ui.shopping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.shopist.Activities.ui.ListFragment;
import com.example.shopist.R;
import com.example.shopist.Utils.PantryListManager;
import com.example.shopist.Utils.ShoppingListManager;

public class ShoppingFragment extends ListFragment {

    private ShoppingViewModel shoppingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shoppingViewModel =
                new ViewModelProvider(this).get(ShoppingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_shopping, container, false);

        listManager = ShoppingListManager.createShoppingListManager(root);
        super.onCreateView(inflater, container, savedInstanceState);

        //final TextView textView = root.findViewById(R.id.text_shopping);
        shoppingViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }
}