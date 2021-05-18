package com.example.shopist.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cloudinary.utils.StringUtils;
import com.example.shopist.Activities.ui.cart.CartActivity;
import com.example.shopist.Product.ShopProduct;
import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ServerClassificationHistogram;
import com.example.shopist.Server.ServerResponses.ServerProductClassification;
import com.example.shopist.Server.ServerResponses.ServerShoppingList;
import com.example.shopist.Server.ServerResponses.ServerShoppingProduct;
import com.example.shopist.Server.ServerResponses.UserAccess;
import com.example.shopist.Server.ServerResponses.WaitTimeInfo;
import com.example.shopist.Utils.Other.ItemListAdapter;
import com.example.shopist.Utils.Other.SimpleCallback;
import com.example.shopist.Utils.Other.Adapter;
import com.example.shopist.Utils.Other.PublicInfoManager;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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
    //private ArrayList<String> listContent = new ArrayList<String>();

    private ShopViewModel shopViewModel;

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

        shopViewModel = new ViewModelProvider(this).get(ShopViewModel.class);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //listContent.clear();
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
        getShoppingProductsFromServer();
        shareShoppingLogic();
    }

    private void productListSettings() {
        //configure product list and adapter
        fillListContentSettings();
        //add product click listener
        //addProductListClickListener();
        //add waiting time button logic
        addWaitTimeLogic();
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

        //fill listContent from ShopProductList with strings to use on list adapter
        /*for(ShopProduct prod : shopProductList) {
            String productInfo = prod.getName() + "; Needed:" + prod.getNeeded() + "; price:" + prod.getPrice();
            listContent.add(productInfo);
        }*/
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
                if(shopProduct.getPrice()==0){
                    handleUserPromptDialog(shopProduct, (args) -> {
                        updateProductInfo(shopProduct, (args2) -> {
                            dialog.dismiss();
                        });
                    });
                } else {
                    updateProductInfo(shopProduct, (args2) -> {
                        dialog.dismiss();
                    });
                }
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


        //Set product classification in view
        getClassificationFromServer(view, shopProduct);

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
        //ratingBar.setRating(shopProduct.getRating());
        /*ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            //update information in server
            updateProductRating(shopProduct, String.valueOf(rating));
        });*/

        ratingButton = view.findViewById(R.id.submitProdClassification);

        ratingButton.setOnClickListener(v -> {
            //Classification of Shopping product
            String productRating = String.valueOf(ratingBar.getRating());
            Toast.makeText(getApplicationContext(), productRating, Toast.LENGTH_LONG).show();

            //update information in server

            updateProductRating(shopProduct, productRating);
            getClassificationFromServer(view, shopProduct);
        });

        ImageButton ratingHistButton = view.findViewById(R.id.ratingHistButton);

        ratingHistButton.setOnClickListener(v -> {
            handleRatingHist(shopProduct);
        });

    }

    /*private void addProductListClickListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemInfo = (String) parent.getAdapter().getItem(position);
                //Toast.makeText(ShopActivity.this, itemInfo,Toast.LENGTH_SHORT).show();
                ShopProduct product = getProductFromShopProductList(itemInfo);
                if(product != null)
                    handleProductDetailDialog(product);
                else
                    Toast.makeText(ShopActivity.this, "error getting product",Toast.LENGTH_SHORT).show();

            }
        });
    }
*/
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
        Call<WaitTimeInfo> call = retrofitManager.accessRetrofitInterface().getCurrentWaitingTime(this.shopListId);
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
            //sProduct.setRating(prod.getRating());
            shopViewModel.getProductList().getValue().add(sProduct);

            String productInfo=prod.getName()+"; Needed:"+prod.getNeeded()+"; price:" + prod.getPrice();
            //shopProductList.add(p);
        }
        fillListContentSettings();
    }

    private void fillTextView(){
        TextView listNameView = findViewById(R.id.shopListName);

        PublicInfoManager.currentShopUuid = shopListId;
        listNameView.setText(this.shopListName);
    }

    /*private void handleProductDetailDialog(String itemInfo){
        View view = getLayoutInflater().inflate(R.layout.update_product_store,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
        builder.setView(view).show();
        handleProductUpdateInShopLogic(view, product);
    }*/

    /*private void handleProductUpdateInShopLogic(View view, ShopProduct shopProduct){
        //String[] prodInfo = itemInfo.split(";");
        //String productPrice[] = prodInfo[2].split(":");

        RatingBar ratingBar;
        Button ratingButton;
        //Set product name in view
        TextView productNameInStore = view.findViewById(R.id.productNameAtStore);
        productNameInStore.setText(product.getName());

        //Set product classification in view
        getClassificationFromServer(view, product);
        //getClassificationHistFromServer(view, product);
        //updateProductRatingFrontend(view, product);
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
                getClassificationFromServer(view, product);
            }
        });
    }

    private void updateProductInfo(String quantity, String price, String itemInfo, SimpleCallback... callbacks){
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

            if(product.getName().equals(prod.getName()) &&
                    product.getNeeded()== prod.getNeeded()
            ){
                productId+=prod.getProductId();
            }
        }
        Log.d("getprodidfromlist:", productId);
        return productId;
    }*/

    /*private String getProductRatingFromList(String productId) {
        String[] prodInfo = itemInfo.split(";");

        ShopProduct product = null;
        String productName = prodInfo[0].trim();
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

        for(ShopProduct prod:this.shopProductList){
            if(productName.equals(prod.getName())){
                product = prod;
            }
        }
        Log.d("getprodfromshoplist", product.getName() +" "+ product.getPrice() );
        return product;
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
    public void getClassificationFromServer(View view, ShopProduct shopProduct){
        String productId = shopProduct.getId();
        Call<ServerProductClassification> call = retrofitManager.accessRetrofitInterface().getProductRating(productId);
        call.enqueue(new Callback<ServerProductClassification>() {
            @Override
            public void onResponse(Call<ServerProductClassification> call, Response<ServerProductClassification> response) {
                    ServerProductClassification classification = response.body();
                    TextView classificationText = view.findViewById(R.id.classificationTextView);

                    if(classification.getClassification()>0)
                        classificationText.setText(String.format("%.1f", classification.getClassification()));
                    else
                        classificationText.setText("Not rated yet");
                    //classificationText.setText(String.valueOf(classification.getClassification()));

            }

            @Override
            public void onFailure(Call<ServerProductClassification> call, Throwable t) {
                Toast.makeText(ShopActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
                Log.d("getServerClassification", "on failure");
            }
        });
    }


    public void updateProductRating(ShopProduct itemInfo, String classification){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("productRating", classification);

        Log.d("Update Product Rating", "updating " + classification + " Id:" +itemInfo.getId());

        Call<Void> call = retrofitManager.accessRetrofitInterface().rateProductAtStore(itemInfo.getId(), map);
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

    private void handleRatingHist(ShopProduct product){
        View view = getLayoutInflater().inflate(R.layout.rating_histogram,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
        AlertDialog dialog = builder.setView(view).create();
        dialog.show();
        getClassificationHistFromServer(view, product);
    }

    public void getClassificationHistFromServer(View view, ShopProduct itemInfo){
        String productId = itemInfo.getId();
        Call<ServerClassificationHistogram> call = retrofitManager.accessRetrofitInterface().getRatingHist(productId);
        call.enqueue(new Callback<ServerClassificationHistogram>() {
            @Override
            public void onResponse(Call<ServerClassificationHistogram> call, Response<ServerClassificationHistogram> response) {
                ServerClassificationHistogram classification = response.body();
                Log.d("getClassificationHistFromServer", "1" +String.valueOf(classification.getC1()));
                Log.d("getClassificationHistFromServer", "2" +String.valueOf(classification.getC2()));
                Log.d("getClassificationHistFromServer", "3" +String.valueOf(classification.getC3()));
                Log.d("getClassificationHistFromServer", "4" +String.valueOf(classification.getC4()));
                Log.d("getClassificationHistFromServer", "5" +String.valueOf(classification.getC5()));
                renderHist(classification, view);
            }

            @Override
            public void onFailure(Call<ServerClassificationHistogram> call, Throwable t) {
                Toast.makeText(ShopActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
                Log.d("getServerClassification", "on failure");
            }
        });
    }

    public void renderHist(ServerClassificationHistogram hist, View view){
        ArrayList<BarEntry> yVals = new ArrayList<>();

        yVals.add(new BarEntry(1, hist.getC1()));
        yVals.add(new BarEntry(2, hist.getC2()));
        yVals.add(new BarEntry(3, hist.getC3()));
        yVals.add(new BarEntry(4, hist.getC4()));
        yVals.add(new BarEntry(5, hist.getC5()));


        HorizontalBarChart chart = (HorizontalBarChart) view.findViewById(R.id.idHorizontalBarChart);
        TextView productNameText = view.findViewById(R.id.product_name);
        productNameText.setText(hist.getProductName());

        BarDataSet set1;
        set1 = new BarDataSet(yVals, "Ratings");

        set1.setColors(Color.parseColor("#F78B5D"), Color.parseColor("#FCB232"), Color.parseColor("#FDD930"), Color.parseColor("#ADD137"), Color.parseColor("#A0C25A"));

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);

        // hide Y-axis
        YAxis left = chart.getAxisLeft();
        left.setDrawLabels(false);

        // custom X-axis labels
        String[] values = new String[] { "1 star", "2 stars", "3 stars", "4 stars", "5 stars"};
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

        chart.setData(data);

        // custom description
        Description description = new Description();
        description.setText("Rating");
        chart.setDescription(description);

        // hide legend
        chart.getLegend().setEnabled(false);

        chart.animateY(1000);
        chart.invalidate();
    }

    public class MyXAxisValueFormatter extends ValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int) value];
        }
    }

        private ArrayList<BarEntry> getDataSet() {

            ArrayList<BarEntry> valueSet1 = new ArrayList<>();

            BarEntry v1e2 = new BarEntry(1, 4341f);
            valueSet1.add(v1e2);
            BarEntry v1e3 = new BarEntry(2, 3121f);
            valueSet1.add(v1e3);
            BarEntry v1e4 = new BarEntry(3, 5521f);
            valueSet1.add(v1e4);
            BarEntry v1e5 = new BarEntry(4, 10421f);
            valueSet1.add(v1e5);
            BarEntry v1e6 = new BarEntry(5, 27934f);
            valueSet1.add(v1e6);

            return valueSet1;
        }

    /*public class CategoryBarChartXaxisFormatter extends ValueFormatter {

        ArrayList<String> mValues;

        public CategoryBarChartXaxisFormatter(ArrayList<String> values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            int val = (int) value;
            String label = "";
            if (val >= 0 && val < mValues.size()) {
                label = mValues.get(val);
            } else {
                label = "";
            }
            return label;
        }
    }*/


    private void handleUserPromptDialog(ShopProduct product, SimpleCallback... callbacks){
        View view = getLayoutInflater().inflate(R.layout.user_prompt_price,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
        builder.setOnDismissListener(dialog -> {
            for(SimpleCallback callback : callbacks) {
                callback.callback();
            }
        });
        AlertDialog dialog = builder.setView(view).create();
        dialog.show();
        handleProductUpdateUserPrompt(view, product, dialog, callbacks);
    }

    private void parseProduct(View v, ShopProduct product) {

        EditText price = v.findViewById(R.id.productPriceField);
        EditText qty = v.findViewById(R.id.productQuantityField);

        product.setPrice(Double.parseDouble(price.getText().toString()));
        product.setQuantity(Long.parseLong(qty.getText().toString()));

    }
    
    //user prompt


    private void handleProductUpdateUserPrompt(View view, ShopProduct product, AlertDialog dialog, SimpleCallback... callbacks) {

        //Set product name in view
        TextView productNameInStore = view.findViewById(R.id.product_name);
        productNameInStore.setText(product.getName());

        //add save button
        Button saveProductInfoButton = view.findViewById(R.id.update_button);
        saveProductInfoButton.setOnClickListener(v -> {
            //get price in store
            EditText productPriceStoreComponent = view.findViewById(R.id.new_price);
            String productPriceStore = productPriceStoreComponent.getText().toString();

            if(StringUtils.isBlank(productPriceStore) || Double.parseDouble(productPriceStore) == 0) {
                Toast.makeText(ShopActivity.this, getResources().getString(R.string.new_price_not_set), Toast.LENGTH_SHORT).show();
                return;
            }

            product.setPrice(Double.parseDouble(productPriceStore));

            dialog.dismiss();

            //Toast.makeText(ShopActivity.this, "Product clicked... " + productPriceStore, Toast.LENGTH_SHORT).show();

            //update information in server
            //updateProductPriceServer(productPriceStore, product, callbacks);
        });

        Button notNowButton = view.findViewById(R.id.notNowButton);
        notNowButton.setOnClickListener(v -> dialog.dismiss());
    }


    private void updateProductPriceServer(String price, ShopProduct product, SimpleCallback... callbacks){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("productPrice", price);
        map.put("shoppingListId", shopListId);
        map.put("productId", product.getId());

        Call<Void> call = retrofitManager.accessRetrofitInterface().updateProductPriceAtStore(map);
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