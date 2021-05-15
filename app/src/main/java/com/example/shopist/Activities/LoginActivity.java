package com.example.shopist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ServerData;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private RetrofitManager retrofitManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        retrofitManager = new RetrofitManager(this);
        addSettings();
    }

    private void addSettings(){
        addButtonSettings();
        checkSignupOrigin();
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

        Call<ServerData> call = retrofitManager.accessRetrofitInterface().executeLogin(map);
        call.enqueue(new Callback<ServerData>() {
            //when the server responds to our request
            @Override
            public void onResponse(Call<ServerData> call, Response<ServerData> response) {
                if (response.code() == 200) {
                    //matching credentials
                    loginSuccess(response);
                } else if (response.code() == 404) {
                    //no matching credentials
                    Toast.makeText(getBaseContext(), "Wrong credentials!!", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401){
                    Toast.makeText(getBaseContext(), "Problem connecting to server (check certificate)", Toast.LENGTH_SHORT).show();
                }
            }
            //when the server fails to respond to our request
            @Override
            public void onFailure(Call<ServerData> call, Throwable t) {
                Toast.makeText(getBaseContext(), "SERVER ERROR! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loginSuccess(Response<ServerData> response){
        //server success (credentials match)
        ServerData userInfoServer = response.body();

        //create a new intent to main activity screen
        Intent intent = new Intent(LoginActivity.this, MainActivityNav.class);

        //put cart information into intent
        intent.putExtra("username",userInfoServer.getName());
        intent.putExtra("email",userInfoServer.getEmail());
        intent.putExtra("userId",userInfoServer.getUserId());

        //start activity targeted in the intent
        startActivity(intent);
    }

    private void checkSignupOrigin(){
        //checks if we go to the login activity from a successful signup
        if(getIntent()!=null && getIntent().getStringExtra("email") != null){
            //we came from signup
            //this means we should fill the login fields with the cart credentials
            EditText emailComponent = findViewById(R.id.emailLogin);
            emailComponent.setText(getIntent().getStringExtra("email"));

            EditText passwordComponent = findViewById(R.id.passwordLogin);
            passwordComponent.setText(getIntent().getStringExtra("password"));
        }
    }

    private void addSignupLinkSettings(){
        TextView signupLink = findViewById(R.id.signupLink);
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                //go to main page activity
                Intent intent = new Intent(LoginActivity.this, MainActivityNav.class);
                startActivity(intent);
            }
        });
    }


}
