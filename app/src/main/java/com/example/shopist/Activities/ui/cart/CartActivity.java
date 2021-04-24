package com.example.shopist.Activities.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.shopist.Activities.ui.ListFragment;
import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Utils.CartListManager;
import com.example.shopist.Utils.ListManager;
import com.example.shopist.Utils.PantryListManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CartActivity extends AppCompatActivity {

    private CartViewModel cartViewModel;

    protected CartListManager listManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cartViewModel =
                new ViewModelProvider(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        Intent intent = getIntent();
        int shoppingListId = intent.getIntExtra("shoppingListId", 1);
        listManager = CartListManager.createCartListManager(root, cartViewModel, shoppingListId);

        final TextView textView = root.findViewById(R.id.cart_total);
        final FloatingActionButton button = root.findViewById(R.id.cart_checkout_button);
        cartViewModel.getTotal().observe(this, s -> {
            textView.setText(s != null ? String.format("Total: %.2f€", s) : "");
            button.setVisibility(s == null ? View.INVISIBLE : View.VISIBLE);
        });
        button.setOnClickListener(view -> { onCheckoutButtonPressed(view); });

        addSettings();
        listOperations();

        return root;
    }

    public void onCheckoutButtonPressed(View view) {
        listManager.handleCheckoutCartLogic();
    }

    protected void listOperations(){
        listManager.listSettings();
    }

    protected void addSettings(){
        listManager.addListLogic();
    }

}