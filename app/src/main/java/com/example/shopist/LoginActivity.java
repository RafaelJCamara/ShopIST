package com.example.shopist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL="http://10.0.2.2:3000";
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
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
        addButtonSettings();
        addSignupLinkSettings();
        mainPageLinkSettings();
    }

    private void addButtonSettings(){
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleServerRequest();
            }
        });
    }

    private void handleServerRequest() {

        HashMap<String, String> map = new HashMap<>();

        //get email
        EditText emailComponent = findViewById(R.id.emailLogin);
        String email = emailComponent.getText().toString();

        //get password
        EditText passwordComponent = findViewById(R.id.passwordLogin);
        String password = passwordComponent.getText().toString();

        map.put("email", email);
        map.put("password", password);

        Call<ServerData> call = retrofitInterface.executeLogin(map);
        call.enqueue(new Callback<ServerData>() {
            //when the server responds to our request
            @Override
            public void onResponse(Call<ServerData> call, Response<ServerData> response) {
                if (response.code() == 200) {
                    //matching credentials
                    Toast.makeText(LoginActivity.this, "Correct credentials!!", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 404) {
                    //no matching credentials
                    Toast.makeText(LoginActivity.this, "Wrong credentials!!", Toast.LENGTH_SHORT).show();
                }
            }
            //when the server fails to respond to our request
            @Override
            public void onFailure(Call<ServerData> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addSignupLinkSettings(){
        TextView signupLink = findViewById(R.id.signupLink);
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(LoginActivity.this, "Signup link", Toast.LENGTH_SHORT).show();
                //go to signup activity

                //create a new intent
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                //start activity targeted in the intent
                startActivity(intent);
            }
        });
    }

    private void mainPageLinkSettings(){
        TextView mainpageLink = findViewById(R.id.gotoMainPageLink);
        mainpageLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(LoginActivity.this, "Main page link", Toast.LENGTH_SHORT).show();
                //go to main page activity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }



    private void loginSuccess(Response<ServerData> response){
//        //server success (credentials match)
//        ServerData userInfoServer = response.body();
//        Toast.makeText(LoginActivity.this, "Correct credentials!!", Toast.LENGTH_SHORT).show();

        //create a new intent to main activity screen
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//
//        //put user information into intent
//        intent.putExtra("username",userInfoServer.getName());
//        intent.putExtra("email",userInfoServer.getEmail());

        //start activity targeted in the intent
        context.startActivity(intent);
    }

}
