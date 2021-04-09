package edu.skku.map.personalproject;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductDetailsActivity extends YouTubeBaseActivity implements View.OnClickListener {
    //region GLOBALS

    private static final String TAG = "ProductDetailsActivity";
    /* Product Views */
    private ImageView itemImageView;
    private TextView itemDetailsTextView;
    private TextView itemPriceTextView;
    private TextView itemNumberPicker;
    private ImageButton decreaseQuantity;
    private ImageButton increaseQuantity;
    /* LinearLayouts to display product size buttons and color buttons  */
    private LinearLayout itemSizesLinearLayout;
    private LinearLayout itemColorsLinearLayout;
    private Button playbtn;
    /*  Toolbar Views */
    private ImageView addToFavoritesButton;
    private TextView cartProductsQuantity;
    private RelativeLayout productsCartShape;
    //  Data source <object> product </object>

    private Product product;
    //  Selected size and selected quantity
    private int selectedProductId;
    private String selectedProductSize="";
    private int selectedProductQuantity=0;
    private int maxQuantityToChoose;
    //  List with added specificProducts to cart
    private ArrayList<Product> specificProducts;
    private String previous;
    private boolean isSize=false;
    private int num=0;
    private StorageReference mStorage= FirebaseStorage.getInstance().getReference();
    private YouTubePlayer.OnInitializedListener listener;
    //  OnClick on toolbar
    private View.OnClickListener toolbarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.toolbar_back_button:
                    onBackPressed();
                    break;
                case R.id.favouritesProductDetailsButton:
                    /*if (User.getInstance().isAnon()) {
                        Utils.showSingleButtonAlert(ProductDetailsActivity.this, getResources().getString(R.string.user_not_logged_in), getResources().getString(R.string.connect_to_see_favorites));
                    } else {
                        if (ProductListsManager.getInstance().wishlistContains(product.getProductId())) {
                            ProductListsManager.getInstance().removeWishlistItemById(product.getProductId());
                            addToFavoritesButton.setBackgroundResource(R.drawable.ic_favorite_grey);
                        } else {
                            ProductListsManager.getInstance().addItemWishlist(product.getProductId());
                            addToFavoritesButton.setBackgroundResource(R.drawable.ic_favorite_red);
                        }
//                        manager.updateCart(ProductDetailsActivity.this);
                        manager.updateWishlist(ProductDetailsActivity.this);
                    }
                    Toast.makeText(ProductDetailsActivity.this, "favoritesProductDetailButton", Toast.LENGTH_SHORT).show();

                    break;
                case R.id.cart_layout:
                    Toast.makeText(ProductDetailsActivity.this, "cart_layout", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(ProductDetailsActivity.this, CartActivity.class));
                    break;*/
            }
        }
    };

    private ArrayList<Button> sizeButtonsList;
    private ArrayList<Button> colorButtonsList;

    //  Reference list
    /*private ArrayList<String> sizesList = new ArrayList<String>() {{
        add("XS");
        add("S");
        add("M");
        add("L");
        add("XL");
        add("XXL");
    }};*/

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_details);

        int productId = getIntent().getIntExtra("item", -1);
        previous = getIntent().getStringExtra("previous") == null ? "" : getIntent().getStringExtra("previous");
        product = ProductListsManager.getInstance().getProductById(productId);
        initToolbar();
        initUI();
        createActivity(product);
    }

    //  Initialisation of toolbar buttons
    private void initToolbar() {
        Button backButton = (Button) findViewById(R.id.toolbar_back_button);
        addToFavoritesButton = (ImageView) findViewById(R.id.favouritesProductDetailsButton);
        FrameLayout goToCartButton = (FrameLayout) findViewById(R.id.cart_layout);

        backButton.setOnClickListener(toolbarListener);
        addToFavoritesButton.setOnClickListener(toolbarListener);
        goToCartButton.setOnClickListener(toolbarListener);

        productsCartShape = (RelativeLayout) findViewById(R.id.products_quantity_shape);
        productsCartShape.setVisibility(ProductListsManager.getInstance().getCartItems().size() != 0 ? View.VISIBLE : View.GONE);
        cartProductsQuantity = (TextView) findViewById(R.id.products_inside_cart_textview);
        cartProductsQuantity.setText(String.valueOf(ProductListsManager.getInstance().getCartItems().size()));
    }

    //  Method to declare all views
    private void initUI() {
        itemImageView = (ImageView) findViewById(R.id.item_image);
        itemColorsLinearLayout = (LinearLayout) findViewById(R.id.itemColorsLinearLayoutID);
        itemDetailsTextView = (TextView) findViewById(R.id.itemDetailsTextViewID);
        itemNumberPicker = (TextView) findViewById(R.id.itemNumberPickerID);
        itemSizesLinearLayout = (LinearLayout) findViewById(R.id.item_sizes_layout);
        itemPriceTextView = (TextView) findViewById(R.id.item_price);
        StorageReference ProductImage = mStorage.child(String.valueOf(product.getProductId()));
        ProductImage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {
                    Glide.with(ProductDetailsActivity.this).load(task.getResult()).into(itemImageView);
                }
                else {
                    Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button addToCartButton = (Button) findViewById(R.id.add_to_cart);
        addToCartButton.setOnClickListener(this);


        increaseQuantity = (ImageButton) findViewById(R.id.increaseNumberPickerID);
        increaseQuantity.setOnClickListener(this);
        decreaseQuantity = (ImageButton) findViewById(R.id.decrease_item_quantity);
        decreaseQuantity.setOnClickListener(this);
        manageQuantityButtons();
        final String playid = product.getVideoID();
        final YouTubePlayerView youTubePlayerView = findViewById(R.id.youtubeView);
        youTubePlayerView.initialize("AIzaSyD31s_pAXIu4Ygt4ZiVdK2yBiV-a5Uyc5Q", new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
                if(!b) {
                    youTubePlayer.cueVideo(playid);
                }
                youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {

                    }

                    @Override
                    public void onLoaded(String s) {
                        youTubePlayer.play();
                    }

                    @Override
                    public void onAdStarted() {

                    }

                    @Override
                    public void onVideoStarted() {

                    }

                    @Override
                    public void onVideoEnded() {

                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {

                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });

    }

    public void createActivity(final Product product) {
        this.product = product;
        itemDetailsTextView.setText(product.getProductName());
        itemPriceTextView.setText(getResources().getString(R.string.dollar_currency_string) + "" + product.getProductPrice());
        addItemsToSpecificProducts(product);
        createColorButtonsList(specificProducts, product.getProductColor());
        createSizeButtonsList(product);
        resetFavoriteButton(product);
    }

    private void resetFavoriteButton(Product product) {
        if (ProductListsManager.getInstance().wishlistContains(product.getProductId())) {
            addToFavoritesButton.setBackgroundResource(R.drawable.ic_favorite_red);
        } else {
            addToFavoritesButton.setBackgroundResource(R.drawable.ic_favorite_grey);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_to_cart:
                if(selectedProductSize=="") {
                    Toast.makeText(this, "select product size first!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if(selectedProductQuantity == 0) {
                        Toast.makeText(this, "select product quantity more than 1!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ProductListsManager.getInstance().addItemCart(product,selectedProductSize, selectedProductQuantity);
                    Toast.makeText(this, "Add Cart Success!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.increaseNumberPickerID:
                if(selectedProductSize=="")
                {
                    Toast.makeText(this, "Please select a size first!", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectedProductQuantity++;
                itemNumberPicker.setText(String.valueOf(selectedProductQuantity));
                break;

            case R.id.decrease_item_quantity:
                if(selectedProductSize=="")
                {
                    Toast.makeText(this, "Please select a size first!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedProductQuantity > 0) {
                    selectedProductQuantity = selectedProductQuantity - 1;
                    itemNumberPicker.setText(String.valueOf(selectedProductQuantity));
                }
                break;
        }
    }


    private void selectSize(int idParam, HashMap<String, String> sizes) {
        for (Button b : sizeButtonsList) {
            if (idParam == b.getId()) {
                b.setBackgroundResource(R.drawable.roundedbuttonclicked);
                b.setTextColor(Color.WHITE);
                selectedProductSize = (String) b.getText();
                TextView remain = findViewById(R.id.remain);
                TextView remain_text = findViewById(R.id.remain_text);
                remain.setText(product.getSizes().get(selectedProductSize));
                remain.setVisibility(View.VISIBLE);
                remain_text.setVisibility(View.VISIBLE);
                isSize=true;
                Toast.makeText(this, "selected Product Size"+selectedProductSize, Toast.LENGTH_SHORT).show();

            } else {
                b.setBackgroundResource(R.drawable.roundedbutton);
                b.setTextColor(Color.BLACK);
            }
        }
    }

    private void createSizeButtonsList(final Product product) {
        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

        sizeButtonsList = new ArrayList<>();
        ArrayList<String> forSort = new ArrayList<>();

        int id = 0;
        for(final String key: product.getSizes().keySet())
        {
            Log.d(TAG, "createSizeButtonsList: key : "+ key + "quantity : " + product.getSizes().get(key));
            final Button sizeButton = new Button(this);
            sizeButton.setId(id);
            sizeButton.setLayoutParams(new LinearLayout.LayoutParams(width,height));
            setMargins(sizeButton,20,20,20,20);
            sizeButton.setBackgroundResource(R.drawable.roundedbutton);

            sizeButton.setTextColor(Color.BLACK);
            sizeButton.setText(key);

            sizeButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    selectedProductQuantity=0;
                    itemNumberPicker.setText(String.valueOf(selectedProductQuantity));
                    selectSize(view.getId(), product.getSizes());
                    manageQuantityButtons();
                    if (selectedProductSize.equals("")) {
                        Utils.showSingleButtonAlertWithoutTitle(ProductDetailsActivity.this, getResources().getString(R.string.stock_consumed_error_msg));
                        sizeButton.setBackgroundResource(R.drawable.roundedbutton);
                        sizeButton.setTextColor(Color.BLACK);
                    }
                }
            });
            sizeButtonsList.add(sizeButton);
            id++;
        }
        display(product);
    }

    private void manageQuantityButtons() {
        increaseQuantity.setImageDrawable(ContextCompat.getDrawable(ProductDetailsActivity.this,
                selectedProductSize == null ? R.drawable.ic_add_green_disabled : R.drawable.ic_add_green));
        increaseQuantity.setEnabled(selectedProductSize != null);
        increaseQuantity.setClickable(selectedProductSize != null);

        decreaseQuantity.setImageDrawable(ContextCompat.getDrawable(ProductDetailsActivity.this,
                selectedProductSize == null ? R.drawable.ic_remove_green_disabled : R.drawable.ic_remove_green));
        decreaseQuantity.setEnabled(selectedProductSize != null);
        decreaseQuantity.setClickable(selectedProductSize != null);
    }

    private void createColorButtonsList(ArrayList<Product> products, String productColor) {
        colorButtonsList = new ArrayList<>();
        for (final Product product : specificProducts) {
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics());
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics());

            Button colorButton = new Button(this);
            colorButton.setLayoutParams(new LinearLayout.LayoutParams(width, height));
            setMargins(colorButton, 8, 8, 8, 8);

            if (product.getProductColor().contentEquals(productColor)) {
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(Color.parseColor(product.getProductColor()));
                gd.setCornerRadius(100);
                gd.setStroke(4, Color.BLACK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    colorButton.setBackground(gd);
                } else {
//                        colorButton.setBackgroundDrawable(gd);
                    colorButton.setBackground(gd);
                }
            } else {
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(Color.parseColor(product.getProductColor()));
                gd.setCornerRadius(100);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    colorButton.setBackground(gd);
                } else {
                    colorButton.setBackground(gd);
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                colorButton.setElevation(2);
            }

            colorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clear();
                    selectedProductQuantity = 0;
                    selectedProductSize = null;
                    manageQuantityButtons();
                    itemNumberPicker.setText(String.valueOf(selectedProductQuantity));
                    createActivity(product);
                }
            });

            colorButtonsList.add(colorButton);
            itemColorsLinearLayout.addView(colorButton);
        }
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    private void clear() {
        colorButtonsList = new ArrayList<>();
        itemColorsLinearLayout.removeAllViewsInLayout();
        itemSizesLinearLayout.removeAllViewsInLayout();
        this.selectedProductSize = null;
    }

    private void addItemsToSpecificProducts(Product product) {
        specificProducts = new ArrayList<>();
        for (Product product1 : ProductListsManager.getInstance().getProducts()) {
            if (product1.getProductId()==product.getProductId()) {
                specificProducts.add(product1);
            }
        }
    }
    private void display(Product product) {
        for(String key: product.getSizes().keySet())
        {
            for(Button sizeButton : sizeButtonsList) {
                if(key.contentEquals(sizeButton.getText())) itemSizesLinearLayout.addView(sizeButton);
            }
        }
    }
    private void display(ArrayList<String> sortedList) {
        for (String size : sortedList) {
            for (Button sizeButton : sizeButtonsList) {
                if (size.contentEquals(sizeButton.getText())) {
                    itemSizesLinearLayout.addView(sizeButton);
                }
            }
        }
    }

    private void check(Product product) {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
