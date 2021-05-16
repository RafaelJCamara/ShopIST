package com.example.shopist.Server.ServerInteraction;

import com.example.shopist.Server.ServerResponses.ServerCart;
import com.example.shopist.Server.ServerResponses.ServerData;
import com.example.shopist.Server.ServerResponses.ServerInitCheckoutToken;
import com.example.shopist.Server.ServerResponses.ServerListToken;
import com.example.shopist.Server.ServerResponses.ServerPantryList;
import com.example.shopist.Server.ServerResponses.ServerProductImageUrl;
import com.example.shopist.Server.ServerResponses.ServerShoppingList;
import com.example.shopist.Server.ServerResponses.ServerUserList;
import com.example.shopist.Server.ServerResponses.UserAccess;
import com.example.shopist.Server.ServerResponses.WaitTimeInfo;
import com.example.shopist.Utils.Other.CartContent;

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

    //get existing pantry lists for the user
    @GET("/list/pantry/userLists/{userId}")
    Call<ServerUserList> getUserCurrentPantryLists(@Path("userId") String userId);

    /*
    * Shopping list routes
    * */

    //create a shopping list
    @POST("/list/shopping")
    Call<ServerListToken> executeShoppingListCreation(@Body HashMap<String,String> map);

    //get a specific shopping list
    @GET("/list/shopping/{id}")
    Call<ServerShoppingList> syncShoppingList(@Path("id") String listId);

    //get existing shopping lists for the user
    @GET("/list/shopping/userLists/{userId}")
    Call<ServerUserList> getUserCurrentShoppingLists(@Path("userId") String userId);



    /*
    * Product routes
    * */


    @POST("/product")
    Call<Void> createProduct(@Body HashMap<String,String> map);

    @POST("/product/{productId}/rateProduct")
    Call<Void> rateProductAtStore(@Path("productId") String productId, @Body HashMap<String,String> map);

    @GET("/product/{productName}/getUrl")
    Call<ServerProductImageUrl> getProductImageUrl(@Path("productName") String imageUrl);


    /*
    * Cart routes
    * */
    @GET("/cart/{shoppingListId}/{userId}")
    Call<ServerCart> getCart(@Path("shoppingListId") String shoppingListId, @Path("userId") String userId);

    @POST("/cart/checkout/{shoppingListId}/{userId}")
    Call<Void> checkoutCart(@Path("shoppingListId") String shoppingListId, @Path("userId") String userId,@Body HashMap<String, CartContent> map);

    @POST("/cart/createCart/{userId}")
    Call<Void> createCart(@Path("userId") String shoppingListId, @Body HashMap<String,String> map);

    /*
     * Store routes
     * */

    @POST("/store/updateProduct")
    Call<Void> updateProductAtStore(@Body HashMap<String,String> map);


    /*
    *   Queue waiting times
    * */
    @POST("/store/{storeId}/initCheckoutProcess")
    Call<ServerInitCheckoutToken> initCheckoutProcess(@Path("storeId") String storeId, @Body HashMap<String,String> map);

    @POST("/store/{storeId}/endCheckoutProcess")
    Call<Void> endCheckoutProcess(@Path("storeId") String storeId, @Body HashMap<String,String> map);

    @GET("/store/{storeId}/currentWaitingTime")
    Call<WaitTimeInfo> getCurrentWaitingTime(@Path("storeId") String storeId);


    /*
    *   Grant user access
    * */

    @POST("/list/pantry/{listId}/grantaccess")
    Call<Void> grantUserAccess(@Path("listId") String listId, @Body HashMap<String,String> map);

    @POST("/list/shopping/{listId}/grantaccess")
    Call<Void> grantUserAccessShopping(@Path("listId") String listId, @Body HashMap<String,String> map);

    @GET("/list/pantry/{listId}/getallusers")
    Call<UserAccess> getAllPantryUsers(@Path("listId") String listId);

    @GET("/list/shopping/{listId}/getallusers")
    Call<UserAccess> getAllShoppingUsers(@Path("listId") String listId);

}
