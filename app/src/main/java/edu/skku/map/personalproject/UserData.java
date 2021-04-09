package edu.skku.map.personalproject;

import android.app.Application;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserData extends Application {
    private Map<String, Object> data = new HashMap<>();
    private Map<String, Object> cart = new HashMap<>();
    boolean isUser = false;

    public Map<String, Object> getData()
    {
        return data;
    }
    public void setData(Map data)
    {
        this.data = data;
    }
    public void setCart() {cart = new HashMap<>();}
    public void addCart(String productId, int num) {this.cart.put(productId,num);}
    public Map<String, Object> getCart() {return this.cart;}
    public void deleteItemCart(int productId) {}
    public void setUser() {this.isUser = true; }
    public void setnotUser() {
        this.isUser = false;
        this.data = new HashMap<>();
        this.cart = new HashMap<>();
    }
    public boolean getUser() {return this.isUser;}
}
