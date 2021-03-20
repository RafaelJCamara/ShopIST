package com.example.shopist;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {
    @POST("/login")
    Call<ServerData> executeServerCall(@Body HashMap<String,String> map);
}
