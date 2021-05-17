package com.example.shopist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.shopist.Activities.ui.cart.CartActivity;
import com.example.shopist.Product.CartProduct;
import com.example.shopist.Product.ShopProduct;
import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ServerShoppingList;
import com.example.shopist.Server.ServerResponses.ServerShoppingProduct;
import com.example.shopist.Utils.Other.ItemListAdapter;
import com.example.shopist.Utils.Other.SimpleCallback;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopActivity extends AppCompatActivity {

    private RetrofitManager retrofitManager;

    public ListView listView;
    //private ArrayList<String> listContent = new ArrayList<String>();

    private ShopViewModel shopViewModel;

    private String shopListName;
    private String shopListId;

    private ArrayList<ServerShoppingProduct> existingPantryProducts;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity);
        retrofitManager = new RetrofitManager(this);
        existingPantryProducts = new ArrayList<ServerShoppingProduct>();

        String listInfo = getIntent().getStringExtra("itemInfo");
        String [] values = listInfo.split("->");
        this.shopListName = values[0];
        this.shopListId = values[1];

        shopViewModel = new ViewModelProvider(this).get(ShopViewModel.class);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //listContent.clear();
        //add shopping products to list view
        handleProductListDialog();
    }

    private void handleProductListDialog(){
        productListSettings();
        getShoppingProductsFromServer();
    }

    private void productListSettings() {
        //configure product list and adapter
        fillListContentSettings();
        //add product click listener
        //addProductListClickListener();
        fillTextView();
    }

    //settings for the list and its adapters
    private void fillListContentSettings(){

        final SwipeRefreshLayout swipeList = this.findViewById(R.id.swipeLayout);

        swipeList.setOnRefreshListener(() -> {
            swipeList.setRefreshing(true);
            getShoppingProductsFromServer((args) -> {
                swipeList.setRefreshing(false);
            });
        });

        //get list view
        listView = findViewById(R.id.shopListProducts);
        View v = getLayoutInflater().inflate(R.layout.product_detail_shop,null);

        //create list adapter
        ItemListAdapter adapter = new ItemListAdapter(this, shopViewModel.getProductList().getValue(), (parent, view, position, id) -> {

            ShopProduct shopProduct = shopViewModel.getProductList().getValue().get(position);

            fillProductDetailView(v, shopProduct);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setOnDismissListener(dialog -> {
                getShoppingProductsFromServer();
            });
            AlertDialog dialog = builder.setView(v).create();
            dialog.show();

            Button update = v.findViewById(R.id.productDetailSave);

            update.setOnClickListener(v1 -> {
                parseProduct(v, shopProduct);
                updateProductInfo(shopProduct, (args) -> {
                    dialog.dismiss();
                });
            });

        }, (args) -> {
            ShopProduct shopProduct = (ShopProduct) args[0];
            shopProduct.setQuantity(0);
            updateProductInfo(shopProduct, (args2) -> {
                getShoppingProductsFromServer();
            });
        });

        //add adapter to list
        listView.setAdapter(adapter);
    }

    private void fillProductDetailView(View view, ShopProduct shopProduct) {

        TextView name = view.findViewById(R.id.productNameDetail);
        TextView description = view.findViewById(R.id.productDescriptionDetail);
        TextView needed = view.findViewById(R.id.productNeededDetail);
        EditText price = view.findViewById(R.id.productPriceField);
        EditText qty = view.findViewById(R.id.productQuantityField);

        name.setText(shopProduct.getName());
        description.setText(shopProduct.getDescription());
        needed.setText(String.format("%d", shopProduct.getNeeded()));
        price.setText(String.format("%.2f", shopProduct.getPrice()));
        qty.setText(String.format("%d", shopProduct.getQuantity()));

        Button minus = view.findViewById(R.id.qtyMinus);
        Button plus = view.findViewById(R.id.qtyPlus);

        minus.setOnClickListener(v1 -> {
            qty.requestFocus();
            qty.setText(String.format("%d", Long.parseLong(qty.getText().toString()) - 1));
        });

        plus.setOnClickListener(v1 -> {
            qty.requestFocus();
            qty.setText(String.format("%d", Long.parseLong(qty.getText().toString()) + 1));
        });

        RatingBar ratingBar;
        Button ratingButton;
        //Set product name in view
        //TextView productNameInStore = view.findViewById(R.id.productNameAtStore);
        //productNameInStore.setText(shopProduct.getName());

        //Set product classification in view
//        TextView classification = view.findViewById(R.id.classificationTextView);
//        classification.setText(getProductRatingFromList(shopProduct.getId()));

        //add save button
        /*Button saveProductInfoButton = view.findViewById(R.id.saveProductInfoAtStore);
        saveProductInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get quantity in store
                EditText productQuantityStoreComponent = view.findViewById(R.id.productQuantityInStore);
                String productQuantityStore = productQuantityStoreComponent.getText().toString();

                //get price in store
                EditText productPriceStoreComponent = view.findViewById(R.id.productPriceInStore);
                String productPriceStore = productPriceStoreComponent.getText().toString();

                Toast.makeText(ShopActivity.this, "Product clicked... "+productQuantityStore+" "+productPriceStore, Toast.LENGTH_SHORT).show();

                //update information in server
                updateProductInfo(shopProduct);
            }
        });*/

        ratingBar = view.findViewById(R.id.submitionRatingBar);
        ratingBar.setRating(shopProduct.getRating());
        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            //update information in server
            updateProductRating(shopProduct, String.valueOf(rating));
        });

        //ratingButton = view.findViewById(R.id.submitProdClassification);

