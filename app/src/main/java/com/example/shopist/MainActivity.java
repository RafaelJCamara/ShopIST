package com.example.shopist;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        fillTextView();
    }

//    private void fillTextView(){
//        TextView textView = findViewById(R.id.textView);
//        String username = getIntent().getStringExtra("username");
//        String email = getIntent().getStringExtra("email");
//        String info = username + " -> " + email;
//        textView.setText(info);
//    }

}