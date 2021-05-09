package com.example.shopist.Server.ServerInteraction;

import com.example.shopist.Server.ServerResponses.ServerCart;
import com.example.shopist.Server.ServerResponses.ServerData;
import com.example.shopist.Server.ServerResponses.ServerListToken;
import com.example.shopist.Server.ServerResponses.ServerPantryList;
import com.example.shopist.Server.ServerResponses.ServerShoppingList;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

    //update a product to the pantry list (ex. when you change where to buy a product)
    @POST("/list/pantry/{id}/update")
    Call<Void> updatePantry(@Path("id") String listId, @Body HashMap<String,String> map);

    //consume a product from a pantry list
    @POST("/list/pantry/{id}/consume")
    Call<Void> consumeProductPantry(@Path("id") String listId, @Body HashMap<String, String> map);


    /*
    * Shopping list routes
    * */

    //create a shopping list
    @POST("/list/shopping")
    Call<ServerListToken> executeShoppingListCreation(@Body HashMap<String,String> map);

    //get a specific shopping list
    @GET("/list/shopping/{id}")
    Call<ServerShoppingList> syncShoppingList(@Path("id") String listId);

    //get all shopping lists in server (TEMPORARY)
    @GET("/list/shopping")
    Call<ArrayList<ServerShoppingList>> syncAllShoppingList();




    /*
    * Product routes
    * */


    @POST("/product")
    Call<Void> createProduct(@Body HashMap<String,String> map);

    @POST("/product/{productId}/rateProduct")
    Call<Void> rateProductAtStore(@Path("productId") String productId, @Body HashMap<String,String> map);

    /*
    * Cart routes
    * */
    @GET("/cart/{shoppingListId}")
    Call<ServerCart> getCart(@Path("shoppingListId") String shoppingListId);

    @POST("/cart/{shoppingListId}")
    Call<Void> checkoutCart(@Path("shoppingListId") String shoppingListId);

    /*
     * Store routes
     * */

    @POST("/store/updateProduct")
    Call<Void> updateProductAtStore(@Body HashMap<String,String> map);

}
