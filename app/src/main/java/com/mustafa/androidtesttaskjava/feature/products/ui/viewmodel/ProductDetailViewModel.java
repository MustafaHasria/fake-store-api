package com.mustafa.androidtesttaskjava.feature.products.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mustafa.androidtesttaskjava.core.di.scope.AppScope;
import com.mustafa.androidtesttaskjava.feature.auth.data.repository.AuthRepository;
import com.mustafa.androidtesttaskjava.feature.products.data.model.Product;
import com.mustafa.androidtesttaskjava.feature.products.data.repository.ProductRepository;

import javax.inject.Inject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for Product Detail feature following MVVM architecture.
 * Uses Dagger 2 for Dependency Injection.
 */
@AppScope
public class ProductDetailViewModel extends AndroidViewModel {
    private final ProductRepository productRepository;
    private final AuthRepository authRepository;
    private final ExecutorService executorService;
    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> logoutSuccess = new MutableLiveData<>();

    @Inject
    public ProductDetailViewModel(@NonNull Application application,
                                   AuthRepository authRepository,
                                   ProductRepository productRepository) {
        super(application);
        this.authRepository = authRepository;
        this.productRepository = productRepository;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void loadProduct(int productId) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        executorService.execute(() -> {
            ProductRepository.ProductResult<Product> result = productRepository.getProduct(productId);
            isLoading.postValue(false);

            if (result.isSuccess()) {
                product.postValue(result.getData());
            } else {
                errorMessage.postValue(result.getErrorMessage());
            }
        });
    }

    public LiveData<Product> getProduct() {
        return product;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getLogoutSuccess() {
        return logoutSuccess;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
