package com.example.shopist.Activities.ui.cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ServerCart;
import com.example.shopist.Server.ServerResponses.ServerCartProduct;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private CartViewModel cartViewModel;

    private List<String> productList = new ArrayList<String>();

    private Context context;

    private RetrofitManager retrofitManager = new RetrofitManager(this);

    private String shoppingListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        context = this;

        Intent intent = getIntent();
        this.shoppingListId = intent.getStringExtra("shoppingListId");

        cartViewModel =
                new ViewModelProvider(this).get(CartViewModel.class);

        final TextView textView = findViewById(R.id.cart_total);
        final FloatingActionButton button = findViewById(R.id.cart_checkout_button);
        cartViewModel.getTotal().observe(this, s -> {
            textView.setText(s != null ? String.format("Total: %.2f€", s) : "");
            button.setVisibility(s == null ? View.INVISIBLE : View.VISIBLE);
        });

        cartViewModel.getQuantity().observe(this, s -> {
            textView.setText(s != null ? String.format("Item Qty: %d", s) : "");
            button.setVisibility(s == null ? View.INVISIBLE : View.VISIBLE);
        });

        button.setOnClickListener(view -> { onCheckoutButtonPressed(view); });

        productListSettings();

        getCartFromServer();

    }

    public void productListSettings() {
        final ListView listView = findViewById(R.id.cartList);

        //create list adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                //context
                this,
                //layout to be applied on
                android.R.layout.simple_list_item_1,
                //id inside layout
                android.R.id.text1,
                //data
                productList
        );

        //add adapter to list
        listView.setAdapter(adapter);
    }

    public void getCartFromServer(){
        Call<ServerCart> call = retrofitManager.accessRetrofitInterface().getCart(this.shoppingListId);
        call.enqueue(new Callback<ServerCart>() {
            @Override
            public void onResponse(Call<ServerCart> call, Response<ServerCart> response) {
                if(response.code()==200){
                    //list retrieved by the server
                    ServerCart cart = response.body();
                    //render list in front-end
                    renderCart(cart);
                }
            }

            @Override
            public void onFailure(Call<ServerCart> call, Throwable t) {
                Toast.makeText(context, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderCart(ServerCart cart){
        for(ServerCartProduct product : cart.getProducts()) {
            String finalListInfo = product.getName() + " | " + product.getDescription() + " | " + product.getPrice() + "€ | x" + product.getQuantity();
            productList.add(finalListInfo);
        }
        productListSettings();
        this.cartViewModel.setQuantity(cart.getQuantity());
        this.cartViewModel.setTotal(cart.getTotal());
    }

    public void onCheckoutButtonPressed(View view) {
        Call<Void> call = retrofitManager.accessRetrofitInterface().checkoutCart(this.shoppingListId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==200) {
                    finish();
                    productList.clear();
                    productListSettings();
                    cartViewModel.setTotal(-1);
                    Toast.makeText(context, "Cart checked out!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

}