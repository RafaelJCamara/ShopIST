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

import com.example.shopist.Activities.ui.cart.CartActivity;
import com.example.shopist.Product.ShopProduct;
import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ServerClassificationHistogram;
import com.example.shopist.Server.ServerResponses.ServerProductClassification;
import com.example.shopist.Server.ServerResponses.ServerShoppingList;
import com.example.shopist.Server.ServerResponses.ServerShoppingProduct;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopActivity extends AppCompatActivity {

    private RetrofitManager retrofitManager;

    public ListView listView;
    private ArrayList<String> listContent = new ArrayList<String>();
    private ArrayList<ShopProduct> shopProductList = new ArrayList<ShopProduct>();

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        listContent.clear();
        //add shopping products to list view
        handleProductListDialog();
    }

    private void handleProductListDialog(){
        productListSettings();
        fillPantryProductList();
    }

    private void productListSettings() {
        //configure product list and adapter
        fillListContentSettings();
        //add product click listener
        addProductListClickListener();
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
                ShopProduct product = getProductFromShopProductList(itemInfo);
                if(product != null)
                    handleProductDetailDialog(product);
                else
                    Toast.makeText(ShopActivity.this, "error getting product",Toast.LENGTH_SHORT).show();

            }
        });
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

            String productInfo=prod.getName()+"; Needed:"+prod.getNeeded()+"; price:" + prod.getPrice();
            listContent.add(productInfo);
            shopProductList.add(p);
        }
        fillListContentSettings();
    }

    private void fillTextView(){
        TextView listNameView = findViewById(R.id.shopListName);
        TextView listCodeView = findViewById(R.id.shopListCode);
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
                getClassificationFromServer(view, product);
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

            if(product.getName().equals(prod.getName()) &&
                    product.getNeeded()== prod.getNeeded()
            ){
                productId+=prod.getProductId();
            }
        }
        Log.d("getprodidfromlist:", productId);
        return productId;
    }

    private ShopProduct getProductFromShopProductList(String itemInfo) {
        String[] prodInfo = itemInfo.split(";");

        ShopProduct product = null;
        String productName = prodInfo[0].trim();

        for(ShopProduct prod:this.shopProductList){
            if(productName.equals(prod.getName())){
                product = prod;
            }
        }
        Log.d("getprodfromshoplist", product.getName() +" "+ product.getPrice() );
        return product;
    }

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
    public void getClassificationFromServer(View view, ShopProduct itemInfo){
        String productId = getProductIdFromList(itemInfo);
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

 /*   public void getClassificationHistFromServer(View view, ShopProduct itemInfo){
        String productId = getProductIdFromList(itemInfo);
        Call<ServerClassificationHistogram> call = retrofitManager.accessRetrofitInterface().getRatingHist(productId);
        call.enqueue(new Callback<ServerClassificationHistogram>() {
            @Override
            public void onResponse(Call<ServerClassificationHistogram> call, Response<ServerClassificationHistogram> response) {
                ServerClassificationHistogram classification = response.body();
                TextView classificationText = view.findViewById(R.id.classificationTextView);

                if(classification.getClassification()>0)
                    classificationText.setText(String.format("%.1f", classification.getClassification()));
                else
                    classificationText.setText("Not rated yet");
                //classificationText.setText(String.valueOf(classification.getClassification()));

            }

            @Override
            public void onFailure(Call<ServerClassificationHistogram> call, Throwable t) {
                Toast.makeText(ShopActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
                Log.d("getServerClassification", "on failure");
            }
        });
    }
*/

    public void updateProductRating(ShopProduct itemInfo, String classification){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("productRating", classification);
        String productId = getProductIdFromList(itemInfo);

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

}