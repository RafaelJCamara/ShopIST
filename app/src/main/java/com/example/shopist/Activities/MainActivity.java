package com.example.shopist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopist.R;
import com.example.shopist.Utils.ListManager;
import com.example.shopist.Utils.PantryListManager;
import com.example.shopist.Utils.ShoppingListManager;

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