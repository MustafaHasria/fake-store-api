package com.mustafa.androidtesttaskjava.core.di.module;

import android.app.Application;
import android.content.Context;

import com.mustafa.androidtesttaskjava.core.auth.AuthManager;
import com.mustafa.androidtesttaskjava.core.di.scope.AppScope;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for application-level dependencies.
 */
@Module
public class AppModule {
    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @AppScope
    Application provideApplication() {
        return application;
    }

    @Provides
    @AppScope
    Context provideContext() {
        return application.getApplicationContext();
    }

    @Provides
    @AppScope
    AuthManager provideAuthManager(Context context) {
        return AuthManager.getInstance(context);
    }
}
