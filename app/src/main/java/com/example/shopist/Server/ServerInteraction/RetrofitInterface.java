package com.example.shopist.Server.ServerInteraction;

import com.example.shopist.Server.ServerResponses.CartServerData;
import com.example.shopist.Server.ServerResponses.ListServerData;
import com.example.shopist.Server.ServerResponses.ServerData;
import com.example.shopist.Server.ServerResponses.ServerListToken;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface RetrofitInterface {
    @POST("/cart/login")
    Call<ServerData> executeLogin(@Body HashMap<String,String> map);

    @POST("/cart/signup")
    Call<Void> executeSignup(@Body HashMap<String,String> map);

    @GET
    Call<ListServerData> getList(@Url String url);

    @POST("/list")
    Call<ServerListToken> executeListCreation(@Body HashMap<String,String> map);

    @POST("/product")
    Call<Void> createProduct(@Body HashMap<String,String> map);

    @GET
    Call<CartServerData> getCart(@Url String url);

    @POST
    Call<Void> checkoutCart(@Url String url);

}
