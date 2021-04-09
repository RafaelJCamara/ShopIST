package com.example.shopist.Utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.shopist.Activities.PantryActivity;
import com.example.shopist.R;

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
    
    public void openItemActivity(String itemInfo){
        Intent intent = new Intent(context, PantryActivity.class);
        intent.putExtra("itemInfo", itemInfo);
        context.startActivity(intent);
    }

}
