package com.example.shopist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ServerPantryProduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantryActivity extends AppCompatActivity {

    private RetrofitManager retrofitManager;

    public ListView listView;
    public ArrayList<String> listContent = new ArrayList<String>();

    private String listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);
        retrofitManager = new RetrofitManager();
        handleProductListDialog();
    }

    /*
    ##########################
    ### initial setting ###
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
        fillTextView();
    }

    //settings for the list and its adapters
    private void fillListContentSettings(){
        //get list view
        listView = findViewById(R.id.productListInfo);

        //create list adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                //context
                PantryActivity.this,
                android.R.layout.simple_list_item_1,
                //data
                listContent
        );

        //add adapter to list
        listView.setAdapter(adapter);
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

    private void fillPantryProductList(){
        Intent intent = getIntent();
        ArrayList<ServerPantryProduct> prods = (ArrayList<ServerPantryProduct>) intent.getSerializableExtra("mylist");
        for(ServerPantryProduct p :prods){
            String productInfo = p.getName()+"; "+p.getDescription();
            Toast.makeText(getApplicationContext(),productInfo ,Toast.LENGTH_SHORT).show();
            listContent.add(productInfo);
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
                Toast.makeText(getApplicationContext(),"Product added with success." ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Server error." ,Toast.LENGTH_SHORT).show();
            }
        });
    }


}