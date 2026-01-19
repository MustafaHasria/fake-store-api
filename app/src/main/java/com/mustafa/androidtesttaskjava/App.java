package com.mustafa.androidtesttaskjava;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.mustafa.androidtesttaskjava.core.di.component.AppComponent;
import com.mustafa.androidtesttaskjava.core.di.component.DaggerAppComponent;
import com.mustafa.androidtesttaskjava.core.di.module.AppModule;
import com.mustafa.androidtesttaskjava.core.di.module.NetworkModule;
import com.mustafa.androidtesttaskjava.core.di.module.RepositoryModule;
import com.mustafa.androidtesttaskjava.core.di.module.ViewModelModule;

/**
 * Application class to enforce light mode and initialize Dagger.
 * Prevents the app from switching to dark mode regardless of system settings.
 */
public class App extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Force light mode - disable dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        
        // Initialize Dagger
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule())
                .repositoryModule(new RepositoryModule())
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
