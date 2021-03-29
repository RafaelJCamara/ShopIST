package com.example.shopist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

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
        //get list id
        TextView listIDComponent = findViewById(R.id.listID);
        String listID = listIDComponent.getText().toString();

        //retrieve list from server
        retrieveList();

        //render list in frontend
        //renderList();
    }

    private void retrieveList(){
        Button getListButton = findViewById(R.id.getList);
        getListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleListRetrieval();
            }
        });
    }

    private void handleListRetrieval(){
        int i = (int) Math.ceil(Math.random()*10);
        String url = "/list/"+i;
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

    private void renderList(){

    }

}