package com.example.shopist.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ServerPantryList;
import com.example.shopist.Server.ServerResponses.ServerPantryProduct;
import com.example.shopist.Utils.Adapter;
import com.example.shopist.Utils.ImageManager;
import com.example.shopist.Utils.ItemListAdapter;
import com.example.shopist.Product.Product;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    private static final int PERMISSION_CODE = 1;
    private static final int PICK_IMAGE = 1;
    String filePath;
    String currentUploadedPhoto;


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

        TextView productNeededDetail = view.findViewById(R.id.productNeededDetail);
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
        addConsumeProductLogic(view, itemInfo);
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

        TextView productNeededComponent = view.findViewById(R.id.productNeededDetail);
        String[] productNeeded = productNeededComponent.getText().toString().split(":");

        HashMap<String,String> map = new HashMap<String,String>();
        map.put("productId", getProductIdFromList(itemInfo));
        map.put("shops", finalShops);
        map.put("needed",productNeeded[1].trim());

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
        builder.setView(view);
        AlertDialog alert = builder.create();
        alert.show();
        createProductLogic(view, alert);
        takeProductPhotoSettings(view);
    }

    /*
    * Create product logic
    * */

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
        map.put("imageUrl", currentUploadedPhoto);

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


    /*
    * Upload photo
    * */

    public void takeProductPhotoSettings(View view){
        addPhotoLogic(view);
//        addUploadLogic();
    }

    private void addPhotoLogic(View view){
        Map config = new HashMap();
        config.put("cloud_name", "dy5jqy5fw");
        config.put("api_key", "941312846299731");
        config.put("api_secret", "1TjF4L4PRUT4K0r7bsTCWQYX12Q");
        MediaManager.init(getApplicationContext(), config);
        addPhotoButtonLogic(view);
    }
    private void addPhotoButtonLogic(View view){
        Button photoButton = view.findViewById(R.id.photo_button);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //request permission to access external storage
                requestPermission();
            }
        });
    }
    private void requestPermission(){
        if(ContextCompat.checkSelfPermission
                (getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
        ){
            accessTheGallery();
        } else {
            ActivityCompat.requestPermissions(
                    PantryActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode== PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                accessTheGallery();
            }else {
                Toast.makeText(getApplicationContext(), "permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void accessTheGallery(){
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //get the image’s file location
        filePath = getRealPathFromUri(data.getData(), PantryActivity.this);
        addUploadLogic();
    }
    private String getRealPathFromUri(Uri imageUri, Activity activity){
        Cursor cursor = activity.getContentResolver().query(imageUri, null,  null, null, null);
        if(cursor==null) {
            return imageUri.getPath();
        }else{
            cursor.moveToFirst();
            int idx =  cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void addUploadLogic(){
        uploadToCloudinary(filePath);
    }

    private void uploadToCloudinary(String filePath) {
        MediaManager.get().upload(filePath).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
            }
            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
            }
            @Override
            public void onSuccess(String requestId, Map resultData) {
                currentUploadedPhoto = resultData.get("url").toString();
            }
            @Override
            public void onError(String requestId, ErrorInfo error) {
            }
            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
            }
        }).dispatch();
    }





}