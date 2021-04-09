
package edu.skku.map.personalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static edu.skku.map.personalproject.Utils.formatPrice;


public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    //region GLOBALS
    private static final String TAG = "CartActivity";
    public static ArrayList<ProductListsManager.CartItem> cartProductList = new ArrayList<ProductListsManager.CartItem>();
    private TextView cartTotalPrice;
    private Button backButton;
    private ListView cartSimpleListProduct;
    private Button cartCheckout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        init();
        setupUI();
    }

    private void init() {
        backButton = (Button) findViewById(R.id.cart_toolbar_back_button);
        cartSimpleListProduct = (ListView) findViewById(R.id.cart_ListView);
        cartTotalPrice = (TextView) findViewById(R.id.cart_total_price);
        cartCheckout = (Button) findViewById(R.id.checkout);
    }

    private void setupUI() {
        backButton.setOnClickListener(this);

        cartProductList = ProductListsManager.getInstance().getCartItems();
        CartAdapter cartAdapter = new CartAdapter(this, R.layout.cart_item, cartProductList);
        cartAdapter.setOnMyItemCheckedChanged(new CartAdapter.OnMyItemCheckedChanged() {
            @Override
            public void onItemCheckedChanged(ProductListsManager.CartItem item, boolean isChecked) {
                total();
                updateTotalPrice();
            }
        });
        cartSimpleListProduct.setAdapter(cartAdapter);

        cartTotalPrice.setText(getResources().getString(R.string.dollar_currency_string) + "" + formatPrice(total()));
        cartCheckout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cart_toolbar_back_button:
                onBackPressed();
                break;
            case R.id.checkout:
                if (cartProductList.size() == 0) {
                    Utils.showSingleButtonAlertWithoutTitle(CartActivity.this, getResources().getString(R.string.alert_cart_is_empty));
                } else {
                    Toast.makeText(this, "checkout button", Toast.LENGTH_SHORT).show();
                    for(int i=0;i<cartProductList.size();i++) {
                        final int temp = i;
                        db.collection("Products").whereEqualTo("productId",String.valueOf(cartProductList.get(i).getId())).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for(QueryDocumentSnapshot document : task.getResult()) {
                                        HashMap<String, String> sizes = new HashMap<>();
                                        sizes= (HashMap<String, String>) document.getData().get("sizes");
                                        int origin = Integer.parseInt(sizes.get(cartProductList.get(temp).getSize()));
                                        origin-=cartProductList.get(temp).getQuantity();
                                        sizes.put(cartProductList.get(temp).getSize(),String.valueOf(origin));
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("sizes", sizes);
                                        db.collection("Products").document(task.getResult().getDocuments().get(0).getId()).set(data, SetOptions.merge());
                                    }
                                    if(temp == cartProductList.size()-1) {
                                        Toast.makeText(CartActivity.this, "Buy Success!", Toast.LENGTH_SHORT).show();
                                        ProductListsManager.getInstance().resetCart();
                                        Intent intent = new Intent(CartActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                    }
                }
                break;
        }
    }

    public double total() {
        cartProductList = ProductListsManager.getInstance().getCartItems();
        double total = 0;
        for (int i = 0; i < cartProductList.size(); i++) {
            total = cartProductList.get(i).getPrice() * cartProductList.get(i).getQuantity() + total;
        }
        return total;
    }

    public void updateTotalPrice() {
        cartTotalPrice.setText(getResources().getString(R.string.dollar_currency_string) + "" + formatPrice(total()));
    }
    @Override
    public void onBackPressed() {
        finish();
    }

}