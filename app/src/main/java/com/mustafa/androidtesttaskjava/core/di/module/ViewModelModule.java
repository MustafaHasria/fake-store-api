package com.mustafa.androidtesttaskjava.core.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mustafa.androidtesttaskjava.core.di.ViewModelFactory;
import com.mustafa.androidtesttaskjava.feature.auth.ui.viewmodel.LoginViewModel;
import com.mustafa.androidtesttaskjava.feature.products.ui.viewmodel.ProductDetailViewModel;
import com.mustafa.androidtesttaskjava.feature.products.ui.viewmodel.ProductsViewModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import javax.inject.Provider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Dagger module for ViewModel dependencies.
 */
@Module
public abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindLoginViewModel(LoginViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProductsViewModel.class)
    abstract ViewModel bindProductsViewModel(ProductsViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProductDetailViewModel.class)
    abstract ViewModel bindProductDetailViewModel(ProductDetailViewModel viewModel);

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @dagger.MapKey
    @interface ViewModelKey {
        Class<? extends ViewModel> value();
    }
}
