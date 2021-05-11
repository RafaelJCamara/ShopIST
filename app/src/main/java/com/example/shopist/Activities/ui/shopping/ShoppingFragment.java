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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.shopist.Activities.MainActivityNav;
import com.example.shopist.Activities.ShopActivity;
import com.example.shopist.Activities.ui.ListFragment;
import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ServerListToken;
import com.example.shopist.Server.ServerResponses.ServerShoppingList;
import com.example.shopist.Server.ServerResponses.ServerUserList;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingFragment extends ListFragment {

    public static ShoppingViewModel shoppingViewModel;

    public RetrofitManager retrofitManager;

    public ListView shoppingListView;
    
    private View root;

    private AlertDialog dialog;
    private static final String LIST_BUNDLE_KEY = "shoppingListContent";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shoppingViewModel =
                new ViewModelProvider(requireActivity()).get(ShoppingViewModel.class);
        root = inflater.inflate(R.layout.fragment_shopping, container, false);

        retrofitManager = new RetrofitManager();

        super.onCreateView(inflater, container, savedInstanceState);

        shoppingViewModel.getShoppingListContent().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> s) {
                shoppingListSettings();
                retrieveShoppingList();
                createShoppingList();
                fillExistingShoppingLists();
            }
        });

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
                shoppingViewModel.getShoppingListContent().getValue()
        );

        //add adapter to list
        shoppingListView.setAdapter(adapter);

        TextView empty= root.findViewById(R.id.empty);
        shoppingListView.setEmptyView(empty);

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
        dialog = builder.setView(v).create();
        dialog.show();
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
                    if(MainActivityNav.withWifi){
                        //there is Wifi
                        //get list from the server
                        getShoppingListFromServer(listId);
                    }else{
                        Toast.makeText(root.getContext(), "Please connect yourself to Wifi before making this operation.", Toast.LENGTH_LONG).show();
                    }

                    dialog.dismiss();
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
        shoppingViewModel.addToShoppingListContent(finalListInfo);
        shoppingListSettings();
        Toast.makeText(root.getContext(), "List added with success!", Toast.LENGTH_LONG).show();
    }


    public void fillExistingShoppingLists(){
        //clean previous content
        shoppingViewModel.clearContent();
        Call<ServerUserList> call = retrofitManager.accessRetrofitInterface().getUserCurrentShoppingLists(MainActivityNav.currentUserId);
        call.enqueue(new Callback<ServerUserList>() {
            @Override
            public void onResponse(Call<ServerUserList> call, Response<ServerUserList> response) {
                if(response.code()==200){
                    String[] currentLists = response.body().getUserList();
                    renderCurrentLists(currentLists);
                }
            }
            @Override
            public void onFailure(Call<ServerUserList> call, Throwable t) {
                Toast.makeText(root.getContext(), "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderCurrentLists(String[] currentLists){
        for(int i=0;i!=currentLists.length;i++){
            shoppingViewModel.addToShoppingListContent(currentLists[i]);
        }
        shoppingListSettings();
        Toast.makeText(root.getContext(), "Current lists rendered with success!", Toast.LENGTH_LONG).show();
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
        dialog = builder.setView(view).create();
        dialog.show();
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
                    if(MainActivityNav.withWifi){
                        //there is wifi
                        //create list at server
                        createShoppingListInServer(listName, listAddress);
                    }else{
                        //there is no Wifi
                        //do changes in local cache
                        MainActivityNav.smallDataCacheManager.createShoppingList(listName,listAddress);
                        //render offline content in frontend
                    }
                    
                    dialog.dismiss();
                }
            }
        });
    }

    public boolean hasShoppingListBeenCreatedByName(String listName){
        for(String listInfo:shoppingViewModel.getShoppingListContent().getValue()){
            String[] listComponents = listInfo.split(" -> ");
            if(listComponents[0].equals(listName)){
                return true;
            }
        }
        return false;
    }

    public boolean hasShoppingListBeenCreatedById(String listId){
        for(String listInfo:shoppingViewModel.getShoppingListContent().getValue()){
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
        map.put("userId", MainActivityNav.currentUserId);
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
        shoppingViewModel.addToShoppingListContent(finalListInfo);
        shoppingListSettings();
        Toast.makeText(root.getContext(), "List created with success!", Toast.LENGTH_LONG).show();
    }

}