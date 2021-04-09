package edu.skku.map.personalproject;


import java.util.ArrayList;

import javax.annotation.Nullable;


public class ProductListsManager {

    private String TAG = "ProductListsManager";
    private static ProductListsManager mInstance = new ProductListsManager();

    private ArrayList<Product> mProducts = new ArrayList<Product>();
    private ArrayList<CartItem> mCartItems = new ArrayList<CartItem>();
    private double totalPrice;
    private ArrayList<Integer> mWishlistProductIds = new ArrayList<Integer>();

    public class CartItem {
        private Product product;
        private String size;
        private int quantity;
        private double price;

        public CartItem(Product product, String size, int quantity) {
            this.product = product;
            this.size = size;
            this.quantity = quantity;
            this.price = product.getProductPrice();
        }

        public void add(int quantity) {
            this.quantity += quantity;
        }

        //region GETTERS

        public int getId() {
            return this.product.getProductId();
        }

        public String getName() {
            return this.product.getProductName();
        }

        public String getColor() {
            return this.product.getProductColor();
        }

        public String getGender() {
            return this.product.getProductGender();
        }

        public String getImage() {
            return this.product.getProductImage();
        }

        public String getSize() {
            return size;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return this.price;
        }

    }

    private ProductListsManager() {
    }

    public synchronized static ProductListsManager getInstance() {
        if (mInstance == null) {
            mInstance = new ProductListsManager();
        }

        return mInstance;
    }

    public void setProducts(ArrayList<Product> products) {
        mProducts=products;
    }


    public synchronized void resetCart() {
        mCartItems = new ArrayList<CartItem>();
    }
    public synchronized void resetwish() { mWishlistProductIds = new ArrayList<>();}


    public synchronized void addItemCart(Product product, String size, int quantity) {
        CartItem newItem = new CartItem(product, size, quantity);
        mCartItems.add(newItem);
        }


    public synchronized void addItemWishlist(int id) {
        mWishlistProductIds.add(id);
    }


    public synchronized void removeCartItem(CartItem cartItem) {
        for (CartItem item : mCartItems) {
            if (cartItem == item) {
                mCartItems.remove(item);
                break;
            }
        }
    }

    public synchronized void removeWishlistItemById(int id) {
        for (Integer i : mWishlistProductIds) {
            if (i == id) {
                mWishlistProductIds.remove(i);
                break;
            }
        }
    }

    //endregion

    //region CONTENT TESTS

    public synchronized boolean wishlistContains(int id) {
        for (Integer i : mWishlistProductIds) {
            if (i == id) {
                return true;
            }
        }

        return false;
    }

    public synchronized boolean cartContains(int id) {
        for (CartItem item : mCartItems) {
            if (item.getId() == id) {
                return true;
            }
        }

        return false;
    }


    public ArrayList<Product> getProducts() {
        return mProducts;
    }

    public ArrayList<CartItem> getCartItems() {
        return mCartItems;
    }

    public ArrayList<Integer> getWishlistProductIds() {
        return mWishlistProductIds;
    }

    public ArrayList<Product> getFavoriteProducts() {
        ArrayList<Product> newList = new ArrayList<Product>();
        for (int i : mWishlistProductIds) {
            for (Product p : mProducts) {
                if (p.getProductId() == i) {
                    newList.add(p);
                    break;
                }
            }
        }

        return newList;
    }

    @Nullable
    public synchronized Product getProductById(int id) {
        for (Product p : mProducts) {
            if (p.getProductId() == id) {
                return p;
            }
        }

        return null;
    }

    public synchronized ArrayList<Product> getShoes() {
        ArrayList<Product> list = new ArrayList<Product>();
        for (Product p : mProducts) {
            if (p.getCategory().equalsIgnoreCase("Shoes")) {
                list.add(p);
            }
        }

        return list;
    }

    public synchronized ArrayList<Product> getOther() {
        ArrayList<Product> list = new ArrayList<Product>();
        for (Product p : mProducts) {
            if (p.getCategory().equalsIgnoreCase("Other") || p.getCategory().equalsIgnoreCase("Accessories")) {
                list.add(p);
            }
        }

        return list;
    }

    public synchronized ArrayList<Product> getFavClothes() {
        ArrayList<Product> list = new ArrayList<Product>();
        for (Product p : getFavoriteProducts()) {
            if (p.getCategory().equalsIgnoreCase("Clothes")) {
                list.add(p);
            }
        }

        return list;
    }

    public synchronized ArrayList<Product> getFavShoes() {
        ArrayList<Product> list = new ArrayList<Product>();
        for (Product p : getFavoriteProducts()) {
            if (p.getCategory().equalsIgnoreCase("Shoes")) {
                list.add(p);
            }
        }

        return list;
    }

    public synchronized ArrayList<Product> getFavOther() {
        ArrayList<Product> list = new ArrayList<Product>();
        for (Product p : getFavoriteProducts()) {
            if (p.getCategory().equalsIgnoreCase("Other") || p.getCategory().equalsIgnoreCase("Accessories")) {
                list.add(p);
            }
        }

        return list;
    }

    public void clearManager(){
        mCartItems.clear();
        mWishlistProductIds.clear();
    }

    public synchronized ArrayList<Product> getClothes() {
        ArrayList<Product> list = new ArrayList<Product>();
        for (Product p : mProducts) {
            if (p.getCategory().equalsIgnoreCase("Clothes")) {
                list.add(p);
            }
        }

        return list;
    }



    public synchronized void resetInstance() {
        mCartItems = new ArrayList<CartItem>();
        mWishlistProductIds = new ArrayList<>();
    }

    public String toString(int mode) {
        switch (mode) {
            case 1:
                return "wishlist: " + mWishlistProductIds.size();

            case 2:
                return "cart: " + mCartItems.size();

            default:
                return super.toString();
        }
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
