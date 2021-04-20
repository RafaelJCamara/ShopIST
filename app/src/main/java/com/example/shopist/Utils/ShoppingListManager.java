package com.example.shopist.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shopist.R;
import com.example.shopist.Server.ServerResponses.ServerListToken;
import com.example.shopist.Server.ServerResponses.ServerPantryList;
import com.example.shopist.Server.ServerResponses.ServerPantryProduct;
import com.example.shopist.Server.ServerResponses.ServerShoppingList;
import com.example.shopist.Server.ServerResponses.ServerShoppingProduct;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingListManager extends ListManager{
    
    public ServerShoppingProduct[] shoppingProducts;

    public ShoppingListManager(Context context, View view, int listID, LayoutInflater layoutInflater) {
        super(context, view, listID, layoutInflater);
    }

    //depende do tipo de lista
    public void retrieveList(){
        Button getListButton = view.findViewById(R.id.getShoppingListButton);
        getListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open get list dialog
                handleGetListDialog();
            }
        });
    }


    public void getListFromServer(String listId){
        Call<ServerShoppingList> call = retrofitManager.accessRetrofitInterface().syncShoppingList(listId);
        call.enqueue(new Callback<ServerShoppingList>() {
            @Override
            public void onResponse(Call<ServerShoppingList> call, Response<ServerShoppingList> response) {
                if(response.code()==200){
                    //list retrieved by the server
                    ServerShoppingList list = response.body();
                    //render list in front-end
                    renderGetList(list, listId);
                }
            }

            @Override
            public void onFailure(Call<ServerShoppingList> call, Throwable t) {
                Toast.makeText(context, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void renderGetList(ServerShoppingList list, String uuid) {
        this.shoppingProducts = list.getProducts();

        String listName = list.getName();
        String finalListInfo = listName + " -> " + uuid;
        listContent.add(finalListInfo);
        listSettings();
        Toast.makeText(context, "List added with success!", Toast.LENGTH_LONG).show();
    }


    //depende do tipo de lista
    public void createList(){
        Button createPantryListButton = view.findViewById(R.id.createShoppingListButton);
        createPantryListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCreateListDialog();
            }
        });
    }


    public void createListInServer(String listName, String listAddress){
        HashMap<String,String> map = new HashMap<>();
        map.put("listName",listName);
        map.put("address", listAddress);
        Call<ServerListToken> call = retrofitManager.accessRetrofitInterface().executeShoppingListCreation(map);
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
        //NOTHING YET
    }

}
