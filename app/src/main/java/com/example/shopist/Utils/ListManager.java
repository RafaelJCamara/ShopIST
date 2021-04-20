package com.example.shopist.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;

import java.util.ArrayList;

public abstract class ListManager {
    public ListView listView;
    public ArrayList<String> listContent;
    public View view;
    public Context context;
    public int listResourceID;
    public LayoutInflater layoutInflater;
    public RetrofitManager retrofitManager;


    public ListManager(Context context, View view, int listID, LayoutInflater layoutInflater){
        this.context = context;
        this.view = view;
        this.listResourceID = listID;
        this.layoutInflater = layoutInflater;
        listContent = new ArrayList<>();
        retrofitManager = new RetrofitManager();
        listSettings();
    }

    public void listSettings(){
        fillListContentSettings();
        addListClickListeners();
    }

    //settings for the list and its adapters
    private void fillListContentSettings(){
        //get list view
        listView = view.findViewById(listResourceID);

        //create list adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                //context
                context,
                //layout to be applied on
                android.R.layout.simple_list_item_1,
                //id inside layout
                android.R.id.text1,
                //data
                listContent
        );

        //add adapter to list
        listView.setAdapter(adapter);
    }

    //action when a item of the list is clicked
    private void addListClickListeners(){
        //add actionlisterner to each item of the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemInfo = (String) parent.getAdapter().getItem(position);
                Toast.makeText(context,"List item clicked!",Toast.LENGTH_SHORT).show();
                openItemActivity(itemInfo);
            }
        });
    }

    public void addListLogic(){
        //retrieve list from server
        retrieveList();
        //create list
        createList();
    }

    //depende do tipo de lista
    public abstract void retrieveList();


    public void handleGetListDialog(){
        View v = layoutInflater.inflate(R.layout.get_list,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(v).show();
        handleGetListLogic(v);
    }

    //depende do tipo de lista
    public void handleGetListLogic(View view){
        Button getListButton = view.findViewById(R.id.addListButton);
        getListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText listID = view.findViewById(R.id.getListId);
                String listId = listID.getText().toString();
                //check if the list has been added
                if(hasListBeenAdded(listId)){
                    //list has been added
                    Toast.makeText(context, "List has already been added.", Toast.LENGTH_LONG).show();
                }else{
                    //the list hasn't been added
                    getListFromServer(listId);
                }
            }
        });
    }

    public boolean hasListBeenAdded(String listId){
        for(String listInfo:listContent){
            String[] listComponents = listInfo.split(" -> ");
            if(listComponents[1].equals(listId)){
                return true;
            }
        }
        return false;
    }

    public abstract void getListFromServer(String listId);




    //depende do tipo de lista
    public abstract void createList();

    public void handleCreateListDialog(){
        View view = layoutInflater.inflate(R.layout.create_list,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view).show();
        handleCreateListLogic(view);
    }

    //depende do tipo de lista
    public void handleCreateListLogic(View view){
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
                if(hasListBeenCreated(listName)){
                    //list has been created
                    Toast.makeText(context, "List has already been created.", Toast.LENGTH_LONG).show();
                }else{
                    //list has not been created
                    createListInServer(listName, listAddress);
                }
            }
        });
    }

    public boolean hasListBeenCreated(String listName){
        for(String listInfo:listContent){
            String[] listComponents = listInfo.split(" -> ");
            if(listComponents[0].equals(listName)){
                return true;
            }
        }
        return false;
    }

    public abstract void createListInServer(String listName, String listAddress);


//    public void createListInServer(String listName){
//        HashMap<String,String> map = new HashMap<>();
//        map.put("name",listName);
//        Call<ServerListToken> call = retrofitManager.accessRetrofitInterface().executeListCreation(map);
//        call.enqueue(new Callback<ServerListToken>() {
//            @Override
//            public void onResponse(Call<ServerListToken> call, Response<ServerListToken> response) {
//                if(response.code()==200){
//                    //list retrieved by the server
//                    ServerListToken token = response.body();
//                    String tokenContent = token.getListId();
//                    //render list in front-end
//                    renderCreateList(tokenContent,listName);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ServerListToken> call, Throwable t) {
//                Toast.makeText(context, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    public abstract void openItemActivity(String itemInfo);

    public void renderCreateList(String token, String listName){
        String finalListInfo = listName+" -> "+token;
        listContent.add(finalListInfo);
        listSettings();
        Toast.makeText(context, "List created with success!", Toast.LENGTH_LONG).show();
    }


}
