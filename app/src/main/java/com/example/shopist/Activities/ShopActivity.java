package com.example.shopist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopist.Activities.ui.cart.CartActivity;
import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ServerShoppingList;
import com.example.shopist.Server.ServerResponses.ServerShoppingProduct;
import com.example.shopist.Server.ServerResponses.UserAccess;
import com.example.shopist.Server.ServerResponses.WaitTimeInfo;
import com.example.shopist.Utils.Other.Adapter;
import com.example.shopist.Utils.Other.PublicInfoManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopActivity extends AppCompatActivity {

    private RetrofitManager retrofitManager;

    public ListView listView;
    private ArrayList<String> listContent = new ArrayList<String>();

    private String listId;

    private RecyclerView recyclerView;

    private ArrayList<ServerShoppingProduct> existingPantryProducts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity);
        retrofitManager = new RetrofitManager(this);
        existingPantryProducts = new ArrayList<ServerShoppingProduct>();
    }

    @Override
    protected void onResume() {
        super.onResume();

        listContent.clear();
        //add shopping products to list view
        handleProductListDialog();
        addUserAccessList();
    }



    /*
    ##########################
    ### access list grant ###
    ##########################
     */

    private void addUserAccessList(){
        FloatingActionButton button = findViewById(R.id.userAccessGrantShoppingButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleUserAccessListDialog();
            }
        });
    }

    private void handleUserAccessListDialog(){
        View view = getLayoutInflater().inflate(R.layout.user_access_shopping,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog alert = builder.create();
        alert.show();
        handleUserAccessListLogic(view, alert);
        fillUserAccessList(view);
        removeUserAccessList(view, alert);
    }

    private void handleUserAccessListLogic(View view, AlertDialog builder){
        EditText userEmail = view.findViewById(R.id.userEmailAddressAccessShopping);
        Button grantAccessButton = view.findViewById(R.id.saveUserShoppingAccessButton);
        grantAccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grantAccessToUser(userEmail.getText().toString(),listId, builder);
            }
        });
    }

    private void grantAccessToUser(String userEmail, String listUuid, AlertDialog builder){
        HashMap<String,String> map = new HashMap<String, String>();
        map.put("userEmail",userEmail);
        map.put("ownerId",MainActivityNav.currentUserId);
        Call<Void> call = retrofitManager.accessRetrofitInterface().grantUserAccessShopping(listUuid, map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==200){
                    Toast.makeText(ShopActivity.this, "User access granted with success.", Toast.LENGTH_LONG).show();
                    builder.cancel();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ShopActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fillUserAccessList(View view){
        Call<UserAccess> call = retrofitManager.accessRetrofitInterface().getAllShoppingUsers(listId);
        call.enqueue(new Callback<UserAccess>() {
            @Override
            public void onResponse(Call<UserAccess> call, Response<UserAccess> response) {
                String[] users = response.body().getUsers();
                renderUserAccessList(users, view);
            }

            @Override
            public void onFailure(Call<UserAccess> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Server error." ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderUserAccessList(String[] users, View view){
        this.recyclerView = view.findViewById(R.id.shoppingListAccessList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setHasFixedSize(true);
        Adapter adapter = new Adapter(Arrays.asList(users));
        recyclerView.setAdapter(adapter);
    }

    private void removeUserAccessList(View view, AlertDialog builder){
        Button removeAccessButton = view.findViewById(R.id.removerUserAccessToShoppingList);
        removeAccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }











    /*
    *   Settings
    * */


    private void handleProductListDialog(){
        productListSettings();
        fillPantryProductList();
    }

    private void productListSettings() {
        //configure product list and adapter
        fillListContentSettings();
        //add product click listener
        addProductListClickListener();
        //add waiting time button logic
        addWaitTimeLogic();
        fillTextView();
    }

    //settings for the list and its adapters
    private void fillListContentSettings(){
        //get list view
        listView = findViewById(R.id.shopListProducts);

        //create list adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                //context
                ShopActivity.this,
                android.R.layout.simple_list_item_1,
                //data
                listContent
        );

        //add adapter to list
        listView.setAdapter(adapter);
    }

    private void addProductListClickListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemInfo = (String) parent.getAdapter().getItem(position);
                //Toast.makeText(ShopActivity.this, itemInfo,Toast.LENGTH_SHORT).show();
                handleProductDetailDialog(itemInfo);
            }
        });
    }

    private void addWaitTimeLogic(){
        FloatingActionButton button = findViewById(R.id.waitTimeButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWaitTimeFromServer();
            }
        });
    }

    private void getWaitTimeFromServer(){
        TextView listCodeView = findViewById(R.id.shopListCode);
        Call<WaitTimeInfo> call = retrofitManager.accessRetrofitInterface().getCurrentWaitingTime(listCodeView.getText().toString());
        call.enqueue(new Callback<WaitTimeInfo>() {
            @Override
            public void onResponse(Call<WaitTimeInfo> call, Response<WaitTimeInfo> response) {
                if(response.code()==200){
                    //list retrieved by the server
                    WaitTimeInfo list = response.body();
                    updateTime(list.getWaitingTime());
                }
            }
            @Override
            public void onFailure(Call<WaitTimeInfo> call, Throwable t) {
                Toast.makeText(ShopActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateTime(double time){
        TextView textView = findViewById(R.id.currentWaitTimeStore);
        textView.setText(String.valueOf(time)+" minutes");
        textView.setVisibility(View.VISIBLE);
    }


    private void fillPantryProductList(){
        //ask the server for information
        Call<ServerShoppingList> call = retrofitManager.accessRetrofitInterface().syncShoppingList(listId);
        call.enqueue(new Callback<ServerShoppingList>() {
            @Override
            public void onResponse(Call<ServerShoppingList> call, Response<ServerShoppingList> response) {
                if(response.code()==200){
                    //list retrieved by the server
                    ServerShoppingList list = response.body();
                    renderLists(list.getProducts());
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
        for(ServerShoppingProduct prod : list){
            String productInfo=prod.getName()+"; Needed:"+prod.getNeeded();
            listContent.add(productInfo);
        }
        fillListContentSettings();
    }

    private void fillTextView(){
        String listInfo = getIntent().getStringExtra("itemInfo");
        String [] values = listInfo.split("->");
        TextView listNameView = findViewById(R.id.shopListName);
        TextView listCodeView = findViewById(R.id.shopListCode);
        listNameView.setText(values[0]);
        listId = values[1];
        listCodeView.setText(listId);
        PublicInfoManager.currentShopUuid = listId;
    }

    private void handleProductDetailDialog(String itemInfo){
        View view = getLayoutInflater().inflate(R.layout.update_product_store,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
        builder.setView(view).show();
        handleProductUpdateInShopLogic(view, itemInfo);
    }

    private void handleProductUpdateInShopLogic(View view, String itemInfo){
        String[] prodInfo = itemInfo.split(";");

        RatingBar ratingBar;
        Button ratingButton;
        //Set product name in view
        TextView productNameInStore = view.findViewById(R.id.productNameAtStore);
        productNameInStore.setText(prodInfo[0].trim());

        //Set product classification in view
        TextView classification = view.findViewById(R.id.classificationTextView);
        classification.setText(getProductRatingFromList(getProductIdFromList(itemInfo)));

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
                updateProductInfo(productQuantityStore, productPriceStore, itemInfo);
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
                updateProductRating(itemInfo, productRating);
            }
        });


    }

    private void updateProductInfo(String quantity, String price, String itemInfo){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("productQuantity", quantity);
        map.put("productPrice", price);
        map.put("shoppingListId", listId);
        map.put("productId", getProductIdFromList(itemInfo));
        
        Call<Void> call = retrofitManager.accessRetrofitInterface().updateProductAtStore(map);
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
    }

    private String getProductRatingFromList(String productId) {
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
    }

    public void onGoToCartButtonPressed(View view) {
        //create cart
        createCart();
    }

    private void createCart(){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("shopId", listId);
        Call<Void> call = retrofitManager.accessRetrofitInterface().createCart(MainActivityNav.currentUserId,map);
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
        intent.putExtra("shoppingListId", listId);
        startActivity(intent);
    }

    //Shopping product Classification

    public void updateProductRating(String itemInfo, String classification){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("productRating", classification);
        String productId = getProductIdFromList(itemInfo);

        Call<Void> call = retrofitManager.accessRetrofitInterface().rateProductAtStore(productId, map);
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

}