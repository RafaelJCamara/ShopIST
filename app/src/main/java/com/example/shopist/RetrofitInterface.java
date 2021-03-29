package com.example.shopist;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface RetrofitInterface {
    @POST("/user/login")
    Call<ServerData> executeLogin(@Body HashMap<String,String> map);

    @POST("/user/signup")
    Call<Void> executeSignup(@Body HashMap<String,String> map);

    @GET
    Call<ListServerData> getList(@Url String url);
}
