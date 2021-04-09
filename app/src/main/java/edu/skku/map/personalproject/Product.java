/*
 * Copyright © 2018-present, MNK Group. All rights reserved.
 */

package edu.skku.map.personalproject;


import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Product {

    private int productId;
    private String productName;     //  Base name of the product instance
    private String productColor;    //  Base color of the product instance
    private String productGender;   //  Scope of the product instance
    private int productPrice;    //  Base price of the product instance
    private String productImage;       //  Image resource used by the product instance
    private HashMap<String, String> sizes;
    private String productAddedDate;
    private String category;
    private String videoID;


    public ProductSize getProductSize() {
        return productSize;
    }

    public void setProductSize(ProductSize productSize) {
        this.productSize = productSize;
    }

    private ProductSize productSize;

    public Product(String productName, int productId, String productColor, String productImage, int productPrice,
                   HashMap<String, String> sizes, String productCategory, String productAddedDate, String productGender, String videoID) {
        this.productName = productName;
        this.productId = productId;
        this.productColor = productColor;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.sizes = sizes;
        this.category = productCategory;
        this.productAddedDate = productAddedDate;
        this.productGender = productGender;
        this.videoID = videoID;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public String getProductColor() {
        return productColor;
    }

    public String getProductAddedDate() {
        return productAddedDate;
    }

    public String getProductGender() {
        return productGender;
    }

    public String getCategory() {
        return category;
    }

    public HashMap<String, String> getSizes() {
        return sizes;
    }

    public String getVideoID() { return videoID;}

}