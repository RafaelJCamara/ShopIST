package com.example.shopist.Activities;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ServerListToken;
import com.example.shopist.Server.ServerResponses.ServerPantryList;
import com.example.shopist.Utils.ListManager;
import com.example.shopist.Utils.MainLogicManager;
import com.example.shopist.Utils.PantryListManager;
import com.example.shopist.Utils.ShoppingListManager;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public RetrofitManager retrofitManager;

    public ListView pantryListView;
    public ArrayList<String> pantryListContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofitManager = new RetrofitManager();
        pantryListContent = new ArrayList<>();


        //LIST OPERATIONS
        pantryListSettings();
        addSettings();
        listProperties();
    }

    private void addSettings(){
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
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Logout with success!", Toast.LENGTH_SHORT).show();

            }
        });
    }


/*
    ##########################
    ### to be moved later ###
    ### button logic ###
    ##########################
     */


    public void listProperties(){
        //pantry lists
        retrievePantryList();
        createPantryList();

        //shopping list
    }

    /*
    * Retrieve pantry list
    * */

    //depende do tipo de lista
    public void retrievePantryList(){
        Button getListButton = findViewById(R.id.getPantryListButton);
        getListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open get list dialog
                handleGetPantryListDialog();
            }
        });
    }

    public void handleGetPantryListDialog(){
        View v = getLayoutInflater().inflate(R.layout.get_list,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(v).show();
        handleGetPantryListLogic(v);
    }

    //depende do tipo de lista
    public void handleGetPantryListLogic(View view){
        Button getListButton = view.findViewById(R.id.addListButton);
        getListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText listID = view.findViewById(R.id.getListId);
                String listId = listID.getText().toString();
                //check if the list has been added
                if(hasListBeenAdded(listId)){
                    //list has been added
                    Toast.makeText(MainActivity.this, "List has already been added.", Toast.LENGTH_LONG).show();
                }else{
                    //the list hasn't been added
                    getPantryListFromServer(listId);
                }
            }
        });
    }

    public boolean hasListBeenAdded(String listId){
        for(String listInfo:pantryListContent){
            String[] listComponents = listInfo.split(" -> ");
            if(listComponents[1].equals(listId)){
                return true;
            }
        }
        return false;
    }

    public void getPantryListFromServer(String listId){
        Call<ServerPantryList> call = retrofitManager.accessRetrofitInterface().syncPantryList(listId);
        call.enqueue(new Callback<ServerPantryList>() {
            @Override
            public void onResponse(Call<ServerPantryList> call, Response<ServerPantryList> response) {
                if(response.code()==200){
                    //list retrieved by the server
                    ServerPantryList list = response.body();
                    //render list in front-end
                    renderGetList(list, listId);
                }
            }
            @Override
            public void onFailure(Call<ServerPantryList> call, Throwable t) {
                Toast.makeText(MainActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void renderGetList(ServerPantryList list, String uuid) {
        String listName = list.getName();
        String finalListInfo = listName + " -> " + uuid;
        pantryListContent.add(finalListInfo);
        pantryListSettings();
        Toast.makeText(MainActivity.this, "List added with success!", Toast.LENGTH_LONG).show();
    }

    public void pantryListSettings(){
        fillPantryListContentSettings();
        addPantryListClickListeners();
    }

    private void addPantryListClickListeners(){
        //add actionlisterner to each item of the list
        pantryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemInfo = (String) parent.getAdapter().getItem(position);
                Toast.makeText(MainActivity.this,"List item clicked!",Toast.LENGTH_SHORT).show();
                openPantryItemActivity(itemInfo);
            }
        });
    }

    public void openPantryItemActivity(String itemInfo){
        Intent intent = new Intent(MainActivity.this, PantryActivity.class);
        intent.putExtra("itemInfo", itemInfo);
        //pass pantry products to be rendered
        startActivity(intent);
    }

    //settings for the list and its adapters
    private void fillPantryListContentSettings(){
        //get list view
        pantryListView = findViewById(R.id.pantryList);

        //create list adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                //context
                MainActivity.this,
                //layout to be applied on
                android.R.layout.simple_list_item_1,
                //data
                pantryListContent
        );

        //add adapter to list
        pantryListView.setAdapter(adapter);
    }


    /*
     * Create pantry list
     * */

    //depende do tipo de lista
    public void createPantryList(){
        Button createPantryListButton = findViewById(R.id.createPantryListButton);
        createPantryListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCreatePantryListDialog();
            }
        });
    }

    public void handleCreatePantryListDialog(){
        View view = getLayoutInflater().inflate(R.layout.create_list,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view).show();
        handleCreatePantryListLogic(view);
    }

    public void handleCreatePantryListLogic(View view){
        Button getListButton = view.findViewById(R.id.createListButton);
        getListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //access list name
                EditText listId = view.findViewById(R.id.newListName);
                String listName = listId.getText().toString();

                //access list address
                EditText listAddressComponent = view.findViewById(R.id.listAddress);
                String listAddress = listAddressComponent.getText().toString();

                //check if list had already been created
                if(hasPantryListBeenCreated(listName)){
                    //list has been created
                    Toast.makeText(MainActivity.this, "List has already been created.", Toast.LENGTH_LONG).show();
                }else{
                    //list has not been created
                    createPantryListInServer(listName, listAddress);
                }
            }
        });
    }

    public boolean hasPantryListBeenCreated(String listName){
        for(String listInfo:pantryListContent){
            String[] listComponents = listInfo.split(" -> ");
            if(listComponents[0].equals(listName)){
                return true;
            }
        }
        return false;
    }

    public void createPantryListInServer(String listName, String listAddress){
        HashMap<String,String> map = new HashMap<>();
        map.put("name",listName);
        map.put("address", listAddress);
        Call<ServerListToken> call = retrofitManager.accessRetrofitInterface().executePantryListCreation(map);
        call.enqueue(new Callback<ServerListToken>() {
            @Override
            public void onResponse(Call<ServerListToken> call, Response<ServerListToken> response) {
                if(response.code()==200){
                    //list retrieved by the server
                    ServerListToken token = response.body();
                    String tokenContent = token.getListId();
                    //render list in front-end
                    renderCreatedPantryList(tokenContent,listName);
                }
            }

            @Override
            public void onFailure(Call<ServerListToken> call, Throwable t) {
                Toast.makeText(MainActivity.this, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void renderCreatedPantryList(String token, String listName){
        String finalListInfo = listName+" -> "+token;
        pantryListContent.add(finalListInfo);
        pantryListSettings();
        Toast.makeText(MainActivity.this, "List created with success!", Toast.LENGTH_LONG).show();
    }


}