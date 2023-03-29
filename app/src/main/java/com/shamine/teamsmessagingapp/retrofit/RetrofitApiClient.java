package com.shamine.teamsmessagingapp.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApiClient {
    private static final String IP_ADDRESS = "192.168.0.102";
    private static final String BASE_URL = "http://" + IP_ADDRESS + ":8080/api/";
    public static final String WEB_SOCKET_BASE_URL = "ws://" + IP_ADDRESS + ":8080/api/websocket";
    public static final String IMAGE_BASE_URL = "http://" + IP_ADDRESS;
    public static final String TOKEN_PREFIX = "Bearer ";
    private static Retrofit retrofit = null;

    public static Retrofit getApiClient()
    {
        if (retrofit == null)
        {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
