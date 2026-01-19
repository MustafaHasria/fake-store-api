package com.mustafa.androidtesttaskjava.core.network;

import android.content.Context;
import android.util.Log;

import com.mustafa.androidtesttaskjava.core.auth.AuthManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp Interceptor that automatically adds Authorization header with Bearer token.
 * Handles token expiration and refresh flow:
 * - Never sends expired tokens
 * - Automatically triggers refresh when token expires
 * - Handles concurrency: only one refresh runs, others wait
 */
public class AuthInterceptor implements Interceptor {
    private static final String TAG = "AuthInterceptor";
    private final AuthManager authManager;
    private final TokenRefreshHandler tokenRefreshHandler;

    public AuthInterceptor(Context context, TokenRefreshHandler tokenRefreshHandler) {
        this.authManager = AuthManager.getInstance(context);
        this.tokenRefreshHandler = tokenRefreshHandler;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String path = originalRequest.url().encodedPath();

        // Skip auth for login endpoint
        if (path.contains("/auth/login")) {
            Log.d(TAG, "Skipping auth for login endpoint");
            return chain.proceed(originalRequest);
        }

        // CRITICAL: Check if token is expired BEFORE adding Authorization header
        if (authManager.isTokenExpired()) {
            Log.d(TAG, "Token expired, attempting refresh before sending request");

            // Handle concurrent requests - ensure only one refresh runs
            if (authManager.isRefreshing()) {
                // Another thread is already refreshing, wait for it
                Log.d(TAG, "Another thread is refreshing, waiting...");
                authManager.waitForRefresh();
            } else {
                // This thread should perform refresh
                if (authManager.startRefresh()) {
                    try {
                        Log.d(TAG, "This thread will perform token refresh");
                        boolean refreshSuccess = tokenRefreshHandler.refreshToken();
                        if (!refreshSuccess) {
                            // Refresh failed, force logout (prevent infinite retry)
                            Log.e(TAG, "Token refresh failed, forcing logout");
                            tokenRefreshHandler.onRefreshFailed();
                            // Return response without Authorization header
                            return chain.proceed(originalRequest.newBuilder()
                                    .removeHeader("Authorization")
                                    .build());
                        }
                        Log.d(TAG, "Token refresh succeeded");
                    } finally {
                        authManager.completeRefresh();
                    }
                } else {
                    // Race condition: another thread started refresh between check and start
                    Log.d(TAG, "Another thread started refresh, waiting...");
                    authManager.waitForRefresh();
                }
            }
        }

        // Get valid token (after potential refresh)
        String token = authManager.getToken();
        if (token != null && !token.isEmpty()) {
            // CRITICAL: Only add Authorization header if token is valid
            Request authenticatedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            Log.d(TAG, "Adding Authorization header with valid token");
            return chain.proceed(authenticatedRequest);
        }

        // No valid token available, proceed without Authorization header
        Log.w(TAG, "No valid token available, proceeding without Authorization header");
        return chain.proceed(originalRequest);
    }
}
