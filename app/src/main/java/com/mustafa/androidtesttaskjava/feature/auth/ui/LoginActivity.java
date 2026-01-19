package com.mustafa.androidtesttaskjava.feature.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mustafa.androidtesttaskjava.App;
import com.mustafa.androidtesttaskjava.R;
import com.mustafa.androidtesttaskjava.feature.auth.ui.viewmodel.LoginViewModel;
import com.mustafa.androidtesttaskjava.feature.products.ui.ProductsActivity;

/**
 * Login Activity - Auth Feature UI
 */
public class LoginActivity extends AppCompatActivity {
    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;
    private TextView tvError;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupViewModel();
        setupListeners();
    }

    private void initializeViews() {
        tilUsername = findViewById(R.id.til_username);
        tilPassword = findViewById(R.id.til_password);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
        tvError = findViewById(R.id.tv_error);
    }

    private void setupViewModel() {
        App app = (App) getApplication();
        ViewModelProvider.Factory factory = app.getAppComponent().viewModelFactory();
        viewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnLogin.setEnabled(!isLoading);
            etUsername.setEnabled(!isLoading);
            etPassword.setEnabled(!isLoading);
        });

        viewModel.getLoginSuccess().observe(this, success -> {
            if (success != null && success) {
                navigateToProducts();
            }
        });

        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                tvError.setText(errorMessage);
                tvError.setVisibility(View.VISIBLE);
            } else {
                tvError.setVisibility(View.GONE);
            }
        });
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText() != null ? etUsername.getText().toString() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

            if (TextUtils.isEmpty(username)) {
                tilUsername.setError(getString(R.string.username_hint) + " cannot be empty");
                return;
            }
            tilUsername.setError(null);

            if (TextUtils.isEmpty(password)) {
                tilPassword.setError(getString(R.string.password_hint) + " cannot be empty");
                return;
            }
            tilPassword.setError(null);

            viewModel.login(username, password);
        });
    }

    private void navigateToProducts() {
        Intent intent = new Intent(this, ProductsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
