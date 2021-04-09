package edu.skku.map.personalproject;

public interface FragmentInterface {
    //  The interface will declare that the fragments will be constraint to do
    // some kind of action when they become visible to the user.
    void fragmentIsVisible();

    void onFilterButtonClicked();

    void onSearchViewOpened(String string);
    void onSearchViewClicked();
    void onSearchViewClosed();
    void onRefreshFragment();
}
