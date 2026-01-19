package com.mustafa.androidtesttaskjava.feature.products.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mustafa.androidtesttaskjava.App;

import com.mustafa.androidtesttaskjava.R;
import com.mustafa.androidtesttaskjava.feature.products.data.model.Product;
import com.mustafa.androidtesttaskjava.feature.products.ui.viewmodel.ProductDetailViewModel;

/**
 * Product Detail Fragment - Products Feature UI
 */
public class ProductDetailFragment extends Fragment {
    private static final String ARG_PRODUCT_ID = "product_id";

    private TextView tvProductTitle;
    private TextView tvProductPrice;
    private TextView tvProductCategory;
    private TextView tvProductDescription;
    private TextView tvProductRating;
    private ProgressBar progressBar;
    private ProductDetailViewModel viewModel;
    private int productId;

    public static ProductDetailFragment newInstance(int productId) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getInt(ARG_PRODUCT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupViewModel();
    }

    private void initializeViews(View view) {
        tvProductTitle = view.findViewById(R.id.tv_product_title);
        tvProductPrice = view.findViewById(R.id.tv_product_price);
        tvProductCategory = view.findViewById(R.id.tv_product_category);
        tvProductDescription = view.findViewById(R.id.tv_product_description);
        tvProductRating = view.findViewById(R.id.tv_product_rating);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupViewModel() {
        App app = (App) requireActivity().getApplication();
        ViewModelProvider.Factory factory = app.getAppComponent().viewModelFactory();
        viewModel = new ViewModelProvider(this, factory).get(ProductDetailViewModel.class);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getProduct().observe(getViewLifecycleOwner(), product -> {
            if (product != null) {
                displayProduct(product);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.loadProduct(productId);
    }

    private void displayProduct(Product product) {
        tvProductTitle.setText(product.getTitle());
        tvProductPrice.setText(String.format("$%.2f", product.getPrice()));
        tvProductCategory.setText(product.getCategory());
        tvProductDescription.setText(product.getDescription());

        if (product.getRating() != null) {
            String ratingText = String.format("%.1f ⭐ (%d reviews)", 
                    product.getRating().getRate(), 
                    product.getRating().getCount());
            tvProductRating.setText(ratingText);
        }
    }
}
