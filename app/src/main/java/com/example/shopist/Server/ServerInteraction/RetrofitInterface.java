package com.example.shopist.Server.ServerInteraction;

import com.example.shopist.Server.ServerResponses.ServerData;
import com.example.shopist.Server.ServerResponses.ServerListToken;
import com.example.shopist.Server.ServerResponses.ServerPantryList;
import com.example.shopist.Server.ServerResponses.ServerShoppingList;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface RetrofitInterface {

    /*
    * User routes
    * */

    @POST("/user/login")
    Call<ServerData> executeLogin(@Body HashMap<String,String> map);

    @POST("/user/signup")
    Call<Void> executeSignup(@Body HashMap<String,String> map);


    /*
    * Pantry list routes
    * */

    //create a pantry list
    @POST("/list/pantry")
    Call<ServerListToken> executePantryListCreation(@Body HashMap<String,String> map);

    //add a product to the pantry list
    @POST("/list/pantry/{id}/addProduct")
    Call<Void> addProductToPantry(@Path("id") String listId, @Body HashMap<String,String> map);

    //get a specific pantry list
    @GET("/list/pantry/{id}")
    Call<ServerPantryList> syncPantryList(@Path("id") String listId);


    /*
    * Shopping list routes
    * */

    //create a shopping list
    @POST("/list/shopping")
    Call<ServerListToken> executeShoppingListCreation(@Body HashMap<String,String> map);

    //get a specific shopping list
    @GET("/list/shopping/{id}")
    Call<ServerShoppingList> syncShoppingList(@Path("id") String listId);


    /*
    * Product routes
    * */


    @POST("/product")
    Call<Void> createProduct(@Body HashMap<String,String> map);



    /*
    * Cart routes
    * */
}