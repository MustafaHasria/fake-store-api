package com.mustafa.androidtesttaskjava;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mustafa.androidtesttaskjava.R;
import com.mustafa.androidtesttaskjava.feature.auth.ui.LoginActivity;
import com.mustafa.androidtesttaskjava.feature.products.ui.ProductsActivity;

/**
 * Main Activity - Entry point of the application
 * Uses Dagger 2 for Dependency Injection
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get Dagger component and check login status
        App app = (App) getApplication();
        if (app.getAppComponent().authManager().isLoggedIn()) {
            startActivity(new Intent(this, ProductsActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
