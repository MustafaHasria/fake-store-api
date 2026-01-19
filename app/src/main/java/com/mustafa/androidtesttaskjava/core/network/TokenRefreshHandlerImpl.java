package com.mustafa.androidtesttaskjava.core.network;

import android.content.Context;
import android.util.Log;

import com.mustafa.androidtesttaskjava.core.auth.AuthManager;
import com.mustafa.androidtesttaskjava.feature.auth.data.model.LoginRequest;
import com.mustafa.androidtesttaskjava.feature.auth.data.model.LoginResponse;
import com.mustafa.androidtesttaskjava.feature.auth.data.remote.AuthApiService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Implementation of TokenRefreshHandler.
 * Encapsulates refresh logic - simulates refresh by re-calling login endpoint.
 * Uses AuthManager to get credentials and save new token.
 */
public class TokenRefreshHandlerImpl implements TokenRefreshHandler {
    private static final String TAG = "TokenRefreshHandlerImpl";
    private final Context context;
    private AuthApiService authApiService;
    private final AuthManager authManager;
    private OnRefreshFailedListener onRefreshFailedListener;

    public TokenRefreshHandlerImpl(Context context, AuthApiService authApiService) {
        this.context = context.getApplicationContext();
        this.authApiService = authApiService;
        this.authManager = AuthManager.getInstance(this.context);
    }

    public void setAuthApiService(AuthApiService authApiService) {
        this.authApiService = authApiService;
    }

    public void setOnRefreshFailedListener(OnRefreshFailedListener listener) {
        this.onRefreshFailedListener = listener;
    }

    @Override
    public boolean refreshToken() {
        String username = authManager.getUsername();
        String password = authManager.getPassword();

        if (username == null || password == null) {
            Log.e(TAG, "Cannot refresh: credentials not found");
            return false;
        }

        try {
            LoginRequest request = new LoginRequest(username, password);
            Call<LoginResponse> call = authApiService.login(request);
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

    @Override
    public void onRefreshFailed() {
        Log.e(TAG, "Token refresh failed, forcing logout");
        authManager.clearAuth();
        if (onRefreshFailedListener != null) {
            onRefreshFailedListener.onRefreshFailed();
        }
    }

    public interface OnRefreshFailedListener {
        void onRefreshFailed();
    }
}
