package com.example.shopist.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitInterface;
import com.example.shopist.Utils.ItemListAdapter;
import com.example.shopist.product.ProductClass;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Retrofit;

public class PantryActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;

    private ArrayList<ProductClass> productList;
    private ListView itemListView;
    private ItemListAdapter itemListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        listCodeView.setText(values[1]);
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


        /*
        //add actionlisterner to each item of the list
        itemListView.setOnItemClickListener(new itemListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = listToDo.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(),"Item list clicked!",Toast.LENGTH_SHORT).show();
            }
        });
        */
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

    private void handleCreateProductDialog(){
        View view = getLayoutInflater().inflate(R.layout.create_product,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view).show();
        createProductLogic(view);
    }


    private void createProductLogic(View view) {
        Button createProductButton = view.findViewById(R.id.CreateProductButton);

        TextView productNameView = view.findViewById(R.id.edit_product_name);
        TextView productPriceView = view.findViewById(R.id.edit_product_price);
        TextView productDescriptionView = view.findViewById(R.id.edit_product_description);

        createProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = productNameView.getText().toString();
                String productPrice = productPriceView.getText().toString();
                String productDescription = productDescriptionView.getText().toString();

                createProductInServer(productName, productPrice, productDescription);
            }
        });
    }

    private void createProductInServer(String productName, String productPrice, String productDescription){
        HashMap<String,String> map = new HashMap<>();
        map.put("name",productName);
        map.put("price",productPrice);
        map.put("description", productDescription);

        /*
        Call<Void> call = retrofitInterface.createProduct(map);
        call.enqueue(new Callback<Void>() {
            //when the server responds to our request
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    //server success
                    //go to login activity
                    //signupSuccess(map);
                }else if (response.code() == 404){
                    String error_message = "Username or email already exists. Please try to change those!";
                    //Toast.makeText(SignupActivity.this, error_message, Toast.LENGTH_LONG).show();
                }
            }
            //when the server fails to respond to our request
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //Toast.makeText(SignupActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
         */


    }


    /*
    item_Class_test = new ProductClass("laranja");
                item_Class_test.setImage(R.drawable.orange);
                productList.add(item_Class_test);

                itemListAdapter.notifyDataSetChanged();

     */
}