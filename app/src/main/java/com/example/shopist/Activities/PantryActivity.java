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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ServerPantryList;
import com.example.shopist.Server.ServerResponses.ServerPantryProduct;
import com.example.shopist.Server.ServerResponses.ServerShoppingList;
import com.example.shopist.Utils.Adapter;

import com.example.shopist.Utils.ItemListAdapter;
import com.example.shopist.Product.Product;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantryActivity extends AppCompatActivity {

    private RetrofitManager retrofitManager;

    public ListView listView;
    private ArrayList<Product> productsList = new ArrayList<Product>();
    private ArrayList<String> shoppingLists = new ArrayList<String>();

    private String listId;

    private RecyclerView recyclerView;

    private ArrayList<ServerPantryProduct> existingPantryProducts;

    private ItemListAdapter itemListAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);
        retrofitManager = new RetrofitManager();
        existingPantryProducts = new ArrayList<ServerPantryProduct>();
        getAllShopListFromServer();


        //add pantry products to list view
        handleProductListDialog();
    }

    /*
    ##########################
    ### initial settings ###
    ##########################
     */

    private void handleProductListDialog(){
        productListSettings();
        fillPantryProductList();
        addProductLogic();
        sharePantryLogic();
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
        listView = findViewById(R.id.productListInfo);

        //create list adapter
        itemListAdapter = new ItemListAdapter(this, productsList);

        //add adapter to list
        listView.setAdapter(itemListAdapter);
    }

    private void addProductListClickListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product itemInfo = (Product) parent.getAdapter().getItem(position);
                handleProductDetailDialog(itemInfo);
            }
        });
    }

    private void fillTextView(){
        String listInfo = getIntent().getStringExtra("itemInfo");
        String [] values = listInfo.split("->");
        TextView listNameView = findViewById(R.id.listName);
        TextView listCodeView = findViewById(R.id.listCode);
        listNameView.setText(values[0]);
        listId = values[1];
        listCodeView.setText(listId);
    }

    private void handleProductDetailDialog(Product itemInfo){
        View view = getLayoutInflater().inflate(R.layout.product_detail_and_shops,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(PantryActivity.this);
        builder.setView(view).show();
        handleBuyInShopsLogic(view, itemInfo);
    }

    private void handleBuyInShopsLogic(View view, Product itemInfo){

        TextView productNameDetail = view.findViewById(R.id.productNameDetail);
        productNameDetail.setText(itemInfo.getName());

        TextView productDescriptionDetail = view.findViewById(R.id.productDescriptionDetail);
        productDescriptionDetail.setText(itemInfo.getDescription());

        TextView productStockDetail = view.findViewById(R.id.productStockDetail);
        productStockDetail.setText(String.valueOf(itemInfo.getStock()));

        TextView productNeededDetail = view.findViewById(R.id.productNeededDetail);
        productNeededDetail.setText(String.valueOf(itemInfo.getNeeded()));

        fillListViewWithShoppingLists(view, itemInfo);
    }

    private void fillListViewWithShoppingLists(View view, Product itemInfo){
        //ArrayList<String> shopList = (ArrayList<String>) getIntent().getSerializableExtra("shoppingLists");
        Log.i("before set adapter", "*******");
        for(String s: shoppingLists){
            Log.i("before set adapter", s);
        }
        this.recyclerView = view.findViewById(R.id.shopListDetail);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setHasFixedSize(true);

        Adapter adapter = new Adapter(shoppingLists);
        recyclerView.setAdapter(adapter);

        addSaveButtonLogic(adapter, view, itemInfo);
        addConsumeProductLogic(view, itemInfo);
    }


    private void getAllShopListFromServer() {
        Log.i("msg pantry act","*******");
        Call<ArrayList<ServerShoppingList>> call = retrofitManager.accessRetrofitInterface().syncAllShoppingList();
        call.enqueue(new Callback<ArrayList<ServerShoppingList>>() {
            @Override
            public void onResponse(Call<ArrayList<ServerShoppingList>> call, Response<ArrayList<ServerShoppingList>> response) {
                if(response.code()==200){
                    //list retrieved by the server
                    ArrayList<ServerShoppingList> lists = response.body();
                    for(ServerShoppingList list : lists){
                        shoppingLists.add(list.getName() + "->"+list.getUuid());
                        Log.i("msg pantry act",list.getName() + " PA");
                    }
                }
            }
            @Override
            public void onFailure(Call<ArrayList<ServerShoppingList>> call, Throwable t) {
                Toast.makeText(PantryActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    //Should return just the shopping list associated with the user/pantryLists
    private void addSaveButtonLogic(Adapter adapter, View view, Product itemInfo){
        Button saveButton = view.findViewById(R.id.productShoppingDetailSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> getSelectedShops = adapter.getSelectedShopping();
//                for(String s: getSelectedShops){
//                    Toast.makeText(view.getContext(), s,Toast.LENGTH_SHORT).show();
//                }
                sendUpdateToServer(getSelectedShops, view, itemInfo);
            }
        });
    }

    private void addConsumeProductLogic(View view, Product itemInfo){
        Button consumeProductButton = view.findViewById(R.id.consumeProductAtPantry);
        consumeProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get amount to be consumed
                EditText amountToBeConsumed = view.findViewById(R.id.amountToConsume);
                if(Integer.parseInt(amountToBeConsumed.getText().toString()) <= itemInfo.getStock())
                    consumeProductsInServer(itemInfo, amountToBeConsumed.getText().toString(), view);
                else
                    Toast.makeText(getApplicationContext(),"Not enough stock" ,Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void consumeProductsInServer(Product itemInfo, String quantityConsumed, View view){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("productId", getProductIdFromList(itemInfo));
        map.put("quantity", quantityConsumed);

        Call<Void> call = retrofitManager.accessRetrofitInterface().consumeProductPantry(listId,map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(getApplicationContext(),"Updated with success." ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Server error." ,Toast.LENGTH_SHORT).show();
            }
        });
        updateInFrontendPantryAfterConsumed(itemInfo, quantityConsumed, view);
    }


    private void updateInFrontendPantryAfterConsumed(Product itemInfo, String quantityConsumed, View view){
        Log.i("Beginning","*******");
        Log.i("Beginning",itemInfo.getName());
        Log.i("Beginning",itemInfo.getDescription());
        Log.i("Beginning",String.valueOf(itemInfo.getNeeded()));
        Log.i("Beginning",String.valueOf(itemInfo.getStock()));
        Log.i("Beginning","*******");
        String finalS = null;
        int index = -1;
        for(int i=0;i!=this.productsList.size();i++){
            if(productsList.get(i).getName().equals(itemInfo.getName())){
                int needed = itemInfo.getNeeded() + Integer.parseInt(quantityConsumed);
                int stock = itemInfo.getStock() - Integer.parseInt(quantityConsumed);
                itemInfo.setNeeded(needed);
                itemInfo.setStock(stock);
                finalS = itemInfo.getName()+";"+itemInfo.getDescription()+";"+"Needed:"+needed+";"+"Stock:"+stock;
                index = i;
                Product product = new Product(itemInfo.getName(), itemInfo.getDescription(), stock, needed);
                productsList.set(index,product);

                TextView productStockDetail = view.findViewById(R.id.productStockDetail);
                productStockDetail.setText(String.valueOf(stock));

                TextView productNeededDetail = view.findViewById(R.id.productNeededDetail);
                productNeededDetail.setText(String.valueOf(needed));
            }
        }

        Log.i("Beginning",String.valueOf(index));
        Log.i("Beginning",finalS);

        fillListContentSettings();
    }


    private void sendUpdateToServer(ArrayList<String> getSelectedShops, View view, Product itemInfo){
        String finalShops = "";
        for(String s: getSelectedShops){
            String[] shopInfo = s.split("->");
            finalShops+=shopInfo[1]+",";
        }
//        Toast.makeText(view.getContext(), finalShops,Toast.LENGTH_SHORT).show();


        HashMap<String,String> map = new HashMap<String,String>();
        map.put("productId", getProductIdFromList(itemInfo));
        map.put("shops", finalShops);
        map.put("needed",String.valueOf(itemInfo.getNeeded()));

        Call<Void> call = retrofitManager.accessRetrofitInterface().updatePantry(listId,map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(getApplicationContext(),"Updated with success." ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Server error." ,Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getProductIdFromList(Product itemInfo){
        String productId ="";
        for(ServerPantryProduct prod:this.existingPantryProducts){

            if(itemInfo.getName().trim().equals(prod.getName()) && itemInfo.getDescription().trim().equals(prod.getDescription())
            //String.valueOf(itemInfo.getNeeded()).trim().equals(String.valueOf(prod.getNeeded())) && String.valueOf(itemInfo.getStock()).trim().equals(String.valueOf(prod.getStock())
            ){
                productId+=prod.getProductId();
            }
        }
        return productId;
    }

    private void fillPantryProductList(){
        //ask the server for information
        Call<ServerPantryList> call = retrofitManager.accessRetrofitInterface().syncPantryList(listId);
        call.enqueue(new Callback<ServerPantryList>() {
            @Override
            public void onResponse(Call<ServerPantryList> call, Response<ServerPantryList> response) {
                if(response.code()==200){
                    //list retrieved by the server
                    ServerPantryList list = response.body();
                    renderLists(list.getProducts());
                }
            }
            @Override
            public void onFailure(Call<ServerPantryList> call, Throwable t) {
                Toast.makeText(PantryActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderLists(ArrayList<ServerPantryProduct> list){
        this.existingPantryProducts = list;
        productsList = new ArrayList<Product>();
        for(ServerPantryProduct prod : list){
            Product product = new Product(prod.getName(), prod.getDescription(), prod.getStock(), prod.getNeeded());
            String productInfo=prod.getName()+"; "+prod.getDescription()+"; Needed:"+prod.getNeeded()+"; Stock:"+prod.getStock();
            productsList.add(product);
        }
        fillListContentSettings();
    }

    //##########################
    //### create new product ###
    //##########################


    private void addProductLogic(){
        Button addProcuctButton = findViewById(R.id.addProductButton);
        addProcuctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCreateProductDialog();
            }
        });
    }

    public void handleCreateProductDialog(){
        View view = getLayoutInflater().inflate(R.layout.create_product,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog alert = builder.create();
        alert.show();
        createProductLogic(view, alert);
    }

    public void createProductLogic(View view, AlertDialog builder){
        Button createProductButton = view.findViewById(R.id.CreateProductButton);
        createProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePantryListProductCreation(view);
                builder.cancel();
            }
        });
    }

    public void handlePantryListProductCreation(View view){
        //product name
        EditText productNameComponent = view.findViewById(R.id.edit_product_name);
        String productName = productNameComponent.getText().toString();

        //product description
        EditText productDescriptionComponent = view.findViewById(R.id.edit_product_description);
        String productDescription = productDescriptionComponent.getText().toString();

        //product barcode
        EditText productBarcodeComponent = view.findViewById(R.id.productBarcode);
        String productBarcode = productBarcodeComponent.getText().toString();

        //product stock
        EditText productStockComponent = view.findViewById(R.id.productStockQuantity);
        String productStockQuantity = productStockComponent.getText().toString();

        //product needed
        EditText productNeededComponent = view.findViewById(R.id.productNeededQuantity);
        String productNeededQuantity = productNeededComponent.getText().toString();

        //create product in the server
        createProductInServer(productName, productDescription, productBarcode, productStockQuantity, productNeededQuantity);
    }

    public void createProductInServer(String productName, String productDescription, String productBarcode, String productStock, String productNeeded){
        HashMap<String,String> map = new HashMap<>();
        map.put("name",productName);
        map.put("description", productDescription);
        map.put("barcode", productBarcode);
        map.put("stock", productStock);
        map.put("needed", productNeeded);

        Call<Void> call = retrofitManager.accessRetrofitInterface().addProductToPantry(listId,map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(getApplicationContext(),"Product added with success." ,Toast.LENGTH_SHORT).show();

                //update the products in the frontend
                renderNewProduct(productName,productDescription,productNeeded,productStock);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Server error." ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderNewProduct(String productName, String productDescription, String needed, String stock){
        Product product = new Product(productName, productDescription, Integer.parseInt(stock), Integer.parseInt(needed));
        productsList.add(product);
        fillPantryProductList();
        fillListContentSettings();
    }

    //#########################
    //### share pantry list ###
    //#########################

    private void sharePantryLogic(){
        Button sharePantryButton = findViewById(R.id.sharePantryButton);
        sharePantryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSharePantryIntent();
            }
        });
    }

    public void handleSharePantryIntent(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}