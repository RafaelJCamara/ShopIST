package com.example.shopist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.shopist.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivityNav extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_pantries, R.id.navigation_shopping)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        fillTextView();
        addLogoutButtonLogic();

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
                Intent intent = new Intent(MainActivityNav.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivityNav.this, "Logout with success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}