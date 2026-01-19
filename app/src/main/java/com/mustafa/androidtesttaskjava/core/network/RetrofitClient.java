package com.mustafa.androidtesttaskjava.core.network;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit client factory with authentication interceptor.
 * Ensures all requests go through AuthInterceptor for token handling.
 */
public class RetrofitClient {
    private static final String BASE_URL = "https://fakestoreapi.com/";
    private static RetrofitClient instance;
    private final Retrofit retrofit;
    private final OkHttpClient okHttpClient;
    private TokenRefreshHandler refreshHandler;

    private RetrofitClient(Context context, TokenRefreshHandler handler) {
        this.refreshHandler = handler;
        
        // Logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Auth interceptor - handles token expiration and refresh
        AuthInterceptor authInterceptor = new AuthInterceptor(context, refreshHandler);

        // OkHttp client with interceptors
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build();

        // Retrofit instance
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance(Context context, TokenRefreshHandler handler) {
        if (instance == null) {
            instance = new RetrofitClient(context.getApplicationContext(), handler);
        } else if (handler != null && instance.refreshHandler != handler) {
            // Update handler if different
            instance.refreshHandler = handler;
        }
        return instance;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
