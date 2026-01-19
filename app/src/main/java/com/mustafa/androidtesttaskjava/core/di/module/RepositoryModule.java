package com.mustafa.androidtesttaskjava.core.di.module;

import android.content.Context;

import com.mustafa.androidtesttaskjava.core.di.scope.AppScope;
import com.mustafa.androidtesttaskjava.core.network.TokenRefreshHandler;
import com.mustafa.androidtesttaskjava.feature.auth.data.repository.AuthRepository;
import com.mustafa.androidtesttaskjava.feature.products.data.repository.ProductRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for repository dependencies.
 */
@Module
public class RepositoryModule {

    @Provides
    @AppScope
    AuthRepository provideAuthRepository(Context context, TokenRefreshHandler tokenRefreshHandler) {
        return new AuthRepository(context, tokenRefreshHandler);
    }

    @Provides
    @AppScope
    ProductRepository provideProductRepository(Context context, TokenRefreshHandler tokenRefreshHandler) {
        return new ProductRepository(context, tokenRefreshHandler);
    }
}
