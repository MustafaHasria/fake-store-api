package com.mustafa.androidtesttaskjava.feature.products.data.remote;

import com.mustafa.androidtesttaskjava.feature.products.data.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * API service for product endpoints.
 * All requests are protected and require Authorization header (added by AuthInterceptor).
 */
public interface ProductApiService {
    @GET("products")
    Call<List<Product>> getProducts();

    @GET("products/{id}")
    Call<Product> getProduct(@Path("id") int id);
}
