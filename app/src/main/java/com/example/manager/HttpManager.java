package com.example.manager;

import android.app.Application;

import com.example.component_base.net.HttpClient;
import com.example.service.WanAndroidAPI;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author Is-Poson
 * @time 2018/7/5  22:52
 * @desc ${TODO}
 */

public class HttpManager {

    private static HttpManager httpManager;
    private static final String BASE_URL = "http://www.wanandroid.com";
    private static HttpClient httpClient;
    private static Application app;

    private HttpManager() {
    }

    public static synchronized HttpManager getInstance(Application application) {
        if (httpManager == null) {
            synchronized (HttpManager.class) {
                if (httpManager == null) {
                    httpManager = new HttpManager();
                    httpClient = HttpClient.getInstance(app);
                }
            }
        }
        return httpManager;
    }

    public Call<String> getMainList(int index, Callback<String> callBack) {
        Call<String> call = httpClient.retrofit(BASE_URL).create(WanAndroidAPI.class).getMainList(index);
        call.enqueue(callBack);
        return call;
    }

}
