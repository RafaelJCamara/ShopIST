package com.example.shopist.Utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shopist.Activities.PantryActivity;
import com.example.shopist.R;
import com.example.shopist.Server.ServerResponses.ServerListToken;
import com.example.shopist.Server.ServerResponses.ServerPantryList;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantryListManager extends ListManager{

    public PantryListManager(Context context, View view, int listID, LayoutInflater layoutInflater) {
        super(context, view, listID, layoutInflater);
    }

    //depende do tipo de lista
    public void retrieveList(){
        Button getListButton = view.findViewById(R.id.getPantryListButton);
        getListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open get list dialog
                handleGetListDialog();
            }
        });
    }

    public void getListFromServer(String listId){
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
                Toast.makeText(context, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void renderGetList(ServerPantryList list, String uuid) {
        String listName = list.getName();
        String finalListInfo = listName + " -> " + uuid;
        listContent.add(finalListInfo);
        listSettings();
        Toast.makeText(context, "List added with success!", Toast.LENGTH_LONG).show();
    }





    //depende do tipo de lista
    public void createList(){
        Button createPantryListButton = view.findViewById(R.id.createPantryListButton);
        createPantryListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCreateListDialog();
            }
        });
    }


    public void createListInServer(String listName, String listAddress){
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
                    renderCreateList(tokenContent,listName);
                }
            }

            @Override
            public void onFailure(Call<ServerListToken> call, Throwable t) {
                Toast.makeText(context, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    
    public void openItemActivity(String itemInfo){
        Intent intent = new Intent(context, PantryActivity.class);
        intent.putExtra("itemInfo", itemInfo);
        context.startActivity(intent);
    }

}
