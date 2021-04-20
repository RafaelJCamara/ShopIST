package com.example.shopist.Activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitInterface;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ServerListToken;
import com.example.shopist.Utils.ItemListAdapter;
import com.example.shopist.product.ProductClass;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PantryActivity extends AppCompatActivity {

    private RetrofitManager retrofitManager;

    private ArrayList<ProductClass> productList;
    private ListView itemListView;
    private ItemListAdapter itemListAdapter;
    private String listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        retrofitManager = new RetrofitManager();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);
        productList = new ArrayList<ProductClass>();
        handleProductListDialog();
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

    private void handleProductListDialog(){
        productListSettings();
        addProductLogic();
    }

    private void productListSettings() {
        //configure product list and adapter
        itemListView = findViewById(R.id.itemList);
        itemListAdapter = new ItemListAdapter(this, productList);
        itemListView.setAdapter(itemListAdapter);
        fillTextView();
    }

    private void addProductLogic(){
        Button addProcuctButton = findViewById(R.id.addProductButton);
        addProcuctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCreateProductDialog();
                Toast.makeText(getApplicationContext(),"addProductButton" ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    ##########################
    ### create new product ###
    ##########################
     */

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
        Toast.makeText(getApplicationContext(),productName ,Toast.LENGTH_SHORT).show();

        //product description
        EditText productDescriptionComponent = view.findViewById(R.id.edit_product_description);
        String productDescription = productDescriptionComponent.getText().toString();
        Toast.makeText(getApplicationContext(),productDescription ,Toast.LENGTH_SHORT).show();

        //product barcode
        EditText productBarcodeComponent = view.findViewById(R.id.productBarcode);
        String productBarcode = productBarcodeComponent.getText().toString();
        Toast.makeText(getApplicationContext(),productBarcode ,Toast.LENGTH_SHORT).show();

        //product stock
        EditText productStockComponent = view.findViewById(R.id.productStockQuantity);
        String productStockQuantity = productStockComponent.getText().toString();
        Toast.makeText(getApplicationContext(),productStockQuantity ,Toast.LENGTH_SHORT).show();

        //product needed
        EditText productNeededComponent = view.findViewById(R.id.productNeededQuantity);
        String productNeededQuantity = productNeededComponent.getText().toString();
        Toast.makeText(getApplicationContext(),productNeededQuantity ,Toast.LENGTH_SHORT).show();

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
                Toast.makeText(getApplicationContext(),"Product add with success." ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Server error." ,Toast.LENGTH_SHORT).show();
            }
        });
    }

}