package com.example.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author Is-Poson
 * @time 2018/7/5  22:59
 * @desc ${TODO}
 */

public interface WanAndroidAPI {
    @GET("/article/list/{index}/json")
    Call<String> getMainList(@Path("index") int index);
}
