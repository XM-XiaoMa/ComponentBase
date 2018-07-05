package com.example.component_base.net;

import android.app.Application;
import android.text.TextUtils;

import com.example.component_base.conf.Service;
import com.example.component_base.exception.BaseUrlException;
import com.example.component_base.util.NetworkUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClient {
    private static HttpClient instance;
    private static OkHttpClient okClient;
    private static Application app;
    private Retrofit retrofit;

    private HttpClient() {
    }

    /**
     * 获取HttpClient单例对象
     *
     * @param application
     * @return
     */
    public static synchronized HttpClient getInstance(Application application) {
        if (instance == null) {
            synchronized (HttpClient.class) {
                if (instance == null) {
                    instance = new HttpClient();
                    initOkClient();
                    app = application;
                }
            }
        }
        return instance;
    }

    /**
     * 初始化OKHttpClient
     *
     * @return
     */
    private static synchronized OkHttpClient initOkClient() {
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

    /**
     * 使用retrofit方式传入头信息
     *
     * @param baseUrl
     */
    public void retrofitBuild(String baseUrl) {
        if (TextUtils.isEmpty(baseUrl))
            throw new BaseUrlException("传入的base url为空或空串");
        createRetrofit(baseUrl);
    }

    /**
     * OkHttpClient 方式传入头信息
     *
     * @param baseUrl
     * @param headerParams
     */
    public void retrofitBuildWithHeader(String baseUrl, Map<String, String> headerParams) {
        if (TextUtils.isEmpty(baseUrl))
            throw new BaseUrlException("传入的base url为空或空串");
        addHeader(headerParams);
        createRetrofit(baseUrl);
    }

    private void createRetrofit(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .client(okClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * OkHttpCient 添加头信息
     *
     * @param headerParams
     */
    public void addHeader(final Map<String, String> headerParams) {
        okClient.newBuilder().addInterceptor(new Interceptor() {
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

    /**
     * 添加缓存
     */
    public void addCacheInterceptor() {
        File httpCacheDirectory = new File(app.getCacheDir(), "OkHttpCache");
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        okClient.newBuilder()
                .cache(cache)
                .addNetworkInterceptor(getCacheInterceptor2())
                .addInterceptor(getCacheInterceptor2());
    }

    /**
     * 在无网络的情况下读取缓存，有网络的情况下根据缓存的过期时间重新请求,
     *
     * @return
     */
    public Interceptor getCacheInterceptor2() {
        Interceptor commonParams = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!NetworkUtils.isNetworkAvailable(app)) {
                    request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                okhttp3.Response response = chain.proceed(request);
                if (NetworkUtils.isNetworkAvailable(app)) {
                    String cacheControl = request.cacheControl().toString();
                    //int maxAge = 60 * 60; // read from cache for 1 minute
                    //设置有网络情况下直接走网络，
                    int maxAge = 0;
                    return response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .removeHeader("Pragma")
                            .build();
                } else {
                    //无网络
                    int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                    return response.newBuilder()
                            .header("Cache-Control", "public,only-if-cached,max-stale=" + maxStale)
                            .removeHeader("Pragma")
                            .build();
                }
            }
        };
        return commonParams;
    }
}
