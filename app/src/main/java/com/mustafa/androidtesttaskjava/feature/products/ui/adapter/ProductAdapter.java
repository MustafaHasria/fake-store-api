package com.mustafa.androidtesttaskjava.feature.products.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mustafa.androidtesttaskjava.R;
import com.mustafa.androidtesttaskjava.feature.products.data.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for products list.
 * Uses ViewHolder pattern for optimal performance.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products = new ArrayList<>();
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvProductTitle;
        private final TextView tvProductPrice;
        private final TextView tvProductCategory;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductTitle = itemView.findViewById(R.id.tv_product_title);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductCategory = itemView.findViewById(R.id.tv_product_category);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onProductClick(products.get(position));
                }
            });
        }

        void bind(Product product) {
            tvProductTitle.setText(product.getTitle());
            tvProductPrice.setText(String.format("$%.2f", product.getPrice()));
            tvProductCategory.setText(product.getCategory());
        }
    }
}
