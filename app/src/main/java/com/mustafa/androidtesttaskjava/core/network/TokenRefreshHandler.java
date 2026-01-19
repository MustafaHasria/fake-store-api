package com.mustafa.androidtesttaskjava.core.network;

/**
 * Interface for handling token refresh operations.
 * Implementations should simulate refresh by re-calling login endpoint.
 */
public interface TokenRefreshHandler {
    /**
     * Refresh the authentication token by re-calling login endpoint.
     * This simulates a refresh token flow since FakeStore API doesn't have a refresh endpoint.
     *
     * @return true if refresh succeeded, false otherwise
     */
    boolean refreshToken();

    /**
     * Called when token refresh fails.
     * Should force logout to prevent infinite retry loops.
     */
    void onRefreshFailed();
}
