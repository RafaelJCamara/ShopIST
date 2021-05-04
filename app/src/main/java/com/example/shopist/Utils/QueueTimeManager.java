package com.example.shopist.Utils;

import android.util.Log;
import android.widget.Toast;

import com.example.shopist.Activities.MainActivityNav;
import com.example.shopist.Server.ServerInteraction.RetrofitManager;
import com.example.shopist.Server.ServerResponses.ServerInitCheckoutToken;
import com.example.shopist.Server.ServerResponses.ServerPantryList;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QueueTimeManager {

    private MainActivityNav myActivity;
    private RetrofitManager retrofitManager;
    private String checkoutId;

    public QueueTimeManager(MainActivityNav myActivity){
        this.myActivity = myActivity;
        retrofitManager = new RetrofitManager();
    }

    public void initCheckoutProcess(){

        //to do
        int numberOfMyCartItems = 1;

        HashMap<String,String> map = new HashMap<>();
        map.put("numberItemsCart", String.valueOf(numberOfMyCartItems));

        Call<ServerInitCheckoutToken> call = retrofitManager.accessRetrofitInterface().initCheckoutProcess("1",map);


        call.enqueue(new Callback<ServerInitCheckoutToken>() {
            @Override
            public void onResponse(Call<ServerInitCheckoutToken> call, Response<ServerInitCheckoutToken> response) {
                if(response.code()==200){
                    String checkoutToken = response.body().getCheckoutToken();
                    setCheckoutId(checkoutToken);
                    Toast.makeText(myActivity.getApplicationContext(), "Checkout started with success!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ServerInitCheckoutToken> call, Throwable t) {
                Toast.makeText(myActivity.getApplicationContext(), "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void endCheckoutProcess(){

        HashMap<String,String> map = new HashMap<>();
        map.put("checkoutId", this.checkoutId);

        Call<Void> call = retrofitManager.accessRetrofitInterface().endCheckoutProcess("1",map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==200){
                    Toast.makeText(myActivity.getApplicationContext(), "Checkout ended with success!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(myActivity.getApplicationContext(), "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });

    }

    public String getCheckoutId(){
        return this.checkoutId;
    }

    public void setCheckoutId(String newCheckoutId){
        this.checkoutId = newCheckoutId;
    }

}
