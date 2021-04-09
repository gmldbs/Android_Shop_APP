package edu.skku.map.personalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    FragmentInterface fragmentInterface;
    private Fragment fragment;
    private FrameLayout frameLayout;
    private Button categoryAllBtn,
            categoryClothesBtn,
            categoryShoesBtn,
            categoryOthersBtn;
    private Button[] tabButtons;
    private DrawerLayout drawer;
    private TextView productsQuantity;
    private RelativeLayout productQuantityShape;
    private LinearLayout drawerListLayout;
    private LinearLayout logoutLayout;
    private ImageView expandArrow;
    private SearchView searchView;
    private double scrollY;
    private RelativeLayout favoritesCounterShape;
    private TextView favoritesCounter;
    private RelativeLayout cartCounterShape;
    private TextView cartCounter;

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.burgerMenuButton:

                    //if (!searchView.isIconified())
                      //  searchView.setIconified(true);
                    if (!drawer.isDrawerOpen(Gravity.LEFT)) {
                        drawer.openDrawer(Gravity.LEFT);
                    }
                    break;
                case R.id.cart_layout:

                    Intent goCart = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(goCart);
                    break;
                case R.id.all_pager_btnId:
                    changeColors(categoryAllBtn.getId());
                    replace(BaseProductsFragment.ALL_PRODUCTS);
                    break;

                case R.id.clothes_pager_btnId:
                    changeColors(categoryClothesBtn.getId());
                    replace(BaseProductsFragment.CLOTHES);
                    break;
                case R.id.shoes_pager_btnId:
                    changeColors(categoryShoesBtn.getId());
                    replace(BaseProductsFragment.SHOES);
                    break;
                case R.id.other_pager_btnId:
                    changeColors(categoryOthersBtn.getId());
                    replace(BaseProductsFragment.OTHER);
                    break;
                case R.id.navigation_favorites:
                    break;
                case R.id.bottom_filters_button:
                    Intent goAddProduct = new Intent(MainActivity.this, AddProductActivity.class);
                    startActivity(goAddProduct);
                    break;
            }
        }
    };
    public double getScrollY() {
        return scrollY;
    }

    public void setScrollY(double scrollY) {
        this.scrollY = scrollY;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initTabButtons();
        initDrawer();
        initActivityViews();
    }
    private void initToolbar() {
        Button burger = findViewById(R.id.burgerMenuButton);
        burger.setOnClickListener(clickListener);
        final SearchView searchView = findViewById(R.id.drawer_activity_search_view);
        searchView.setOnClickListener(clickListener);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) searchView.setIconified(true);
            }
        });
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_edit_frame);
        //searchEditText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.black));
        //searchEditText.setHintTextColor(ContextCompat.getColor(MainActivity.this,R.color.background_e8_grey));

        FrameLayout goToCartButton = findViewById(R.id.cart_layout);
        goToCartButton.setOnClickListener(clickListener);
    }

    private void initTabButtons() {
        categoryAllBtn = (Button) findViewById(R.id.all_pager_btnId);
        categoryClothesBtn = (Button) findViewById(R.id.clothes_pager_btnId);
        categoryShoesBtn = (Button) findViewById(R.id.shoes_pager_btnId);
        categoryOthersBtn = (Button) findViewById(R.id.other_pager_btnId);
        tabButtons = new Button[]{categoryAllBtn, categoryClothesBtn, categoryOthersBtn, categoryShoesBtn};
        categoryAllBtn.setOnClickListener(clickListener);
        categoryAllBtn.setBackgroundResource(R.drawable.border);
        categoryAllBtn.setTextColor(ContextCompat.getColor(this, R.color.text_black));
        categoryClothesBtn.setOnClickListener(clickListener);
        categoryShoesBtn.setOnClickListener(clickListener);
        categoryOthersBtn.setOnClickListener(clickListener);
    }

    private void initDrawer() {
        drawer = findViewById(R.id.main_drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                favoritesCounterShape.setVisibility(ProductListsManager.getInstance().getFavoriteProducts().size() == 0? View.GONE : View.VISIBLE);
                favoritesCounter.setText(String.valueOf(ProductListsManager.getInstance().getFavoriteProducts().size()));
                cartCounterShape.setVisibility(ProductListsManager.getInstance().getCartItems().size() == 0? View.GONE : View.VISIBLE);
                cartCounter.setText(String.valueOf(ProductListsManager.getInstance().getCartItems().size()));
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        LinearLayout favorites = findViewById(R.id.navigation_favorites);
        favorites.setOnClickListener(clickListener);

        cartCounterShape = (RelativeLayout) findViewById(R.id.cart_quantity_shape);
        favoritesCounterShape = (RelativeLayout) findViewById(R.id.favorites_quantity_shape);
        favoritesCounterShape.setVisibility(ProductListsManager.getInstance().getFavoriteProducts().size() == 0? View.GONE : View.VISIBLE);
        favoritesCounter = (TextView) findViewById(R.id.favorites_drawer_text);
        favoritesCounter.setText(String.valueOf(ProductListsManager.getInstance().getFavoriteProducts().size()));
        LinearLayout cart = (LinearLayout) findViewById(R.id.navigation_cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openCart = new Intent(MainActivity.this, CartActivity.class);
                startActivity(openCart);
            }
        });
        cartCounterShape = (RelativeLayout) findViewById(R.id.cart_quantity_shape);
        cartCounterShape.setVisibility(ProductListsManager.getInstance().getFavoriteProducts().size() == 0? View.GONE : View.VISIBLE);
        cartCounter = (TextView) findViewById(R.id.drawer_cart_textview);
        cartCounter.setText(String.valueOf(ProductListsManager.getInstance().getCartItems().size()));
        TextView logout_text = findViewById(R.id.logout_text);
        TextView login = findViewById(R.id.login_text);

        final UserData currentUser = (UserData) getApplication();

        if(currentUser.getUser()) {
            logout_text.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
        }
        else {
            logout_text.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
        }

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(Gravity.LEFT);
                Intent openRegister = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(openRegister);
                Toast.makeText(MainActivity.this, "createAccount", Toast.LENGTH_SHORT).show();
            }
        });
        logout_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentUser.setnotUser();
                ProductListsManager.getInstance().resetCart();
                ProductListsManager.getInstance().resetwish();
                initDrawer();
                initActivityViews();
            }
        });
        TextView createAccount = (TextView) findViewById(R.id.navigation_create_account);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(Gravity.LEFT);
                Intent openRegister = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(openRegister);
                Toast.makeText(MainActivity.this, "createAccount", Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayout userLayout = (LinearLayout) findViewById(R.id.user_layout);
        userLayout.setVisibility(View.VISIBLE);
        TextView username = (TextView) findViewById(R.id.username);
        TextView userEmail = (TextView) findViewById(R.id.user_email);
        if(currentUser.getUser()) {
            username.setText(currentUser.getData().get("Name").toString());
            userEmail.setText(currentUser.getData().get("email").toString());
        }
        else {
            username.setText("Anonymous");
            userEmail.setText("");
        }
        drawerListLayout = (LinearLayout) findViewById(R.id.drawer_list_layout);
        logoutLayout = (LinearLayout) findViewById(R.id.logout_user_layout);
        expandArrow = (ImageView) findViewById(R.id.expand_icon);
        TextView secondUserEmail = (TextView) findViewById(R.id.user_email_with_logo);
        secondUserEmail.setText("Email second");
        TextView logout = (TextView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "logout", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initActivityViews() {
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        replace(BaseProductsFragment.ALL_PRODUCTS);
        ImageButton bottomFiltersButton = findViewById(R.id.bottom_filters_button);
        UserData currentUser = (UserData) getApplication();
        if(currentUser.getUser() && currentUser.getData().get("id").equals("admin")) bottomFiltersButton.setVisibility(View.VISIBLE);
        else bottomFiltersButton.setVisibility(View.GONE);
        bottomFiltersButton.setOnClickListener(clickListener);
    }

    public void changeColors(int buttonID) {
        for (Button tabButton : tabButtons) {
            if (tabButton.getId() == buttonID) {
                tabButton.setBackgroundResource(R.drawable.border);
                tabButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.text_black, null));
            } else {
                tabButton.setBackgroundResource(R.color.background_white);
                tabButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.text_medium_grey, null));
            }
        }
    }
    private void replace(int id) {
        Log.d("replace", "replace: id = "+id);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = BaseProductsFragment.newInstance(id);
        fragmentInterface = BaseProductsFragment.newInstance(id);
        fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initToolbar();
        initTabButtons();
        initDrawer();
        initActivityViews();

    }
}
