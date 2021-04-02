package com.example.shopist.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitInterface;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ListServerData;
import com.example.shopist.Server.ServerResponses.ServerListToken;
import com.example.shopist.Utils.ListManager;
import com.example.shopist.Utils.PantryListManager;
import com.example.shopist.Utils.ShoppingListManager;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ListManager pantryListManager;
    private ListManager shoppingListManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pantryListManager = new PantryListManager(MainActivity.this, findViewById(android.R.id.content),
                                            R.id.pantryList,getLayoutInflater());
        shoppingListManager = new ShoppingListManager(MainActivity.this, findViewById(android.R.id.content),
                                                R.id.shoppingList, getLayoutInflater());
        //LIST OPERATIONS
        listOperations();
        addSettings();
    }

    private void listOperations(){
        pantryListManager.listSettings();
        shoppingListManager.listSettings();
    }

    private void addSettings(){
        fillTextView();
        addLogoutButtonLogic();
        pantryListManager.addListLogic();
        shoppingListManager.addListLogic();
    }


    private void fillTextView(){
        TextView textView = findViewById(R.id.textView);
        String info = "Welcome, ";
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null){
            if(extras.containsKey("username")){
                info+=intent.getStringExtra("username");
            }
        }else{
            info+="anonymous";
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

}