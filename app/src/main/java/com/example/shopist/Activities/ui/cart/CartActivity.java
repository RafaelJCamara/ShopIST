package com.example.shopist.Activities.ui.cart;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cloudinary.utils.StringUtils;
import com.example.shopist.Activities.ShopActivity;
import com.example.shopist.Product.CartProduct;
import com.example.shopist.Activities.LoginActivity;
import com.example.shopist.Activities.MainActivity;
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
import com.example.shopist.Utils.Other.ItemListAdapter;
import com.example.shopist.Utils.Other.SimpleCallback;
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

    private ItemListAdapter adapter;

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

        final TextView cartTitle = findViewById(R.id.cart_title);
        cartTitle.setText(String.format("%s - %s", intent.getStringExtra("storeName"), getString(R.string.cart)));

        cartViewModel =
                new ViewModelProvider(this).get(CartViewModel.class);

        final TextView totalView = findViewById(R.id.cart_total);
        final TextView qtyView = findViewById(R.id.cart_qty);
        final FloatingActionButton button = findViewById(R.id.cart_checkout_button);
        cartViewModel.getTotal().observe(this, s -> {
            totalView.setText(s != null ? String.format("Total: %.2f€", s) : "");
            button.setVisibility(s == null ? View.INVISIBLE : View.VISIBLE);
        });

        cartViewModel.getQuantity().observe(this, s -> {
            qtyView.setText(s != null ? String.format("Item Qty: %d", s) : "");
            button.setVisibility(s == null ? View.INVISIBLE : View.VISIBLE);
        });

        button.setOnClickListener(view -> { onCheckoutButtonPressed(view); });

        productListSettings();

        getCartFromServer();

    }

    public void productListSettings() {

        final SwipeRefreshLayout swipeList = this.findViewById(R.id.swipeLayout);

        swipeList.setOnRefreshListener(() -> {
            swipeList.setRefreshing(true);
            getCartFromServer((args) -> {
                swipeList.setRefreshing(false);
            });
        });

        final ListView listView = findViewById(R.id.cartList);
        View v = getLayoutInflater().inflate(R.layout.product_detail_cart,null);

        //create list adapter
        adapter = new ItemListAdapter(this, cartViewModel.getProductList().getValue(), (parent, view, position, id) -> {

            CartProduct cartProduct = cartViewModel.getProductList().getValue().get(position);

            fillProductDetailView(v, cartProduct);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setOnDismissListener(dialog -> {
                getCartFromServer();
            });
            AlertDialog dialog = builder.setView(v).create();
            dialog.show();

            Button update = v.findViewById(R.id.productDetailSave);
            Button remove = v.findViewById(R.id.removeProductButton);

            update.setOnClickListener(v1 -> {
                parseProduct(v, cartProduct);
                updateProductInfo(v, cartProduct, (args) -> {
                    dialog.dismiss();
                });
            });

            remove.setOnClickListener(v1 -> {
                cartProduct.setQuantity(0);
                updateProductInfo(v, cartProduct, (args) -> {
                    dialog.dismiss();
                });
            });

        }, (args) -> {
            CartProduct cartProduct = (CartProduct) args[0];
            cartProduct.setQuantity(0);
            updateProductInfo(v, cartProduct, (args2) -> {
                getCartFromServer();
            });
        });

        //add adapter to list
        listView.setAdapter(adapter);
    }

    private void fillProductDetailView(View v, CartProduct cartProduct) {

        TextView name = v.findViewById(R.id.productNameDetail);
        TextView description = v.findViewById(R.id.productDescriptionDetail);
        EditText price = v.findViewById(R.id.productPriceField);
        EditText qty = v.findViewById(R.id.productQuantityField);

        name.setText(cartProduct.getName());
        description.setText(cartProduct.getDescription());
        price.setText(String.format("%.2f", cartProduct.getPrice()));
        qty.setText(String.format("%d", cartProduct.getQuantity()));

        Button minus = v.findViewById(R.id.qtyMinus);
        Button plus = v.findViewById(R.id.qtyPlus);

        minus.setOnClickListener(v1 -> {
            qty.requestFocus();
            qty.setText(String.format("%d", Long.parseLong(qty.getText().toString()) - 1));
        });

        plus.setOnClickListener(v1 -> {
            qty.requestFocus();
            qty.setText(String.format("%d", Long.parseLong(qty.getText().toString()) + 1));
        });

    }

    public void getCartFromServer(SimpleCallback... callback){
        Call<ServerCart> call = retrofitManager.accessRetrofitInterface().getCart(this.shoppingListId, MainActivityNav.currentUserId);
        call.enqueue(new Callback<ServerCart>() {
            @Override
            public void onResponse(Call<ServerCart> call, Response<ServerCart> response) {
                if(response.code()==200){
                    //list retrieved by the server
                    ServerCart cart = response.body();
                    //render list in front-end
                    renderCart(cart);
                    for(SimpleCallback c : callback) {
                        c.callback();
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerCart> call, Throwable t) {
                Toast.makeText(context, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderCart(ServerCart cart){
        cartViewModel.setProductList(new ArrayList<>());
        int count = 0;
        for(ServerCartProduct product : cart.getProducts()) {
            CartProduct cProduct = new CartProduct(product.getName(), product.getDescription(), product.getPrice(), product.getQuantity());
            cProduct.setId(product.getProductId());
            cartViewModel.getProductList().getValue().add(cProduct);
            String finalListInfo = product.getName() + " | " + product.getDescription() + " | " + product.getPrice() + "€ | x" + product.getQuantity();
            productList.add(finalListInfo);
            count+=product.getQuantity();
        }
        PublicInfoManager.currentNumberItemsInCart = count;
        productListSettings();
        this.cartViewModel.setQuantity(cart.getQuantity());
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
        AlertDialog dialog = builder.setView(view).create();
        dialog.show();
        TextView productName = view.findViewById(R.id.productNameCart);
        productName.setText(this.productList.get(index));
        Button nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
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
            dialog.dismiss();
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
        ProductBought pb = new ProductBought(productName, StringUtils.isBlank(changedValue) ? 0 : Integer.valueOf(changedValue));
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
                    cartViewModel.getProductList().getValue().clear();
                    productListSettings();
                    cartViewModel.setTotal(-1);
                    Toast.makeText(context, "Cart checked out!", Toast.LENGTH_SHORT).show();
//                    gotoMainActivity();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateProductInfo(View view, CartProduct product, SimpleCallback... callback){

        HashMap<String,String> map = new HashMap<String,String>();
        map.put("productQuantity", String.valueOf(product.getQuantity()));
        map.put("productPrice", String.valueOf(product.getPrice()));
        map.put("shoppingListId", this.shoppingListId);
        map.put("productId", product.getId());

        Call<Void> call = retrofitManager.accessRetrofitInterface().updateProductAtStore(map);
        call.enqueue(new Callback<Void>() {
            //when the server responds to our request
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(CartActivity.this, "Product updated with success.", Toast.LENGTH_SHORT).show();
                for(SimpleCallback c : callback) {
                    c.callback();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CartActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseProduct(View v, CartProduct product) {

        EditText price = v.findViewById(R.id.productPriceField);
        EditText qty = v.findViewById(R.id.productQuantityField);

        product.setPrice(Double.parseDouble(price.getText().toString()));
        product.setQuantity(Long.parseLong(qty.getText().toString()));

    }

    private void gotoMainActivity(){
        Intent intent = new Intent(context, MainActivityNav.class);

        //put cart information into intent
        intent.putExtra("username", MainActivityNav.currentUsername);
        intent.putExtra("userId",MainActivityNav.currentUserId);

        //start activity targeted in the intent
        startActivity(intent);
    }

}