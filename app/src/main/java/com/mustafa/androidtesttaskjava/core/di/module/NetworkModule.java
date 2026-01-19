package com.mustafa.androidtesttaskjava.core.di.module;

import android.content.Context;

import com.mustafa.androidtesttaskjava.core.di.scope.AppScope;
import com.mustafa.androidtesttaskjava.core.network.RetrofitClient;
import com.mustafa.androidtesttaskjava.core.network.TokenRefreshHandler;
import com.mustafa.androidtesttaskjava.core.network.TokenRefreshHandlerImpl;
import com.mustafa.androidtesttaskjava.feature.auth.data.remote.AuthApiService;
import com.mustafa.androidtesttaskjava.feature.products.data.remote.ProductApiService;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Dagger module for network dependencies.
 */
@Module
public class NetworkModule {
    private static final String BASE_URL = "https://fakestoreapi.com/";

    @Provides
    @AppScope
    @Named("temp")
    Retrofit provideTempRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @AppScope
    @Named("temp")
    AuthApiService provideTempAuthApiService(@Named("temp") Retrofit retrofit) {
        return retrofit.create(AuthApiService.class);
    }

    @Provides
    @AppScope
    TokenRefreshHandler provideTokenRefreshHandler(Context context, @Named("temp") AuthApiService tempAuthApiService) {
        return new TokenRefreshHandlerImpl(context, tempAuthApiService);
    }

    @Provides
    @AppScope
    RetrofitClient provideRetrofitClient(Context context, TokenRefreshHandler tokenRefreshHandler) {
        return RetrofitClient.getInstance(context, tokenRefreshHandler);
    }

    @Provides
    @AppScope
    AuthApiService provideAuthApiService(RetrofitClient retrofitClient, TokenRefreshHandler tokenRefreshHandler) {
        AuthApiService authApiService = retrofitClient.create(AuthApiService.class);
        // Update TokenRefreshHandler with the real AuthApiService
        if (tokenRefreshHandler instanceof TokenRefreshHandlerImpl) {
            ((TokenRefreshHandlerImpl) tokenRefreshHandler).setAuthApiService(authApiService);
        }
        return authApiService;
    }

    @Provides
    @AppScope
    ProductApiService provideProductApiService(RetrofitClient retrofitClient) {
        return retrofitClient.create(ProductApiService.class);
    }
}
