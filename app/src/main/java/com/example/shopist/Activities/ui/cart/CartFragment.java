package com.example.shopist.Activities.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.shopist.Activities.ui.ListFragment;
import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Utils.CartListManager;
import com.example.shopist.Utils.PantryListManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CartFragment extends ListFragment {

    private CartViewModel cartViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cartViewModel =
                new ViewModelProvider(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        listManager = CartListManager.createCartListManager(root, cartViewModel);
        super.onCreateView(inflater, container, savedInstanceState);

        final TextView textView = root.findViewById(R.id.cart_total);
        final FloatingActionButton button = root.findViewById(R.id.cart_checkout_button);
        cartViewModel.getTotal().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(@Nullable Double s) {
                textView.setText(s != null ? String.format("Total: %.2f€", s) : "");
                button.setVisibility(s == null ? View.INVISIBLE : View.VISIBLE);
            }
        });
        button.setOnClickListener(view -> { onCheckoutButtonPressed(view); });

        return root;
    }

    public void onCheckoutButtonPressed(View view) {
        ((CartListManager) listManager).handleCheckoutCartLogic();
    }

}