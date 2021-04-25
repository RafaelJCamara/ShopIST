package com.example.shopist.Activities.ui.shopping;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.example.shopist.Activities.MainActivity;
import com.example.shopist.Activities.ShopActivity;
import com.example.shopist.Activities.ui.ListFragment;
import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ServerListToken;
import com.example.shopist.Server.ServerResponses.ServerShoppingList;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingFragment extends ListFragment {

    private ShoppingViewModel shoppingViewModel;

    public RetrofitManager retrofitManager;

    public ListView shoppingListView;
    public ArrayList<String> shoppingListContent;
    
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shoppingViewModel =
                new ViewModelProvider(this).get(ShoppingViewModel.class);
        root = inflater.inflate(R.layout.fragment_shopping, container, false);

        /*listManager = ShoppingListManager.createShoppingListManager(root);
        super.onCreateView(inflater, container, savedInstanceState);
        */

        retrofitManager = new RetrofitManager();
        shoppingListContent = new ArrayList<>();

        retrieveShoppingList();
        createShoppingList();

        return root;
    }

    public void shoppingListSettings(){
        fillShoppingListContentSettings();
        addShoppingListClickListeners();
    }

    private void fillShoppingListContentSettings(){
        //get list view
        shoppingListView = root.findViewById(R.id.shoppingList);

        //create list adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                //context
                root.getContext(),
                //layout to be applied on
                android.R.layout.simple_list_item_1,
                //data
                shoppingListContent
        );

        //add adapter to list
        shoppingListView.setAdapter(adapter);
    }

    private void addShoppingListClickListeners(){
        //add actionlisterner to each item of the list
        shoppingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemInfo = (String) parent.getAdapter().getItem(position);
                openShoppingItemActivity(itemInfo);
            }
        });
    }

    public void openShoppingItemActivity(String itemInfo){
        Intent intent = new Intent(root.getContext(), ShopActivity.class);
        intent.putExtra("itemInfo", itemInfo);
        startActivity(intent);
    }


    public void retrieveShoppingList(){
        Button getListButton = root.findViewById(R.id.getShoppingListButton);
        getListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open get list dialog
                handleGetShoppingListDialog();
            }
        });
    }

    public void handleGetShoppingListDialog(){
        View v = getLayoutInflater().inflate(R.layout.get_list,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
        builder.setView(v).show();
        handleGetShoppingListLogic(v);
    }

    public void handleGetShoppingListLogic(View view){
        Button getListButton = view.findViewById(R.id.addListButton);
        getListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText listID = view.findViewById(R.id.getListId);
                String listId = listID.getText().toString();
                //check if the list has been added
                if(hasShoppingListBeenCreatedById(listId)){
                    //list has been added
                    Toast.makeText(root.getContext(), "List has already been added.", Toast.LENGTH_LONG).show();
                }else{
                    //the list hasn't been added
                    getShoppingListFromServer(listId);
                }
            }
        });
    }

    public void getShoppingListFromServer(String listId){
        Call<ServerShoppingList> call = retrofitManager.accessRetrofitInterface().syncShoppingList(listId);
        call.enqueue(new Callback<ServerShoppingList>() {
            @Override
            public void onResponse(Call<ServerShoppingList> call, Response<ServerShoppingList> response) {
                if(response.code()==200){
                    //list retrieved by the server
                    ServerShoppingList list = response.body();
                    //render list in front-end
                    renderShoppingGetList(list, listId);
                }
            }
            @Override
            public void onFailure(Call<ServerShoppingList> call, Throwable t) {
                Toast.makeText(root.getContext(), "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void renderShoppingGetList(ServerShoppingList list, String uuid) {
        String listName = list.getName();
        String finalListInfo = listName + " -> " + uuid;
        shoppingListContent.add(finalListInfo);
        shoppingListSettings();
        Toast.makeText(root.getContext(), "List added with success!", Toast.LENGTH_LONG).show();
    }


    /*
     * Create shopping list
     * */

    public void createShoppingList(){
        Button createPantryListButton = root.findViewById(R.id.createShoppingListButton);
        createPantryListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCreateShoppingListDialog();
            }
        });
    }

    public void handleCreateShoppingListDialog(){
        View view = getLayoutInflater().inflate(R.layout.create_list,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
        builder.setView(view).show();
        handleCreateShoppingListLogic(view);
    }

    public void handleCreateShoppingListLogic(View view){
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
                if(hasShoppingListBeenCreatedByName(listName)){
                    //list has been created
                    Toast.makeText(root.getContext(), "List has already been created.", Toast.LENGTH_LONG).show();
                }else{
                    //list has not been created
                    createShoppingListInServer(listName, listAddress);
                }
            }
        });
    }

    public boolean hasShoppingListBeenCreatedByName(String listName){
        for(String listInfo:shoppingListContent){
            String[] listComponents = listInfo.split(" -> ");
            if(listComponents[0].equals(listName)){
                return true;
            }
        }
        return false;
    }

    public boolean hasShoppingListBeenCreatedById(String listId){
        for(String listInfo:shoppingListContent){
            String[] listComponents = listInfo.split(" -> ");
            if(listComponents[1].equals(listId)){
                return true;
            }
        }
        return false;
    }

    public void createShoppingListInServer(String listName, String listAddress){
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
                    renderCreateShoppingList(tokenContent,listName);
                }
            }

            @Override
            public void onFailure(Call<ServerListToken> call, Throwable t) {
                Toast.makeText(root.getContext(), "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void renderCreateShoppingList(String token, String listName){
        String finalListInfo = listName+" -> "+token;
        shoppingListContent.add(finalListInfo);
        shoppingListSettings();
        Toast.makeText(root.getContext(), "List created with success!", Toast.LENGTH_LONG).show();
    }

}