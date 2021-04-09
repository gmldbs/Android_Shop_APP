package edu.skku.map.personalproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Andreea Braesteanu on 8/17/2017.
 */

public class BaseProductsFragment extends Fragment implements FragmentInterface, View.OnClickListener {

    public static final String ARG_PAGE = "Page";
    public static final int ALL_PRODUCTS = 0;
    public static final int CLOTHES = 1;
    public static final int SHOES = 2;
    public static final int OTHER = 3;

    private int pageNr;
    private ArrayList<Product> products;
    private ArrayList<Product> clothes;
    private ArrayList<Product> shoes;
    private ArrayList<Product> other;
    private ArrayList<Product> all;

    private String firstFilter = Utils.FILTER_ALL;
    private String secondFilter = Utils.NEWEST;
    private int oldVerticalOffset = 0;

    private AllProductsAdapter productsAdapter;
    private RelativeLayout filtersLayout;
    private ImageButton bottomFilters;
    private NestedScrollView nestedScrollView;
    private Button menBtn, womenBtn, kidsBtn, allBtn,
            priceUpBtn, priceDownBtn, newestBtn, oldestBtn;
    private Button[] firstRow, secondRow;
    private View rootView;
    private RecyclerView productsRecyclerView;
    private ImageView promoImage;
    private SearchView searchView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public BaseProductsFragment() {
    }

