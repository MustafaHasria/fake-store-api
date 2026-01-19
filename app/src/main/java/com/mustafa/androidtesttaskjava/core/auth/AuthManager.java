package com.mustafa.androidtesttaskjava.core.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages authentication tokens, expiration, and refresh coordination.
 * Encapsulates all token-related logic including:
 * - Token storage with timestamp
 * - 60-second expiration check
 * - Concurrency handling for token refresh
 */
public class AuthManager {
    private static final String TAG = "AuthManager";
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_TOKEN_SAVED_AT = "token_saved_at";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final long TOKEN_VALIDITY_DURATION = 60 * 1000; // 60 seconds in milliseconds

    private static AuthManager instance;
    private final SharedPreferences preferences;
    private final AtomicBoolean isRefreshing = new AtomicBoolean(false);
    private CountDownLatch refreshLatch;

    private AuthManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Save token and timestamp (tokenSavedAt)
     */
    public void saveToken(String token) {
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Cannot save null or empty token");
            return;
        }
        long currentTime = System.currentTimeMillis();
        preferences.edit()
                .putString(KEY_TOKEN, token)
                .putLong(KEY_TOKEN_SAVED_AT, currentTime)
                .apply();
        Log.d(TAG, "Token saved at: " + currentTime);
    }

    /**
     * Get current token if valid, null if expired or not exists
     * Token is considered expired after 60 seconds from tokenSavedAt
     */
    public String getToken() {
        String token = preferences.getString(KEY_TOKEN, null);
        if (token == null) {
            return null;
        }

        long savedAt = preferences.getLong(KEY_TOKEN_SAVED_AT, 0);
        if (isTokenExpired(savedAt)) {
            Log.d(TAG, "Token expired - elapsed time exceeds 60 seconds");
            return null;
        }

        return token;
    }

    /**
     * Check if token is expired
     * Token expires 60 seconds after tokenSavedAt timestamp
     */
    public boolean isTokenExpired() {
        long savedAt = preferences.getLong(KEY_TOKEN_SAVED_AT, 0);
        return isTokenExpired(savedAt);
    }

    private boolean isTokenExpired(long savedAt) {
        if (savedAt == 0) {
            return true;
        }
        long elapsed = System.currentTimeMillis() - savedAt;
        boolean expired = elapsed >= TOKEN_VALIDITY_DURATION;
        if (expired) {
            Log.d(TAG, "Token expired: " + elapsed + "ms elapsed (limit: " + TOKEN_VALIDITY_DURATION + "ms)");
        }
        return expired;
    }

    /**
     * Save credentials for refresh simulation
     */
    public void saveCredentials(String username, String password) {
        preferences.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_PASSWORD, password)
                .apply();
    }

    /**
     * Get saved username for refresh
     */
    public String getUsername() {
        return preferences.getString(KEY_USERNAME, null);
    }

    /**
     * Get saved password for refresh
     */
    public String getPassword() {
        return preferences.getString(KEY_PASSWORD, null);
    }

    /**
     * Clear all auth data
     */
    public void clearAuth() {
        preferences.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_TOKEN_SAVED_AT)
                .remove(KEY_USERNAME)
                .remove(KEY_PASSWORD)
                .apply();
        Log.d(TAG, "Auth data cleared");
    }

    /**
     * Check if user is logged in (has valid token)
     */
    public boolean isLoggedIn() {
        return getToken() != null;
    }

    /**
     * Start refresh process - returns true if this thread should perform refresh
     * Returns false if another thread is already refreshing
     * Uses AtomicBoolean.compareAndSet for thread-safe concurrency handling
     */
    public boolean startRefresh() {
        if (isRefreshing.compareAndSet(false, true)) {
            refreshLatch = new CountDownLatch(1);
            Log.d(TAG, "Starting token refresh - this thread will perform refresh");
            return true;
        }
        Log.d(TAG, "Refresh already in progress - this thread will wait");
        return false;
    }

    /**
     * Wait for refresh to complete (for concurrent requests)
     * Uses CountDownLatch to synchronize multiple threads waiting for refresh
     */
    public void waitForRefresh() {
        if (refreshLatch != null) {
            try {
                Log.d(TAG, "Waiting for token refresh to complete");
                refreshLatch.await();
                Log.d(TAG, "Token refresh completed, resuming request");
            } catch (InterruptedException e) {
                Log.e(TAG, "Interrupted while waiting for refresh", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Complete refresh process
     * Releases all waiting threads via CountDownLatch
     */
    public void completeRefresh() {
        isRefreshing.set(false);
        if (refreshLatch != null) {
            refreshLatch.countDown();
            refreshLatch = null;
        }
        Log.d(TAG, "Token refresh completed - all waiting threads notified");
    }

    /**
     * Check if refresh is in progress
     */
    public boolean isRefreshing() {
        return isRefreshing.get();
    }
}
