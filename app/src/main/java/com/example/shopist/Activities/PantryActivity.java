package com.example.shopist.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ServerPantryList;
import com.example.shopist.Server.ServerResponses.ServerPantryProduct;
import com.example.shopist.Server.ServerResponses.ServerProductImageUrl;
import com.example.shopist.Utils.Other.Adapter;
import com.example.shopist.Utils.CacheManager.ImageCacheManager;
import com.example.shopist.Utils.Other.ItemListAdapter;
import com.example.shopist.Product.Product;
import com.example.shopist.Utils.Other.ProdImage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import org.w3c.dom.Text;

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

    //testing purposes while we don't fix retriving the correct product info from server
    private ArrayList<ProdImage> productAndImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);
//        setContentView(R.layout.activity_pantry);
        retrofitManager = new RetrofitManager();
        existingPantryProducts = new ArrayList<ServerPantryProduct>();
        productAndImage = new ArrayList<ProdImage>();
        //add pantry products to list view
//        initCloudSettings();
        handleProductListDialog();
    }

    /*
    ##########################
    ### initial settings ###
    ##########################
     */

//    private void initCloudSettings(){
//        Map config = new HashMap();
//        config.put("cloud_name", "dy5jqy5fw");
//        config.put("api_key", "941312846299731");
//        config.put("api_secret", "1TjF4L4PRUT4K0r7bsTCWQYX12Q");
//        MediaManager.init(getApplicationContext(), config);
//    }

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
        renderProductImage(view, itemInfo);
    }

    private void renderProductImage(View view, Product itemInfo){
        String imageUrl = accessProuductImageUrl(itemInfo.getName());
        //check if product is cached
        if(ImageCacheManager.checkIfImageIsCached(imageUrl)){
            Log.d("imageLoading","PRODUCT IMAGE IN CACHE");
           //in cache
            Bitmap imageContent = ImageCacheManager.retrieveBitmapContent(imageUrl);
            renderFromBitmapContent(imageContent, view);
        }else{
            Log.d("imageLoading","PRODUCT IMAGE NOT IN CACHE");
            //not in cache
            renderProductImageFromServer(view,itemInfo);
        }
    }

    private void renderFromBitmapContent(Bitmap bitmap, View view){
        ImageView imageView = view.findViewById(R.id.productImageView);
        imageView.setImageBitmap(bitmap);
        Toast.makeText(getApplicationContext(),"CURRENT CACHE SIZE (in kB): "+ImageCacheManager.getCurrentCacheSize() ,Toast.LENGTH_SHORT).show();
    }

    private boolean checkIfIsInList(Product itemInfo){
        for(ProdImage pi : this.productAndImage){
            if(pi.getProductName().equals(itemInfo.getName())){
                return true;
            }
        }
        return false;
    }

    private String accessProuductImageUrl(String productName){
        String url="";
        for(ProdImage pi : this.productAndImage){
            if(pi.getProductName().equals(productName)){
                url = pi.getProductImageUrl();
                break;
            }
        }
        return url;
    }

    private void renderProductImageFromServer(View view, Product itemInfo){
        //check if the product image url is currently in small info cache
        //currently we are storing in a list, but we will change for a cache system
        if(checkIfIsInList(itemInfo)){
            //info in cache, retrieve from it
            String url = accessProuductImageUrl(itemInfo.getName());
            renderImage(view, url);
        }else{
            //ask server for image url
            Call<ServerProductImageUrl> call = retrofitManager.accessRetrofitInterface().getProductImageUrl(itemInfo.getName());
            call.enqueue(new Callback<ServerProductImageUrl>() {
                @Override
                public void onResponse(Call<ServerProductImageUrl> call, Response<ServerProductImageUrl> response) {
                    String url = response.body().getImageUrl();
                    productAndImage.add(new ProdImage(itemInfo.getName(),url));
                    renderImage(view, url);
                }

                @Override
                public void onFailure(Call<ServerProductImageUrl> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),"Server error." ,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void renderImage(View view, String url){
        ImageView imageView = view.findViewById(R.id.productImageView);
        //the below configuration is telling Glide to not use cache
        //we want to use our own cache (not theirs)
        Glide
                .with(getApplicationContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Bitmap bitmap = ((BitmapDrawable)resource).getBitmap();
                        //add photo to cache
                        ImageCacheManager.addPhotoToCache(url, bitmap);
                        Log.d("imageLoading","PRODUCT PHOTO ADDED TO CACHE SUCCESSFULY.");
                        imageView.setImageBitmap(bitmap);
                        Toast.makeText(getApplicationContext(),"CURRENT CACHE SIZE (in kB): "+ImageCacheManager.getCurrentCacheSize() ,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
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
                if (Integer.parseInt(amountToBeConsumed.getText().toString()) <= itemInfo.getStock()) {
                    if(MainActivityNav.withWifi){
                        //there is wifi
                        //consume product in server
                        consumeProductsInServer(itemInfo, amountToBeConsumed.getText().toString(), view);
                    }else{
                        //there is no wifi
                        TextView listNameComponent = findViewById(R.id.listName);
                        String listName = listNameComponent.getText().toString();
                        //consume product locally
                        MainActivityNav.smallDataCacheManager.consumeProductFromPantry(listName,itemInfo.getName());
                        //render update on frontend
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Not enough stock", Toast.LENGTH_SHORT).show();

                }
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
        Log.d("createshop",getProductIdFromList(itemInfo));
        map.put("shops", finalShops);
        Log.d("createshop",finalShops);
        map.put("needed",productNeeded[0].trim());
        Log.d("createshop",productNeeded[0].trim());

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
//        Button addProcuctButton = findViewById(R.id.addProductButton);
//        addProcuctButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                handleCreateProductDialog();
//            }
//        });

        FloatingActionButton button = findViewById(R.id.addProductBtn);
        button.setOnClickListener(new View.OnClickListener() {
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
//        Map config = new HashMap();
//        config.put("cloud_name", "dy5jqy5fw");
//        config.put("api_key", "941312846299731");
//        config.put("api_secret", "1TjF4L4PRUT4K0r7bsTCWQYX12Q");
//        MediaManager.init(getApplicationContext(), config);
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
                Toast.makeText(getApplicationContext(), currentUploadedPhoto, Toast.LENGTH_SHORT).show();
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