package com.mustafa.androidtesttaskjava.feature.auth.data.repository;

import android.content.Context;
import android.util.Log;

import com.mustafa.androidtesttaskjava.core.auth.AuthManager;
import com.mustafa.androidtesttaskjava.core.network.RetrofitClient;
import com.mustafa.androidtesttaskjava.core.network.TokenRefreshHandler;
import com.mustafa.androidtesttaskjava.feature.auth.data.model.LoginRequest;
import com.mustafa.androidtesttaskjava.feature.auth.data.model.LoginResponse;
import com.mustafa.androidtesttaskjava.feature.auth.data.remote.AuthApiService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Repository for authentication operations.
 * Handles login and token refresh simulation.
 * Encapsulates refresh logic inside AuthManager pattern.
 */
public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private final AuthApiService apiService;
    private final AuthManager authManager;
    private final Context context;

    public AuthRepository(Context context, TokenRefreshHandler refreshHandler) {
        this.context = context.getApplicationContext();
        this.authManager = AuthManager.getInstance(context);
        RetrofitClient retrofitClient = RetrofitClient.getInstance(context, refreshHandler);
        this.apiService = retrofitClient.create(AuthApiService.class);
    }

    /**
     * Login and save token with timestamp
     */
    public LoginResult login(String username, String password) {
        try {
            LoginRequest request = new LoginRequest(username, password);
            Call<LoginResponse> call = apiService.login(request);
            Response<LoginResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                String token = response.body().getToken();
                if (token != null && !token.isEmpty()) {
                    // Save token with timestamp (tokenSavedAt)
                    authManager.saveToken(token);
                    // Save credentials for refresh simulation
                    authManager.saveCredentials(username, password);
                    Log.d(TAG, "Login successful, token saved");
                    return new LoginResult(true, null);
                } else {
                    return new LoginResult(false, "Invalid response from server");
                }
            } else {
                return new LoginResult(false, "Invalid credentials");
            }
        } catch (IOException e) {
            Log.e(TAG, "Login error", e);
            return new LoginResult(false, "Network error: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during login", e);
            return new LoginResult(false, "Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Refresh token by re-calling login endpoint (simulated refresh)
     * Encapsulated in AuthManager logic
     */
    public boolean refreshToken() {
        String username = authManager.getUsername();
        String password = authManager.getPassword();

        if (username == null || password == null) {
            Log.e(TAG, "Cannot refresh: credentials not found");
            return false;
        }

        try {
            LoginRequest request = new LoginRequest(username, password);
            Call<LoginResponse> call = apiService.login(request);
            Response<LoginResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                String token = response.body().getToken();
                if (token != null && !token.isEmpty()) {
                    authManager.saveToken(token);
                    Log.d(TAG, "Token refreshed successfully");
                    return true;
                }
            }
            Log.e(TAG, "Token refresh failed: " + response.code());
            return false;
        } catch (IOException e) {
            Log.e(TAG, "Token refresh network error", e);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during token refresh", e);
            return false;
        }
    }

    /**
     * Logout - clear all auth data
     */
    public void logout() {
        authManager.clearAuth();
        Log.d(TAG, "User logged out");
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return authManager.isLoggedIn();
    }

    /**
     * Result wrapper for login operation
     */
    public static class LoginResult {
        private final boolean success;
        private final String errorMessage;

        public LoginResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
