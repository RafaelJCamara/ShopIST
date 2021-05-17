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
import com.example.shopist.Product.Product;
import com.example.shopist.Product.ShopProduct;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShopActivity extends AppCompatActivity {

    private RetrofitManager retrofitManager;

    public ListView listView;
    private ArrayList<String> listContent = new ArrayList<String>();
    private ArrayList<ShopProduct> shopProductList = new ArrayList<ShopProduct>();

    private String shopListName;
    private String shopListId;

    private RecyclerView recyclerView;

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
        fillUserAccessList(view, alert);
//        removeUserAccessList(view, alert);
    }

    private void handleUserAccessListLogic(View view, AlertDialog builder){
        EditText userEmail = view.findViewById(R.id.userEmailAddressAccessShopping);
        Button grantAccessButton = view.findViewById(R.id.saveUserShoppingAccessButton);
        grantAccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grantAccessToUser(userEmail.getText().toString(),shopListId, builder);
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

    private void fillUserAccessList(View view, AlertDialog builder){
        Call<UserAccess> call = retrofitManager.accessRetrofitInterface().getAllShoppingUsers(shopListId);
        call.enqueue(new Callback<UserAccess>() {
            @Override
            public void onResponse(Call<UserAccess> call, Response<UserAccess> response) {
                String[] users = response.body().getUsers();
                renderUserAccessList(users, view, builder);
            }

            @Override
            public void onFailure(Call<UserAccess> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Server error." ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderUserAccessList(String[] users, View view, AlertDialog builder){
        this.recyclerView = view.findViewById(R.id.shoppingListAccessList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setHasFixedSize(true);
        Adapter adapter = new Adapter(Arrays.asList(users));
        recyclerView.setAdapter(adapter);
        removeUserAccessList(view, builder, adapter);
    }

    private void removeUserAccessList(View view, AlertDialog builder, Adapter adapter){
        Button removeAccessButton = view.findViewById(R.id.removerUserAccessToShoppingList);
        removeAccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeUsers(adapter.getSelectedShopping(), view, builder);
            }
        });
    }

    private void removeUsers(ArrayList<String> removedUsers, View view, AlertDialog builder){
        HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
        map.put("deleted", removedUsers);
        Call<Void> call = retrofitManager.accessRetrofitInterface().removeUserAccessShopping(shopListId,map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(view.getContext(),"User removed with success." ,Toast.LENGTH_SHORT).show();
                builder.cancel();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Server error." ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    *   Settings
    * */


    private void handleProductListDialog(){
        productListSettings();
        fillPantryProductList();
        shareShoppingLogic();
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
        listContent.clear();

        //fill listContent from ShopProductList with strings to use on list adapter
        for(ShopProduct prod : shopProductList) {
            String productInfo = prod.getName() + "; Needed:" + prod.getNeeded() + "; price:" + prod.getPrice();
            listContent.add(productInfo);
        }
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
                ShopProduct product = getProductFromShoProductList(itemInfo);
                if(product != null)
                    handleProductDetailDialog(product);
                else
                    Toast.makeText(ShopActivity.this, "error getting product",Toast.LENGTH_SHORT).show();

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
        Call<ServerShoppingList> call = retrofitManager.accessRetrofitInterface().syncShoppingList(shopListId);
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
            ShopProduct p = new ShopProduct(prod.getName(), prod.getDescription(), prod.getNeeded());
            if(prod.getPrice()!=0)
                p.setPrice(prod.getPrice());

            if(prod.getNrRatings()!=0) {
                p.setNrRating(prod.getNrRatings());
                p.setTotalRating(prod.getTotalRating());
            }

            String productInfo=prod.getName()+"; Needed:"+prod.getNeeded()+"; price:" + prod.getPrice();
            listContent.add(productInfo);
            shopProductList.add(p);
        }
        fillListContentSettings();
    }

    private void fillTextView(){
        String listInfo = getIntent().getStringExtra("itemInfo");
        String [] values = listInfo.split("->");
        TextView listNameView = findViewById(R.id.shopListName);
        TextView listCodeView = findViewById(R.id.shopListCode);
        listNameView.setText(values[0]);
        shopListId = values[1];
        listCodeView.setText(shopListId);
        PublicInfoManager.currentShopUuid = shopListId;
        listNameView.setText(this.shopListName);
        listCodeView.setText(this.shopListId);
    }

    private void handleProductDetailDialog(ShopProduct product){
        View view = getLayoutInflater().inflate(R.layout.update_product_store,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
        builder.setView(view).show();
        handleProductUpdateInShopLogic(view, product);
    }

    private void handleProductUpdateInShopLogic(View view, ShopProduct product){
        //String[] prodInfo = itemInfo.split(";");
        //String productPrice[] = prodInfo[2].split(":");

        RatingBar ratingBar;
        Button ratingButton;
        //Set product name in view
        TextView productNameInStore = view.findViewById(R.id.productNameAtStore);
        productNameInStore.setText(product.getName());

        //Set product classification in view
        updateProductRatingFrontend(view, product);
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
                if(product.getPrice()==0 && productPriceStore.trim().equals("")){
                    handleUserPromptDialog(product);
                }

                //update information in server
                updateProductInfo(productQuantityStore, productPriceStore, product);
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
                updateProductRating(product, productRating);

                updateProductRatingFrontend(view, product);
            }
        });
    }

    private void updateProductInfo(String quantity, String price, ShopProduct itemInfo){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("productQuantity", quantity);
        map.put("productPrice", price);
        map.put("shoppingListId", shopListId);
        map.put("productId", getProductIdFromList(itemInfo));

        itemInfo.setPrice(Float.parseFloat(price));
        fillListContentSettings();

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


    private String getProductIdFromList(ShopProduct product){
        String productId ="";
        for(ServerShoppingProduct prod:this.existingPantryProducts){
            //String[] prodInfo = itemInfo.split(";");
            //String[] productNeeded = prodInfo[1].trim().split(":");

            if(product.getName().equals(prod.getName()) &&
                    product.getNeeded()== prod.getNeeded()
            ){
                productId+=prod.getProductId();
            }
        }
        return productId;
    }

    private ShopProduct getProductFromShoProductList(String itemInfo) {
        String[] prodInfo = itemInfo.split(";");

        ShopProduct product = null;
        String productName = prodInfo[0].trim();

        for(ShopProduct prod:this.shopProductList){
            if(productName.equals(prod.getName())){
                product = prod;
            }
        }
        Log.d("getprodfromshoplist", product.getName() +" "+ product.getPrice() +" " +product.getRating() );
        return product;
    }

    public void onGoToCartButtonPressed(View view) {
        //create cart
        createCart();
    }

    private void createCart(){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("shopId", shopListId);
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
        intent.putExtra("storeName", this.shopListName);
        intent.putExtra("shoppingListId", shopListId);
        startActivity(intent);
    }

    //Shopping product Classification

    public void updateProductRating(ShopProduct itemInfo, String classification){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("productRating", classification);
        String productId = getProductIdFromList(itemInfo);

        //this should just append after server response OK

        double currentClassification = itemInfo.getTotalRating();
        double totalClassifications = itemInfo.getNrRatings();
        itemInfo.setTotalRating(Double.parseDouble(classification) + currentClassification);
        itemInfo.setNrRating(totalClassifications+1);

        Call<Void> call = retrofitManager.accessRetrofitInterface().rateProductAtStore(productId, map);
        call.enqueue(new Callback<Void>() {
            //when the server responds to our request
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ShopActivity.this, "Product updated with success.", Toast.LENGTH_SHORT).show();
                //updateProductOnShopProductList(itemInfo);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ShopActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateProductRatingFrontend(View view, ShopProduct itemInfo){
        TextView classification = view.findViewById(R.id.classificationTextView);
        ShopProduct product = getProductFromShoProductList(itemInfo.getName());
        if(product.getNrRatings()>0)
            classification.setText(String.format("%.1f", product.getRating()));
        else
            classification.setText("Not rated yet");


        int i;
        for(i=0; i<this.shopProductList.size(); i++){
            if(this.shopProductList.get(i).getName().equals(itemInfo.getName())) {
                //this.shopProductList.set(i, itemInfo);
                Log.d("updateProductOnShopProductList", itemInfo.getName() +" "+ itemInfo.getPrice() +" " +itemInfo.getRating() );
            }
        }


        //fillPantryProductList();
    }

    //user prompt
    private void handleUserPromptDialog(ShopProduct product){
        View view = getLayoutInflater().inflate(R.layout.user_prompt_price,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
        builder.setView(view).show();
        handleProductUpdateUserPrompt(view, product);
    }

    private void handleProductUpdateUserPrompt(View view, ShopProduct product) {
        //String[] prodInfo = itemInfo.split(";");
        Button updateButton;
        //Set product name in view
        TextView productNameInStore = view.findViewById(R.id.product_name);
        productNameInStore.setText(product.getName());

        //add save button
        Button saveProductInfoButton = view.findViewById(R.id.update_button);
        saveProductInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get price in store
                EditText productPriceStoreComponent = view.findViewById(R.id.new_price);
                String productPriceStore = productPriceStoreComponent.getText().toString();

                Toast.makeText(ShopActivity.this, "Product clicked... " + productPriceStore, Toast.LENGTH_SHORT).show();

                //update information in server
                updateProductPriceServer(productPriceStore, product);
            }
        });
    }


    private void updateProductPriceServer(String price, ShopProduct product){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("productPrice", price);
        map.put("shoppingListId", shopListId);
        map.put("productId", getProductIdFromList(product));

        Call<Void> call = retrofitManager.accessRetrofitInterface().updateProductPriceAtStore(map);
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

    //#########################
    //### share shopping list ###
    //#########################

    private void shareShoppingLogic(){
        Button sharePantryButton = findViewById(R.id.shareShoppingProduct);
        sharePantryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleShareShoppingIntent();
            }
        });
    }

    public void handleShareShoppingIntent(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String s = getString(R.string.share_message, getResources().getString(R.string.title_shopping),shopListId);
        sendIntent.putExtra(Intent.EXTRA_TEXT, s);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }


}