package com.mustafa.androidtesttaskjava.core.di.component;

import androidx.lifecycle.ViewModelProvider;

import com.mustafa.androidtesttaskjava.core.auth.AuthManager;
import com.mustafa.androidtesttaskjava.core.di.module.AppModule;
import com.mustafa.androidtesttaskjava.core.di.module.NetworkModule;
import com.mustafa.androidtesttaskjava.core.di.module.RepositoryModule;
import com.mustafa.androidtesttaskjava.core.di.module.ViewModelModule;
import com.mustafa.androidtesttaskjava.core.di.scope.AppScope;
import com.mustafa.androidtesttaskjava.feature.auth.data.repository.AuthRepository;
import com.mustafa.androidtesttaskjava.feature.products.data.repository.ProductRepository;

import dagger.Component;

/**
 * Dagger component for application-level dependencies.
 */
@AppScope
@Component(modules = {AppModule.class, NetworkModule.class, RepositoryModule.class, ViewModelModule.class})
public interface AppComponent {
    AuthManager authManager();
    AuthRepository authRepository();
    ProductRepository productRepository();
    ViewModelProvider.Factory viewModelFactory();
}
