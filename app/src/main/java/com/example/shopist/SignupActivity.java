package com.example.shopist;

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

public class SignupActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        initRetrofit();
        addSettings();
    }

    private void initRetrofit() {
        //instantiate retrofit object
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }

    private void addSettings() {
        addButtonSettings();
    }

    private void addButtonSettings() {
        Button signupButton = findViewById(R.id.signupButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleServerRequest();
            }
        });
    }

    private void handleServerRequest() {
        HashMap<String, String> map = getFormInfo();
        Call<Void> call = retrofitInterface.executeSignup(map);
        call.enqueue(new Callback<Void>() {
            //when the server responds to our request
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    //server success
                    //go to login activity with intent success message
                    String success_message = "Successfully signed up!";
                    Toast.makeText(SignupActivity.this, success_message, Toast.LENGTH_LONG).show();
                }else if (response.code() == 404){
                    String success_message = "Incorrect credentials";
                    Toast.makeText(SignupActivity.this, success_message, Toast.LENGTH_LONG).show();
                }
            }

            //when the server fails to respond to our request
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private HashMap<String, String> getFormInfo() {
        HashMap<String, String> map = new HashMap<>();

        //get username
        EditText usernameComponent = findViewById(R.id.usernameSignup);
        String username = usernameComponent.getText().toString();
        map.put("username",username);

        //get email
        EditText emailComponent = findViewById(R.id.emailSignup);
        String email = emailComponent.getText().toString();
        map.put("email",email);

        //get first password
        EditText passwordComponent = findViewById(R.id.passwordSignup);
        String password = passwordComponent.getText().toString();
        map.put("password",password);

        //get confirm password
//        EditText confirmPasswordComponent = findViewById(R.id.confirmPasswordSignup);
//        String confirmPassword = confirmPasswordComponent.getText().toString();
//        map.put("confirmPassword",confirmPassword);

        return map;
    }

}
