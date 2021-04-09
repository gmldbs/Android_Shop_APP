package edu.skku.map.personalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class AllProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private Context context;
    private ArrayList<Product> products;
    private ArrayList<Product> filteredProducts;
    private Fragment fragment;
    private StorageReference mStorage=FirebaseStorage.getInstance().getReference();

    public AllProductsAdapter(Context context, ArrayList<Product> allProducts, Fragment fragment) {
        this.context = context;
        this.products = allProducts;
        this.filteredProducts = products;;
        this.fragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.widget_layout_products_gridview, parent, false);
        return new ProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ProductsViewHolder productsHolder = ((ProductsViewHolder) holder);
        final Product product = filteredProducts.get(position);
        if (product != null) {
            productsHolder.favorite.setImageResource(ProductListsManager.getInstance().wishlistContains(product.getProductId()) ?
                    R.drawable.ic_favorite_red : R.drawable.ic_favorite_grey);
            productsHolder.cart.setImageResource(ProductListsManager.getInstance().cartContains(product.getProductId()) ?
                    R.drawable.ic_shopping_cart_green : R.drawable.ic_shopping_cart_grey);
            productsHolder.mainProductName.setText(product.getProductName());
            productsHolder.mainProducePrice.setText(context.getResources().getString(R.string.dollar_currency_string) + "" + product.getProductPrice());
            if(product.getProductGender().equals("M")) productsHolder.mainProductGender.setText("Man");
            else if(product.getProductGender().equals("W")) productsHolder.mainProductGender.setText("Woman");
            else productsHolder.mainProductGender.setText("Man and Woman");
            StorageReference ProductImage = mStorage.child(String.valueOf(product.getProductId()));
            final Context context = this.context;
            ProductImage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Glide.with(context).load(task.getResult()).into(productsHolder.mainProductImage);
                    }
                    else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            productsHolder.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ProductListsManager.getInstance().wishlistContains(product.getProductId())) {
                        productsHolder.favorite.setImageResource(R.drawable.ic_favorite_grey);
                        ProductListsManager.getInstance().removeWishlistItemById(product.getProductId());
                    } else {
                        productsHolder.favorite.setImageResource(R.drawable.ic_favorite_red);
                        ProductListsManager.getInstance().addItemWishlist(product.getProductId());
                    }

                }
            });

            productsHolder.cart.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent openProductDetailScreen = new Intent(context, ProductDetailsActivity.class);
                    openProductDetailScreen.putExtra("item", product.getProductId());
                    context.startActivity(openProductDetailScreen);
                }
            });

            productsHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openProductDetailScreen = new Intent(context, ProductDetailsActivity.class);
                    openProductDetailScreen.putExtra("item", product.getProductId());
                    context.startActivity(openProductDetailScreen);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return filteredProducts.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    filteredProducts = products;
                } else {

                    ArrayList<Product> filteredList = new ArrayList<>();

                    for (Product product : products) {

                        if (product.getProductName().toLowerCase().contains(charString)) {

                            filteredList.add(product);
                        }
                    }

                    filteredProducts = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredProducts;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredProducts = (ArrayList<Product>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void filterListByGender(ArrayList<Product> filteredProducts) {
        this.filteredProducts = new ArrayList<>();
        this.filteredProducts = filteredProducts;
        notifyDataSetChanged();
    }

    public void sortList(String type) {
        switch (type) {
            case Utils.NEWEST:
                break;
            case Utils.PRICE_DOWN:
                if(filteredProducts==null) break;
                Collections.sort(filteredProducts, new Comparator<Product>() {
                    public int compare(Product p1, Product p2) {
                        if(p1.getProductPrice()>p2.getProductPrice()) return 1;
                        else return -1;
                    }
                });
                notifyDataSetChanged();
                break;
            case Utils.PRICE_UP:
                Collections.sort(filteredProducts, new Comparator<Product>() {
                    public int compare(Product p1, Product p2) {
                        if(p2.getProductPrice()>p1.getProductPrice()) return 1;
                        else return -1;
                    }
                });
                notifyDataSetChanged();
                break;
        }
    }

    private class ProductsViewHolder extends RecyclerView.ViewHolder {
        LinearLayout parentLayout;
        TextView mainProductName;
        ImageView mainProductImage;
        TextView mainProducePrice;
        TextView mainProductGender;
        ImageView cart;
        ImageView favorite;

        ProductsViewHolder(View convertView) {
            super(convertView);
            this.parentLayout = (LinearLayout) convertView.findViewById(R.id.grid_view_items);
            this.mainProductName = (TextView) convertView.findViewById(R.id.productName);
            this.mainProductImage = (ImageView) convertView.findViewById(R.id.productImage);
            this.mainProducePrice = (TextView) convertView.findViewById(R.id.productPrice);
            this.mainProductGender = convertView.findViewById(R.id.productGender);
            this.cart = (ImageView) convertView.findViewById(R.id.cart);
            this.favorite = (ImageView) convertView.findViewById(R.id.favorite);
        }
    }

}