//        ratingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Classification of Shopping product
//                String productRating = String.valueOf(ratingBar.getRating());
//                Toast.makeText(getApplicationContext(), productRating, Toast.LENGTH_LONG).show();
//
//            }
//        });

    }

    /*private void addProductListClickListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemInfo = (String) parent.getAdapter().getItem(position);
                //Toast.makeText(ShopActivity.this, itemInfo,Toast.LENGTH_SHORT).show();
                handleProductDetailDialog(itemInfo);
            }
        });
    }*/

    private void getShoppingProductsFromServer(SimpleCallback... callbacks){
        //ask the server for information
        Call<ServerShoppingList> call = retrofitManager.accessRetrofitInterface().syncShoppingList(shopListId);
        call.enqueue(new Callback<ServerShoppingList>() {
            @Override
            public void onResponse(Call<ServerShoppingList> call, Response<ServerShoppingList> response) {
                if(response.code()==200){
                    //list retrieved by the server
                    ServerShoppingList list = response.body();
                    renderLists(list.getProducts());

                    for(SimpleCallback callback : callbacks) {
                        callback.callback();
                    }
                }
            }
            @Override
            public void onFailure(Call<ServerShoppingList> call, Throwable t) {
                Toast.makeText(ShopActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderLists(ArrayList<ServerShoppingProduct> list){
        this.existingPantryProducts = list;
        shopViewModel.setProductList(new ArrayList<>());
        for(ServerShoppingProduct prod : list){
            ShopProduct sProduct = new ShopProduct(prod.getName(), prod.getDescription(), prod.getPrice(), prod.getQuantity(), prod.getNeeded());
            sProduct.setId(prod.getProductId());
            sProduct.setRating(prod.getRating());
            shopViewModel.getProductList().getValue().add(sProduct);
        }
        fillListContentSettings();
    }

    private void fillTextView(){
        TextView listNameView = findViewById(R.id.shopListName);
        listNameView.setText(this.shopListName);
    }

    /*private void handleProductDetailDialog(String itemInfo){
        View view = getLayoutInflater().inflate(R.layout.update_product_store,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
        builder.setView(view).show();
        handleProductUpdateInShopLogic(view, itemInfo);
    }*/

    /*private void handleProductUpdateInShopLogic(View view, ShopProduct shopProduct){
        //String[] prodInfo = itemInfo.split(";");

        RatingBar ratingBar;
        Button ratingButton;
        //Set product name in view
        TextView productNameInStore = view.findViewById(R.id.productNameAtStore);
        productNameInStore.setText(shopProduct.getName());

        //Set product classification in view
        TextView classification = view.findViewById(R.id.classificationTextView);
        classification.setText(getProductRatingFromList(shopProduct.getId()));

        //add save button
        Button saveProductInfoButton = view.findViewById(R.id.saveProductInfoAtStore);
        saveProductInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get quantity in store
                EditText productQuantityStoreComponent = view.findViewById(R.id.productQuantityInStore);
                String productQuantityStore = productQuantityStoreComponent.getText().toString();

                //get price in store
                EditText productPriceStoreComponent = view.findViewById(R.id.productPriceInStore);
                String productPriceStore = productPriceStoreComponent.getText().toString();

                Toast.makeText(ShopActivity.this, "Product clicked... "+productQuantityStore+" "+productPriceStore, Toast.LENGTH_SHORT).show();

                //update information in server
                updateProductInfo(shopProduct);
            }
        });

        ratingBar = view.findViewById(R.id.submitionRatingBar);
        ratingButton = view.findViewById(R.id.submitProdClassification);

        ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Classification of Shopping product
                String productRating = String.valueOf(ratingBar.getRating());
                Toast.makeText(getApplicationContext(), productRating, Toast.LENGTH_LONG).show();

                //update information in server
                updateProductRating(shopProduct, productRating);
            }
        });


    }

    private void updateProductInfo(String quantity, String price, String itemInfo, SimpleCallback... callbacks){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("productQuantity", quantity);
        map.put("productPrice", price);
        map.put("shoppingListId", shopListId);
        map.put("productId", getProductIdFromList(itemInfo));
        
        Call<Void> call = retrofitManager.accessRetrofitInterface().updateProductAtStore(map);
        call.enqueue(new Callback<Void>() {
            //when the server responds to our request
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ShopActivity.this, "Product updated with success.", Toast.LENGTH_SHORT).show();
                for(SimpleCallback callback : callbacks) {
                    callback.callback();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ShopActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
     */

    private void updateProductInfo(ShopProduct shopProduct, SimpleCallback... callbacks){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("productQuantity", String.valueOf(shopProduct.getQuantity()));
        map.put("productPrice", String.valueOf(shopProduct.getPrice()));
        map.put("shoppingListId", shopListId);
        map.put("productId", shopProduct.getId());

        Call<Void> call = retrofitManager.accessRetrofitInterface().updateProductAtStore(map);
        call.enqueue(new Callback<Void>() {
            //when the server responds to our request
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ShopActivity.this, "Product updated with success.", Toast.LENGTH_SHORT).show();
                for(SimpleCallback callback : callbacks) {
                    callback.callback();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ShopActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

/*
    private String getProductIdFromList(String itemInfo){
        String productId ="";
        for(ServerShoppingProduct prod:this.existingPantryProducts){
            String[] prodInfo = itemInfo.split(";");
            String[] productNeeded = prodInfo[1].trim().split(":");

            if(prodInfo[0].trim().equals(prod.getName()) &&
                    productNeeded[1].trim().equals(String.valueOf(prod.getNeeded()))
            ){
                productId+=prod.getProductId();
            }
        }
        return productId;
    }*/

    /*private String getProductRatingFromList(String productId) {
        String rateString="";
        double rate =0;
        for(ServerShoppingProduct prod:this.existingPantryProducts){
            if(productId.trim().equals(prod.getProductId())){
                if(prod.getTotalRating()!=0) {
                    double totalRating = prod.getTotalRating();
                    double nrRatings = prod.getNrRatings();
                    rate = totalRating / nrRatings;
                    rateString = String.format("%.1f", rate);
                }

            }
        }
        if(rate == 0)
            rateString = "no rating";

        return rateString;
    }*/

    public void onGoToCartButtonPressed(View view) {
        //create cart
        createCart();
    }

    private void createCart(){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("shopId", shopListId);
        Call<Void> call = retrofitManager.accessRetrofitInterface().createCart(map);
        call.enqueue(new Callback<Void>() {
            //when the server responds to our request
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ShopActivity.this, "Cart created with success.", Toast.LENGTH_SHORT).show();
                startCartActivity();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ShopActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startCartActivity(){
        Intent intent = new Intent(ShopActivity.this, CartActivity.class);
        intent.putExtra("storeName", this.shopListName);
        intent.putExtra("shoppingListId", shopListId);
        startActivity(intent);
    }

    //Shopping product Classification

    public void updateProductRating(ShopProduct shopProduct, String classification){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("productRating", classification);

        Call<Void> call = retrofitManager.accessRetrofitInterface().rateProductAtStore(shopProduct.getId(), map);
        call.enqueue(new Callback<Void>() {
            //when the server responds to our request
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ShopActivity.this, "Product updated with success.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ShopActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseProduct(View v, ShopProduct product) {

        EditText price = v.findViewById(R.id.productPriceField);
        EditText qty = v.findViewById(R.id.productQuantityField);

        product.setPrice(Double.parseDouble(price.getText().toString()));
        product.setQuantity(Long.parseLong(qty.getText().toString()));

    }

}