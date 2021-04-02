package com.example.shopist.Server.ServerInteraction;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL="http://10.0.2.2:3000";

    public RetrofitManager(){
        initRetrofit();
    }

    private void initRetrofit(){
        //instantiate retrofit settings
        retrofit  = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }

    public RetrofitInterface accessRetrofitInterface(){
        return this.retrofitInterface;
    }

}
