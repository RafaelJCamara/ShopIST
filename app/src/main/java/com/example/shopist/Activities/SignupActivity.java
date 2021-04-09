package com.example.shopist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private RetrofitManager retrofitManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        retrofitManager = new RetrofitManager();
        addSettings();
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
        String password = map.get("password");
        String confirmPassword = map.get("confirmPassword");
        if(password.equals(confirmPassword)){
            //matching password tuple
            Call<Void> call = retrofitManager.accessRetrofitInterface().executeSignup(map);
            call.enqueue(new Callback<Void>() {
                //when the server responds to our request
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 200) {
                        //server success
                        //go to login activity
                        signupSuccess(map);
                    }else if (response.code() == 404){
                        Toast.makeText(SignupActivity.this, "Username or email already exists. Please try to change those!", Toast.LENGTH_LONG).show();
                    }
                }
                //when the server fails to respond to our request
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(SignupActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            //no matching password tuple
            Toast.makeText(SignupActivity.this, "Passwords are not matching!", Toast.LENGTH_SHORT).show();
        }
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
        EditText confirmPasswordComponent = findViewById(R.id.confirmPasswordSignup);
        String confirmPassword = confirmPasswordComponent.getText().toString();
        map.put("confirmPassword",confirmPassword);

        return map;
    }

    private void signupSuccess(HashMap<String,String> map){
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        intent.putExtra("email",map.get("email"));
        intent.putExtra("password",map.get("password"));
        Toast.makeText(SignupActivity.this, "Signup with success!", Toast.LENGTH_LONG).show();
        startActivity(intent);
    }

}
