package com.example.shopist;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL="http://10.0.2.2:3000";
    private ListView productList;
    private ArrayList<String> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        products = new ArrayList<String>();
        //LIST OPERATIONS
        listOperations();

        initRetrofit();
        addSettings();
    }

    private void listOperations(){

        listSettings();

        //add actionlisterner to each item of the list
        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String itemSelected = listToDo.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(),"Product clicked!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listSettings(){
        //get list view
        productList = findViewById(R.id.productList);

        //create list adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                //context
                getApplicationContext(),
                //layout to be applied on
                android.R.layout.simple_list_item_1,
                //id inside layout
                android.R.id.text1,
                //data
                products
        );

        //add adapter to list
        productList.setAdapter(adapter);
    }


    private void initRetrofit(){
        //instantiate retrofit settings
        retrofit  = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }

    private void addSettings(){
        fillTextView();
        addLogoutButtonLogic();
        addSendListLogic();
    }

    private void fillTextView(){
        TextView textView = findViewById(R.id.textView);
        String username = getIntent().getStringExtra("username");
        String email = getIntent().getStringExtra("email");
        String info = "Welcome, ";
        if(username.length()!=0){
            info+=username;
        }else{
            info+="anonymous person";
        }
        textView.setText(info);
    }

    private void addLogoutButtonLogic(){
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Logout with success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addSendListLogic(){
        //retrieve list from server
        retrieveList();
    }

    private void retrieveList(){
        Button getListButton = findViewById(R.id.getList);
        getListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get list id
                EditText listIDComponent = findViewById(R.id.listaId);
                String listId = listIDComponent.getText().toString();
                handleListRetrieval(listId);
            }
        });
    }

    private void handleListRetrieval(String listId){
        String url = "/list/"+listId;
        Call<ListServerData> call = retrofitInterface.getList(url);
        call.enqueue(new Callback<ListServerData>() {
            @Override
            public void onResponse(Call<ListServerData> call, Response<ListServerData> response) {
                if(response.code()==200){
                    //list retrieved by the server
                    ListServerData list = response.body();

                    //render list in front-end
                    renderList(list);
                }
            }

            @Override
            public void onFailure(Call<ListServerData> call, Throwable t) {
                Toast.makeText(MainActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderList(ListServerData list){
        addProductsToList(list);
        changeListInfo(list.getListName(), list.getUuid());
        listOperations();
    }

    private void addProductsToList(ListServerData list){
        ServerProduct[] listProducts = list.getProducts();
        for(ServerProduct p:listProducts){
            String productName = p.getName();
            String price = String.valueOf(p.getPrice());
            String description = p.getDescription();
            products.add(productName+", "+price+" - "+description);
        }
    }

    private void changeListInfo(String listName, String listId){
        TextView listInfo = findViewById(R.id.listInfo);
        listInfo.setText("List name: "+listName+". List code: "+listId);
    }

}