package com.example.shopist.Activities.ui.pantries;

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

import com.example.shopist.Activities.PantryActivity;
import com.example.shopist.Activities.ui.ListFragment;
import com.example.shopist.R;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ServerListToken;
import com.example.shopist.Server.ServerResponses.ServerPantryList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantriesFragment extends ListFragment {

    public RetrofitManager retrofitManager;

    public ListView pantryListView;
    //public List<String> pantryListContent;

    private PantriesViewModel pantriesViewModel;
    
    private View root;

    private AlertDialog dialog;
    private static final String LIST_BUNDLE_KEY = "pantryListContent";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_pantries, container, false);

        retrofitManager = new RetrofitManager();

        pantriesViewModel =
                new ViewModelProvider(requireActivity()).get(PantriesViewModel.class);

        super.onCreateView(inflater, container, savedInstanceState);

        pantriesViewModel.getPantryListContent().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> s) {
                pantryListSettings();
                retrievePantryList();
                createPantryList();
            }
        });
        return root;
    }

    /*
    ##########################
    ### pantry list ###
    ##########################
     */

    /*
     * Retrieve pantry list
     * */

    //depende do tipo de lista
    public void retrievePantryList(){
        Button getListButton = root.findViewById(R.id.getPantryListButton);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
        dialog = builder.setView(v).create();
        dialog.show();
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
                    Toast.makeText(root.getContext(), "List has already been added.", Toast.LENGTH_LONG).show();
                }else{
                    //the list hasn't been added
                    getPantryListFromServer(listId);
                    dialog.dismiss();
                }
            }
        });
    }

    public boolean hasListBeenAdded(String listId){
        for(String listInfo: pantriesViewModel.getPantryListContent().getValue()){
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
                Toast.makeText(root.getContext(), "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void renderGetList(ServerPantryList list, String uuid) {
        String listName = list.getName();
        String finalListInfo = listName + " -> " + uuid;
        pantriesViewModel.addToPantryListContent(finalListInfo);
        pantryListSettings();
        Toast.makeText(root.getContext(), "List added with success!", Toast.LENGTH_LONG).show();
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
                openPantryItemActivity(itemInfo);
            }
        });
    }

    public void openPantryItemActivity(String itemInfo){
        Intent intent = new Intent(root.getContext(), PantryActivity.class);
        intent.putExtra("itemInfo", itemInfo);
        intent.putExtra("shoppingLists", /* get shopping lists from server? */new ArrayList<String>());
        startActivity(intent);
    }

    //settings for the list and its adapters
    private void fillPantryListContentSettings(){
        //get list view
        pantryListView = root.findViewById(R.id.pantryList);

        //create list adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                //context
               root.getContext(),
                //layout to be applied on
                android.R.layout.simple_list_item_1,
                //data
                pantriesViewModel.getPantryListContent().getValue()
        );

        //add adapter to list
        pantryListView.setAdapter(adapter);

        TextView empty= root.findViewById(R.id.empty);
        pantryListView.setEmptyView(empty);
    }


    /*
     * Create pantry list
     * */

    //depende do tipo de lista
    public void createPantryList(){
        Button createPantryListButton = root.findViewById(R.id.createPantryListButton);
        createPantryListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCreatePantryListDialog();
            }
        });
    }

    public void handleCreatePantryListDialog(){
        View view = getLayoutInflater().inflate(R.layout.create_list,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
        dialog = builder.setView(view).create();
        dialog.show();
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
                    Toast.makeText(root.getContext(), "List has already been created.", Toast.LENGTH_LONG).show();
                }else{
                    //list has not been created
                    createPantryListInServer(listName, listAddress);
                    dialog.dismiss();
                }
            }
        });
    }

    public boolean hasPantryListBeenCreated(String listName){
        for(String listInfo:pantriesViewModel.getPantryListContent().getValue()){
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
                Toast.makeText(root.getContext(), "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void renderCreatedPantryList(String token, String listName){
        String finalListInfo = listName+" -> "+token;
        pantriesViewModel.addToPantryListContent(finalListInfo);
        pantryListSettings();
        Toast.makeText(root.getContext(), "List created with success!", Toast.LENGTH_LONG).show();
    }

}