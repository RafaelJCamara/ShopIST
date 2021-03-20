package com.example.shopist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

        //instantiate retrofit object
        retrofit  = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        Button b = findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleServerRequest();
            }
        });
    }

    private void handleServerRequest(){
        HashMap<String,String> map = new HashMap<>();
        map.put("email","email_do_android");
        map.put("pass","password_do_android");
        Call<ServerData> call = retrofitInterface.executeServerCall(map);
        call.enqueue(new Callback<ServerData>() {
            //when the server responds to our request
            @Override
            public void onResponse(Call<ServerData> call, Response<ServerData> response) {
                if(response.code()==200){
                    //server success
                    ServerData user = response.body();
                    String user_info = user.getName() + " - " + user.getEmail();
                    Toast.makeText(MainActivity.this, user_info, Toast.LENGTH_SHORT).show();
                }
            }

            //when the server fails to respond to our request
            @Override
            public void onFailure(Call<ServerData> call, Throwable t) {
                Toast.makeText(MainActivity.this, "SERVER ERROR", Toast.LENGTH_SHORT).show();
            }
        });

    }

}