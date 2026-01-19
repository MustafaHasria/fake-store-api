package com.mustafa.androidtesttaskjava.feature.auth.data.remote;

import com.mustafa.androidtesttaskjava.feature.auth.data.model.LoginRequest;
import com.mustafa.androidtesttaskjava.feature.auth.data.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * API service for authentication endpoints.
 */
public interface AuthApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