    public static BaseProductsFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, id);
        BaseProductsFragment fragment = new BaseProductsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public int getPageNr() {
        return pageNr;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageNr = getArguments().getInt(ARG_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_base, container, false);
        Log.e("Page Number", String.valueOf(pageNr));
        init();
        return rootView;
    }
    private void init() {
        searchView = (SearchView) getActivity().findViewById(R.id.drawer_activity_search_view);
        filtersLayout = (RelativeLayout) rootView.findViewById(R.id.filters_layout);
        nestedScrollView = (NestedScrollView) rootView.findViewById(R.id.nested_scroll_view);
        productsRecyclerView = (RecyclerView) rootView.findViewById(R.id.all_products_recycler_view);
        bottomFilters = getActivity().findViewById(R.id.bottom_filters_button);
        initFilterButtons(filtersLayout);
        final Activity activity = getActivity();
        final Fragment fragment = this;
        all = new ArrayList<>();
        db.collection("Products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d("onComplete: ","here is get function");
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        String productId = (String) document.getData().get("productId");
                        String productName = (String) document.getData().get("productName");
                        String productColor = (String) document.getData().get("productColor");
                        String productGender =(String) document.getData().get("productGender");
                        String productPrice =(String) document.getData().get("productPrice");
                        String productImage =(String) document.getData().get("productImage");
                        String videoID = document.getData().get("videoID").toString();
                        HashMap<String, String> sizes = new HashMap<>();
                        sizes= (HashMap<String, String>) document.getData().get("sizes");
                        Log.d("onComplete: ", "size hash map : "+sizes);
                        document.getData().get("productName");
                        String productAddedDate =(String) document.getData().get("productaddedDate");
                        String category = (String) document.getData().get("category");
                        Product product = new Product(productName, Integer.parseInt(productId), productColor, productImage, Integer.parseInt(productPrice), sizes, category, productAddedDate, productGender, videoID);
                        all.add(product);
                    }
                    ProductListsManager.getInstance().setProducts(all);
                    productsAdapter = new AllProductsAdapter(activity, all, fragment);
                    productsRecyclerView.setAdapter(productsAdapter);
                    setupUI();
                }
            }
        });
    }

    private void setupUI() {
        searchView.onActionViewCollapsed();

        productsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        switch (pageNr) {
            case ALL_PRODUCTS:
                products = ProductListsManager.getInstance().getProducts();
                break;
            case CLOTHES:
                products = ProductListsManager.getInstance().getClothes();
                break;
            case SHOES:
                products = ProductListsManager.getInstance().getShoes();
                break;
            case OTHER:
                products = ProductListsManager.getInstance().getOther();
                break;
        }

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (pageNr == 0)
                    ((MainActivity) getActivity()).setScrollY(scrollY);
            }
        });
        showAll();
        productsAdapter.sortList(Utils.NEWEST);
        resetColorRow1(allBtn);
    }

    public void initFilterButtons(View view) {

        menBtn = (Button) view.findViewById(R.id.menBtnId);
        womenBtn = (Button) view.findViewById(R.id.womenBtnId);
        kidsBtn = (Button) view.findViewById(R.id.kidsBtnId);
        allBtn = (Button) view.findViewById(R.id.allBtnId);
        newestBtn = (Button) view.findViewById(R.id.newestBtnId);
        oldestBtn = (Button) view.findViewById(R.id.oldestBtnId);
        priceUpBtn = (Button) view.findViewById(R.id.priceUpBtnId);
        priceDownBtn = (Button) view.findViewById(R.id.priceDownBtnId);

        menBtn.setOnClickListener(this);
        womenBtn.setOnClickListener(this);
        kidsBtn.setOnClickListener(this);
        allBtn.setOnClickListener(this);
        newestBtn.setOnClickListener(this);
        oldestBtn.setOnClickListener(this);
        priceUpBtn.setOnClickListener(this);
        priceDownBtn.setOnClickListener(this);

        firstRow = new Button[]{menBtn, womenBtn, kidsBtn, allBtn};
        secondRow = new Button[]{priceUpBtn, priceDownBtn, newestBtn, oldestBtn};

        allBtn.setBackgroundResource(R.color.navigation_buttons_green);
        allBtn.setTextColor(ContextCompat.getColor(getActivity(), R.color.background_white));
        newestBtn.setBackgroundResource(R.color.navigation_buttons_green);
        newestBtn.setTextColor(ContextCompat.getColor(getActivity(), R.color.background_white));
    }

    @Override
    public void fragmentIsVisible() {
    }

    @Override
    public void onFilterButtonClicked() {
        filtersLayout.setVisibility(View.VISIBLE);
        nestedScrollView.smoothScrollTo(0, 0);

    }

    @Override
    public void onSearchViewOpened(String string) {
        if (productsAdapter != null)
            productsAdapter.getFilter().filter(string);
    }

    @Override
    public void onSearchViewClicked() {
        bottomFilters.setVisibility(View.GONE);
        filtersLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSearchViewClosed() {

    }
    @Override
    public void onRefreshFragment() {
    }

    public void isAppBarVisible(boolean isVisible) {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.allBtnId:
                firstFilter = Utils.FILTER_ALL;
                resetColorRow1(allBtn);
                showAll();
                break;
            case R.id.menBtnId:
                firstFilter = Utils.FILTER_MEN;
                resetColorRow1(menBtn);
                showMenClothes();
                break;
            case R.id.womenBtnId:
                firstFilter = Utils.FILTER_WOMEN;
                resetColorRow1(womenBtn);
                showWomenClothes();
                break;
            case R.id.kidsBtnId:
                firstFilter = Utils.FILTER_KIDS;
                resetColorRow1(kidsBtn);
                showKidsClothes();
                break;

            case R.id.newestBtnId:
                secondFilter = Utils.NEWEST;
                resetColorRow2(newestBtn);
                productsAdapter.sortList(Utils.NEWEST);
                break;
            case R.id.oldestBtnId:
                secondFilter = Utils.OLDEST;
                resetColorRow2(oldestBtn);
                productsAdapter.sortList(Utils.OLDEST);
                break;
            case R.id.priceUpBtnId:
                secondFilter = Utils.PRICE_UP;
                resetColorRow2(priceUpBtn);
                productsAdapter.sortList(Utils.PRICE_UP);
                break;
            case R.id.priceDownBtnId:
                secondFilter = Utils.PRICE_DOWN;
                resetColorRow2(priceDownBtn);
                productsAdapter.sortList(Utils.PRICE_DOWN);
                break;
        }
    }


    public void resetColorRow1(Button button) {
        for (Button aFirstRow : firstRow) {
            if (aFirstRow != button) {
                aFirstRow.setBackgroundResource(R.color.background_transparent);
            }
        }
        if(getActivity()==null) return;
        button.setBackgroundResource(R.color.navigation_buttons_green);
        button.setTextColor(ContextCompat.getColor(getActivity(), R.color.background_white));
    }

    public void resetColorRow2(Button button) {
        for (Button aSecondRow : secondRow) {
            if (aSecondRow != button) {
                aSecondRow.setBackgroundResource(R.color.background_transparent);
                if (aSecondRow.getId() == R.id.priceDownBtnId) {
                    aSecondRow.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_grey, 0);
                }
                if (aSecondRow.getId() == R.id.priceUpBtnId) {
                    aSecondRow.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_less_grey, 0);
                }
            }
        }
        if(getActivity()==null) {
            return;
        }
        Log.d("resetColorRow2: ","getactivy: "+getActivity()+" button : "+button);
        button.setBackgroundResource(R.color.navigation_buttons_green);
        button.setTextColor(ContextCompat.getColor(getActivity(), R.color.background_white));
        if (secondFilter.equals(Utils.PRICE_UP) || secondFilter.equals(Utils.PRICE_DOWN))
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, secondFilter.equals(Utils.PRICE_DOWN) ? R.drawable.ic_expand_more_white : R.drawable.ic_expand_less_white, 0);
    }

    public void showAll() {
        productsAdapter.filterListByGender(products);
        sort();

    }

    public void showWomenClothes() {
        productsAdapter.filterListByGender(Utils.getFilteredListByGender(Utils.FILTER_WOMEN, products));
        sort();

    }

    public void showMenClothes() {
        productsAdapter.filterListByGender(Utils.getFilteredListByGender(Utils.FILTER_MEN, products));
        sort();
    }

    public void showKidsClothes() {
        productsAdapter.filterListByGender(Utils.getFilteredListByGender(Utils.FILTER_KIDS, products));
        sort();
    }

    public void sort() {
        switch (secondFilter) {
            case Utils.NEWEST:
                newestBtn.performClick();
                break;
            case Utils.OLDEST:
                oldestBtn.performClick();
                break;
            case Utils.PRICE_UP:
                priceUpBtn.performClick();
                break;
            case Utils.PRICE_DOWN:
                priceDownBtn.performClick();
                break;
        }
    }

}
