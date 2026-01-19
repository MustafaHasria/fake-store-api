package com.mustafa.androidtesttaskjava.feature.products.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.mustafa.androidtesttaskjava.App;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mustafa.androidtesttaskjava.R;
import com.mustafa.androidtesttaskjava.feature.auth.ui.LoginActivity;
import com.mustafa.androidtesttaskjava.feature.products.data.model.Product;
import com.mustafa.androidtesttaskjava.feature.products.ui.adapter.ProductAdapter;
import com.mustafa.androidtesttaskjava.feature.products.ui.viewmodel.ProductsViewModel;

/**
 * Products Activity - Products Feature UI
 */
public class ProductsActivity extends AppCompatActivity {
    private RecyclerView rvProducts;
    private ProgressBar progressBar;
    private TextView tvError;
    private ProductAdapter adapter;
    private ProductsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_products);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Handle window insets properly for AppBarLayout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.app_bar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initializeViews();
        setupRecyclerView();
        setupViewModel();
    }

    private void initializeViews() {
        rvProducts = findViewById(R.id.rv_products);
        progressBar = findViewById(R.id.progress_bar);
        tvError = findViewById(R.id.tv_error);
        findViewById(R.id.card_error).setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(product -> {
            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(adapter);
    }

    private void setupViewModel() {
        App app = (App) getApplication();
        ViewModelProvider.Factory factory = app.getAppComponent().viewModelFactory();
        viewModel = new ViewModelProvider(this, factory).get(ProductsViewModel.class);

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            rvProducts.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

        viewModel.getProducts().observe(this, products -> {
            if (products != null && !products.isEmpty()) {
                adapter.setProducts(products);
                findViewById(R.id.card_error).setVisibility(View.GONE);
                rvProducts.setVisibility(View.VISIBLE);
            } else {
                tvError.setText(R.string.no_products);
                findViewById(R.id.card_error).setVisibility(View.VISIBLE);
                rvProducts.setVisibility(View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                tvError.setText(errorMessage);
                findViewById(R.id.card_error).setVisibility(View.VISIBLE);
                rvProducts.setVisibility(View.GONE);
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            } else {
                findViewById(R.id.card_error).setVisibility(View.GONE);
            }
        });

        viewModel.getLogoutSuccess().observe(this, success -> {
            if (success != null && success) {
                navigateToLogin();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_products, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            showLogoutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.logout, (dialog, which) -> {
                    viewModel.logout();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
