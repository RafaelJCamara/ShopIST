package com.example.shopist.Activities.ui.cart;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopist.Activities.LoginActivity;
import com.example.shopist.Activities.MainActivityNav;
import com.example.shopist.Activities.PantryActivity;
import com.example.shopist.Activities.ui.pantries.PantriesFragment;
import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ServerCart;
import com.example.shopist.Server.ServerResponses.ServerCartProduct;
import com.example.shopist.Utils.Other.Adapter;
import com.example.shopist.Utils.Other.CartContent;
import com.example.shopist.Utils.Other.DistributeProductsAtCartAdapter;
import com.example.shopist.Utils.Other.PantryInCartContent;
import com.example.shopist.Utils.Other.ProductBought;
import com.example.shopist.Utils.Other.PublicInfoManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
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

    private RecyclerView recyclerView;

    private int index = 0;

    public static ArrayList<PantryInCartContent> selectedPantries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        selectedPantries = new ArrayList<PantryInCartContent>();
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
        Call<ServerCart> call = retrofitManager.accessRetrofitInterface().getCart(this.shoppingListId, MainActivityNav.currentUserId);
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
        int count = 0;
        for(ServerCartProduct product : cart.getProducts()) {
            String finalListInfo = product.getName() + " | " + product.getDescription() + " | " + product.getPrice() + "€ | x" + product.getQuantity();
            productList.add(finalListInfo);
            count+=product.getQuantity();
        }
        PublicInfoManager.currentNumberItemsInCart = count;
        productListSettings();
        this.cartViewModel.setTotal(cart.getTotal());
    }

    public void onCheckoutButtonPressed(View view) {
        //show interface
        showSelectionInterface();
    }

    private void showSelectionInterface(){
        //similar to when we click on a product inside a pantry list
        View view = getLayoutInflater().inflate(R.layout.select_pantry_to_checkout,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        builder.setView(view).show();
        TextView productName = view.findViewById(R.id.productNameCart);
        productName.setText(this.productList.get(index));
        Button nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index!=productList.size()-1){
                    Log.d("checkout","Current index: "+index);
                    index=index+1;
                    Log.d("checkout","New index: "+index);
                    //get form content
                    showSelectionInterface();
                }else{
                    Log.d("checkout","End of all products bought.");
                    index=0;
                    //finished distribution
                    //goto pantry list
                    HashMap<String, CartContent> map = new HashMap<String,CartContent>();
                    //choose what goes to which pantry
                    CartContent cc = new CartContent(selectedPantries);
                    map.put("checkout",cc);
                    updateCheckoutAtServer(view, map);
                }
            }
        });
        //add adapter settings
        adapterSettings(view);
    }

    private void adapterSettings(View view){
        ArrayList<String> currentPantries = (ArrayList<String>) PantriesFragment.pantriesViewModel.getPantryListContent().getValue();
        this.recyclerView = view.findViewById(R.id.toPutProductsRV);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setHasFixedSize(true);

        TextView textview = view.findViewById(R.id.productNameCart);

        DistributeProductsAtCartAdapter adapter = new DistributeProductsAtCartAdapter(currentPantries, textview.getText().toString());
        recyclerView.setAdapter(adapter);
    }

    /*
    *   Checkout stuff
    * */

    public static void recordChange(String changedValue, String productName, String pantryListUuid){
        ProductBought pb = new ProductBought(productName, Integer.valueOf(changedValue));
        if(pantryAlreadyExists(pantryListUuid)){
            //pantry already exists
            for(int i=0;i!=selectedPantries.size();i++){
                if(selectedPantries.get(i).getPantryUuid().equals(pantryListUuid)){
                    //found pantry
                    selectedPantries.get(i).addProductToList(pb);
                }
            }
        }else{
            //pantry does not exists
            ArrayList<ProductBought> apb = new ArrayList<ProductBought>();
            apb.add(pb);
            PantryInCartContent pc = new PantryInCartContent(pantryListUuid, apb);
            selectedPantries.add(pc);
        }
    }

    private static boolean pantryAlreadyExists(String pantryUuid){
        for(PantryInCartContent picc: selectedPantries){
            if(picc.getPantryUuid().equals(pantryUuid)){
                return true;
            }
        }
        return false;
    }



    private void updateCheckoutAtServer(View view, HashMap<String, CartContent> map){
        Call<Void> call = retrofitManager.accessRetrofitInterface().checkoutCart(this.shoppingListId, MainActivityNav.currentUserId,map);
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