package com.mustafa.androidtesttaskjava.feature.products.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.mustafa.androidtesttaskjava.App;
import com.mustafa.androidtesttaskjava.R;
import com.mustafa.androidtesttaskjava.feature.auth.ui.LoginActivity;
import com.mustafa.androidtesttaskjava.feature.products.ui.fragment.ProductDetailFragment;
import com.mustafa.androidtesttaskjava.feature.products.ui.viewmodel.ProductDetailViewModel;

/**
 * Product Detail Activity - Products Feature UI
 */
public class ProductDetailActivity extends AppCompatActivity {
    private ProductDetailViewModel viewModel;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        productId = getIntent().getIntExtra("product_id", -1);
        if (productId == -1) {
            finish();
            return;
        }

        setupFragment();
        setupViewModel();
    }

    private void setupFragment() {
        ProductDetailFragment fragment = ProductDetailFragment.newInstance(productId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void setupViewModel() {
        App app = (App) getApplication();
        ViewModelProvider.Factory factory = app.getAppComponent().viewModelFactory();
        viewModel = new ViewModelProvider(this, factory).get(ProductDetailViewModel.class);

        viewModel.getLogoutSuccess().observe(this, success -> {
            if (success != null && success) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
