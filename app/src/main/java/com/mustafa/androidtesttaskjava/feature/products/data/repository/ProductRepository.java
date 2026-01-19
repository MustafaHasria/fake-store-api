package com.mustafa.androidtesttaskjava.feature.products.data.repository;

import android.content.Context;
import android.util.Log;

import com.mustafa.androidtesttaskjava.core.network.RetrofitClient;
import com.mustafa.androidtesttaskjava.core.network.TokenRefreshHandler;
import com.mustafa.androidtesttaskjava.feature.products.data.model.Product;
import com.mustafa.androidtesttaskjava.feature.products.data.remote.ProductApiService;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Repository for product operations.
 * All requests are protected and go through AuthInterceptor.
 */
public class ProductRepository {
    private static final String TAG = "ProductRepository";
    private final ProductApiService apiService;

    public ProductRepository(Context context, TokenRefreshHandler refreshHandler) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance(context, refreshHandler);
        this.apiService = retrofitClient.create(ProductApiService.class);
    }

    /**
     * Get all products
     */
    public ProductResult<List<Product>> getProducts() {
        try {
            Call<List<Product>> call = apiService.getProducts();
            Response<List<Product>> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                return new ProductResult<>(true, response.body(), null);
            } else {
                return new ProductResult<>(false, null, "Failed to load products: " + response.code());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading products", e);
            return new ProductResult<>(false, null, "Network error: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error loading products", e);
            return new ProductResult<>(false, null, "Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Get product by ID
     */
    public ProductResult<Product> getProduct(int id) {
        try {
            Call<Product> call = apiService.getProduct(id);
            Response<Product> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                return new ProductResult<>(true, response.body(), null);
            } else {
                return new ProductResult<>(false, null, "Failed to load product: " + response.code());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading product", e);
            return new ProductResult<>(false, null, "Network error: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error loading product", e);
            return new ProductResult<>(false, null, "Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Result wrapper for product operations
     */
    public static class ProductResult<T> {
        private final boolean success;
        private final T data;
        private final String errorMessage;

        public ProductResult(boolean success, T data, String errorMessage) {
            this.success = success;
            this.data = data;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        public T getData() {
            return data;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
