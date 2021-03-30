package com.example.shopist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRetrofit();
        addSettings();
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
        fillDummyContent();
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
                    Toast.makeText(MainActivity.this, "List retrieved with success!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ListServerData> call, Throwable t) {
                Toast.makeText(MainActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fillDummyContent(){
        //create list
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("name","exampleList");
        Call<ServerListToken> call = retrofitInterface.executeListCreation(map);
        call.enqueue(new Callback<ServerListToken>() {
            @Override
            public void onResponse(Call<ServerListToken> call, Response<ServerListToken> response) {
                if(response.code()==200){
                    ServerListToken listToken = response.body();
                    String token = listToken.getListId();
                    addProducts(token);
                }
            }
            @Override
            public void onFailure(Call<ServerListToken> call, Throwable t) {
                Toast.makeText(MainActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addProducts(String listID){
        //create product 1
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("name","banana");
        map.put("price", "5.99" );
        map.put("description","A simple banana.");
        map.put("listToken",listID);
        Call<Void> call = retrofitInterface.createProduct(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==200){
                    Toast.makeText(MainActivity.this, "Product 1 created successfully.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });

        //create product 2
        HashMap<String,String> map2 = new HashMap<String,String>();
        map2.put("name","potato");
        map2.put("price", "2.99" );
        map2.put("description","A simple potato.");
        map2.put("listToken",listID);
        Call<Void> call2 = retrofitInterface.createProduct(map2);
        call2.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==200){
                    Toast.makeText(MainActivity.this, "Product 2 created successfully.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

}