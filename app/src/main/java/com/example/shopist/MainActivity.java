package com.example.shopist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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
    private ListView pantryLists;
    private ArrayList<String> pantryList;

    private ListView shoppingLists;
    private ArrayList<String> shoppingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pantryList = new ArrayList<String>();
        shoppingList = new ArrayList<String>();
        //LIST OPERATIONS
        listOperations();

        initRetrofit();
        addSettings();
    }

    private void listOperations(){
        pantryListSettings();
//        shoppingListSettings();
    }

    private void pantryListSettings(){
        pantrySettings();

        //add actionlisterner to each item of the list
        pantryLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String itemSelected = listToDo.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(),"Pantry list clicked!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pantrySettings(){
        //get list view
        pantryLists = findViewById(R.id.productList);

        //create list adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                //context
                getApplicationContext(),
                //layout to be applied on
                android.R.layout.simple_list_item_1,
                //id inside layout
                android.R.id.text1,
                //data
                pantryList
        );

        //add adapter to list
        pantryLists.setAdapter(adapter);
    }

    private void shoppingListSettings(){
        shoppingSettings();
        //add actionlisterner to each item of the list
        shoppingLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String itemSelected = listToDo.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(),"Product clicked!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shoppingSettings(){
        //get list view
        shoppingLists = findViewById(R.id.productList);

        //create list adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                //context
                getApplicationContext(),
                //layout to be applied on
                android.R.layout.simple_list_item_1,
                //id inside layout
                android.R.id.text1,
                //data
                shoppingList
        );

        //add adapter to list
        shoppingLists.setAdapter(adapter);
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
        addPantryListLogic();
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

    private void addPantryListLogic(){
        //retrieve list from server
        retrievePantryList();
        //create list
        createPantryList();
    }

    private void retrievePantryList(){
        Button getListButton = findViewById(R.id.getPantryListButton);
        getListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open get list dialog
                handleGetPantryListDialog();
            }
        });
    }

    private void handleGetPantryListDialog(){
        View view = getLayoutInflater().inflate(R.layout.get_pantry_list,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view).show();
        handleGetPantryListLogic(view);
    }

    private void handleGetPantryListLogic(View view){
        Button getPantryListButton = view.findViewById(R.id.addPantryListButton);
        getPantryListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pantryListId = view.findViewById(R.id.getPantryListId);
                String listId = pantryListId.getText().toString();
                getPantryListFromServer(listId);
            }
        });
    }

    private void getPantryListFromServer(String listId){
        String url = "/list/"+listId;
        Call<ListServerData> call = retrofitInterface.getList(url);
        call.enqueue(new Callback<ListServerData>() {
            @Override
            public void onResponse(Call<ListServerData> call, Response<ListServerData> response) {
                if(response.code()==200){
                    //list retrieved by the server
                    ListServerData list = response.body();
                    //render list in front-end
                    renderGetPantryList(list);
                }
            }

            @Override
            public void onFailure(Call<ListServerData> call, Throwable t) {
                Toast.makeText(MainActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderGetPantryList(ListServerData list){
        String listName = list.getListName();
        String listCode = list.getUuid();
        String finalListInfo = listName+" -> "+listCode;
        pantryList.add(finalListInfo);
        listOperations();
        Toast.makeText(MainActivity.this, "List added with success!", Toast.LENGTH_LONG).show();
    }


    private void createPantryList(){
        Button createPantryListButton = findViewById(R.id.createPantryListButton);
        createPantryListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCreatePantryListDialog();
            }
        });
    }

    private void handleCreatePantryListDialog(){
        View view = getLayoutInflater().inflate(R.layout.create_pantry_list,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view).show();
        handleCreatePantryListLogic(view);
    }

    private void handleCreatePantryListLogic(View view){
        Button getPantryListButton = view.findViewById(R.id.finalCreatePantryListButton);
        getPantryListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pantryListId = view.findViewById(R.id.newPantryListName);
                String listName = pantryListId.getText().toString();
                createPantryListInServer(listName);
            }
        });
    }

    private void createPantryListInServer(String listName){
        HashMap<String,String> map = new HashMap<>();
        map.put("name",listName);
        Call<ServerListToken> call = retrofitInterface.executeListCreation(map);
        call.enqueue(new Callback<ServerListToken>() {
            @Override
            public void onResponse(Call<ServerListToken> call, Response<ServerListToken> response) {
                if(response.code()==200){
                    //list retrieved by the server
                    ServerListToken token = response.body();
                    String tokenContent = token.getListId();
                    //render list in front-end
                    renderCreatePantryList(tokenContent,listName);
                }
            }

            @Override
            public void onFailure(Call<ServerListToken> call, Throwable t) {
                Toast.makeText(MainActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderCreatePantryList(String token, String listName){
        String finalListInfo = listName+" -> "+token;
        pantryList.add(finalListInfo);
        listOperations();
        Toast.makeText(MainActivity.this, "List created with success!", Toast.LENGTH_LONG).show();
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


}