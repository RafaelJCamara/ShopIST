package com.example.shopist.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.shopist.Activities.ui.cart.CartActivity;
import com.example.shopist.R;
import com.example.shopist.Server.ServerResponses.ListServerData;

import java.util.concurrent.Callable;

public class ShoppingListManager extends ListManager{

    private Intent gotoCart;

    private Callable<Void> viewItemClickCallback;

    public ShoppingListManager(Context context, View view, int listID, LayoutInflater layoutInflater) {
        super(context, view, listID, layoutInflater);
    }

    public static ShoppingListManager createShoppingListManager(View view) {
        return new ShoppingListManager(view.getContext(), view,
                R.id.shoppingList, (LayoutInflater) view.getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE ));
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

    @Override
    protected void addListClickListeners() {
        listView.setOnItemClickListener((parent, view, position, id) -> {

            ListServerData shoppingList = (ListServerData) parent.getItemAtPosition(position);
            gotoCart.putExtra("shoppingListId", shoppingList.getUuid());
            viewItemClickCallback.;

        });
    }
}
