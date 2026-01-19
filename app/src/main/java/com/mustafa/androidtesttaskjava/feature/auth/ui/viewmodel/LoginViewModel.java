package com.mustafa.androidtesttaskjava.feature.auth.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mustafa.androidtesttaskjava.core.di.scope.AppScope;
import com.mustafa.androidtesttaskjava.feature.auth.data.repository.AuthRepository;

import javax.inject.Inject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for Login feature following MVVM architecture.
 * Uses Dagger 2 for Dependency Injection.
 */
@AppScope
public class LoginViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final ExecutorService executorService;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    @Inject
    public LoginViewModel(@NonNull Application application, AuthRepository authRepository) {
        super(application);
        this.authRepository = authRepository;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            errorMessage.setValue("Username cannot be empty");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            errorMessage.setValue("Password cannot be empty");
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);

        executorService.execute(() -> {
            AuthRepository.LoginResult result = authRepository.login(username.trim(), password);
            isLoading.postValue(false);

            if (result.isSuccess()) {
                loginSuccess.postValue(true);
            } else {
                errorMessage.postValue(result.getErrorMessage());
                loginSuccess.postValue(false);
            }
        });
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
