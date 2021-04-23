package com.example.shopist.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.shopist.Utils.Adapter;
import com.example.shopist.Utils.ItemListAdapter;
import com.example.shopist.Product.Product;


import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantryActivity extends AppCompatActivity {

    private RetrofitManager retrofitManager;

    public ListView listView;
    private ArrayList<Product> productsList = new ArrayList<Product>();

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

        TextView productNeededDetail = view.findViewById(R.id.neededProductDetail);
        productNeededDetail.setText(String.valueOf(itemInfo.getNeeded()));

        fillListViewWithShoppingLists(view, itemInfo);
    }

    private void fillListViewWithShoppingLists(View view, Product itemInfo){
        ArrayList<String> shopList = (ArrayList<String>) getIntent().getSerializableExtra("shoppingLists");
        this.recyclerView = view.findViewById(R.id.shopListDetail);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setHasFixedSize(true);
        Adapter adapter = new Adapter(shopList);
        recyclerView.setAdapter(adapter);

        addSaveButtonLogic(adapter, view, itemInfo);
    }

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

    private void sendUpdateToServer(ArrayList<String> getSelectedShops, View view, Product itemInfo){
        String finalShops = "";
        for(String s: getSelectedShops){
            String[] shopInfo = s.split("->");
            finalShops+=shopInfo[1]+",";
        }
//        Toast.makeText(view.getContext(), finalShops,Toast.LENGTH_SHORT).show();

        TextView productNeededComponent = view.findViewById(R.id.neededProductDetail);
        String[] productNeeded = productNeededComponent.getText().toString().split(":");

        HashMap<String,String> map = new HashMap<String,String>();
        map.put("productId", getProductIdFromList(itemInfo));
        map.put("shops", finalShops);
        map.put("needed",productNeeded[1].trim());

        Call<Void> call = retrofitManager.accessRetrofitInterface().updatePantry(listId,map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                Toast.makeText(getApplicationContext(),"Updated with success. TEST :"+response ,Toast.LENGTH_SHORT).show();
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
                && String.valueOf(itemInfo.getNeeded()).trim().equals(String.valueOf(prod.getNeeded())) && String.valueOf(itemInfo.getStock()).trim().equals(String.valueOf(prod.getStock()))
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
        for(ServerPantryProduct prod : list){
            Product product = new Product(prod.getName(), prod.getDescription(), prod.getStock(), prod.getNeeded());
            String productInfo=prod.getName()+"; "+prod.getDescription()+"; Needed:"+prod.getNeeded()+"; Stock:"+prod.getStock();
            productsList.add(product);
        }
        fillListContentSettings();
    }

    /*
    ##########################
    ### create new product ###
    ##########################
     */

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
        builder.setView(view).show();
        createProductLogic(view);
    }

    public void createProductLogic(View view){
        Button createProductButton = view.findViewById(R.id.CreateProductButton);
        createProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePantryListProductCreation(view);
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
        Product product = new Product(productName, productDescription, Integer.parseInt(needed), Integer.parseInt(stock));
        productsList.add(product);
        fillListContentSettings();
    }

}