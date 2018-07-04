package com.example.component_base.net;

import android.text.TextUtils;

import com.example.component_base.conf.Service;
import com.example.component_base.exception.BaseUrlException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClient {
    private static HttpClient instance;
    private OkHttpClient okClient;
    private Retrofit retrofit;

    private HttpClient() {
    }

    private synchronized OkHttpClient getOkClient() {
        if (okClient == null) {
            synchronized (HttpClient.class) {
                if (okClient == null) {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.connectTimeout(Service.CONNECT_TIMEOUT, TimeUnit.SECONDS);
                    builder.writeTimeout(Service.WRITE_TIMEOUT, TimeUnit.SECONDS);
                    builder.readTimeout(Service.READ_TIMEOUT, TimeUnit.SECONDS);
                    okClient = builder.build();
                }
            }
        }
        return okClient;
    }

    public void addHeader(final Map<String, String> headerParams) {
        getOkClient().newBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();
                for (Map.Entry<String, String> param : headerParams.entrySet()) {
                    builder.addHeader(param.getKey(), param.getValue());
                }
                Request request = builder.build();
                return chain.proceed(request);
            }
        });
    }

    public static synchronized HttpClient getInstance() {
        if (instance == null) {
            synchronized (HttpClient.class) {
                if (instance == null) {
                    instance = new HttpClient();
                }
            }
        }
        return instance;
    }

    public void retrofitBuild(String baseUrl) {
        if (TextUtils.isEmpty(baseUrl))
            throw new BaseUrlException("传入的base url为空或空串");
        retrofit = new Retrofit.Builder()
                .client(getOkClient())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build();
    }

    public void retrofitBuildWithHeader(String baseUrl, Map<String, String> headerParams) {
        if (TextUtils.isEmpty(baseUrl))
            throw new BaseUrlException("传入的base url为空或空串");
        addHeader(headerParams);
        retrofit = new Retrofit.Builder()
                .client(getOkClient())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build();
    }
}
