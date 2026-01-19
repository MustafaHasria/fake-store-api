package com.mustafa.androidtesttaskjava.core.di.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Application scope for Dagger dependencies.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface AppScope {
}
