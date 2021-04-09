/*
 * Copyright © 2018-present, MNK Group. All rights reserved.
 */
package edu.skku.map.personalproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CartAdapter extends ArrayAdapter<ProductListsManager.CartItem> {


    private ArrayList<ProductListsManager.CartItem> productList = new ArrayList<>();
    private Activity parentActivity;
    private StorageReference mStorage= FirebaseStorage.getInstance().getReference();

    public CartAdapter(Activity context, int textViewResourceId, ArrayList<ProductListsManager.CartItem> objects) {
        super(context, textViewResourceId, objects);
        this.productList = objects;
        this.parentActivity = context;
    }

    @Override
    public void remove(@Nullable ProductListsManager.CartItem object) {
        super.remove(object);
        notifyDataSetChanged();
        ProductListsManager.getInstance().removeCartItem(object);
        ((CartActivity) parentActivity).updateTotalPrice();
    }

    @Override
    public int getCount() {
        return this.productList.size();
    }
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(parentActivity).inflate(R.layout.cart_item, parent, false);

            holder.mainProductName = (TextView) convertView.findViewById(R.id.cartNameProduct);
            holder.mainProductImage = (ImageView) convertView.findViewById(R.id.cartImageProduct);
            holder.mainProductPrice = (TextView) convertView.findViewById(R.id.cartPriceProduct);
            holder.mainProductQuantity = (TextView) convertView.findViewById(R.id.cartQuantityProduct);
            holder.mainProductColor = (ImageView) convertView.findViewById(R.id.cartColorProduct);
            holder.mainProductSize = (TextView) convertView.findViewById(R.id.cartSizeProduct);
            holder.mainProductBin = (ImageView) convertView.findViewById(R.id.cartBinImageProduct);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final ProductListsManager.CartItem cartItem = productList.get(position);

        if (cartItem != null) {
            holder.mainProductName.setText(cartItem.getName());
            holder.mainProductPrice.setText(parentActivity.getResources().getString(R.string.dollar_currency_string) + "" + cartItem.getPrice());
            holder.mainProductQuantity.setText(cartItem.getQuantity() + " " + (cartItem.getQuantity() > 1 ? "pcs" : "pc"));
            holder.mainProductColor.setBackgroundColor(Color.parseColor(cartItem.getColor()));
            StorageReference ProductImage = mStorage.child(String.valueOf(cartItem.getId()));
            ProductImage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Glide.with(getContext()).load(task.getResult()).into(holder.mainProductImage);
                    }
                    else {
                        Toast.makeText(parentActivity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // Item Color shape
            GradientDrawable gd1 = new GradientDrawable();
            gd1.setColor(Color.parseColor(cartItem.getColor()));
            gd1.setCornerRadius(100);
            gd1.setStroke(2, Color.WHITE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.mainProductColor.setBackground(gd1);
            }

            // Item Size shape
            GradientDrawable gd2 = new GradientDrawable();
            gd2.setColor(Color.BLACK);
            gd2.setCornerRadius(100);
            gd2.setStroke(2, Color.BLACK);
            holder.mainProductSize.setText(cartItem.getSize());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.mainProductSize.setBackground(gd2);
            }

            holder.mainProductBin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductListsManager.getInstance().removeCartItem(cartItem);
                    if(mOnMyItemCheckedChanged != null) mOnMyItemCheckedChanged.onItemCheckedChanged(cartItem, true);
                    notifyDataSetChanged();
                }
            });
        }
        return convertView;
    }

    public void updateSource() {
        this.productList = ProductListsManager.getInstance().getCartItems();
        notifyDataSetChanged();
    }

    private class Holder {
        TextView mainProductName;
        ImageView mainProductImage;
        TextView mainProductPrice;
        TextView mainProductQuantity;
        ImageView mainProductColor;
        TextView mainProductSize;
        ImageView mainProductBin;
    }

    public interface OnMyItemCheckedChanged {
        public void onItemCheckedChanged(ProductListsManager.CartItem item, boolean isChecked);
    }
    private OnMyItemCheckedChanged mOnMyItemCheckedChanged;

    public void setOnMyItemCheckedChanged(OnMyItemCheckedChanged  onMyItemCheckedChanged){
        this.mOnMyItemCheckedChanged = onMyItemCheckedChanged;
    }
}
