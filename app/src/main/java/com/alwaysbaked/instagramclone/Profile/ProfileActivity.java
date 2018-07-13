package com.alwaysbaked.instagramclone.Profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.alwaysbaked.instagramclone.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int ACTIVITY_NUMBER = 4;
    private static final int NUM_GRID_COLUMNS = 3;
    @BindView(R.id.bottomNavViewBar)
    BottomNavigationViewEx bottomNavigationViewEx;
    @BindView(R.id.profileToolBar)
    Toolbar toolbar;
    @BindView(R.id.profileMenu)
    ImageView profileMenu;
    @BindView(R.id.profile_photo)
    ImageView profilePhoto;
    @BindView(R.id.gridView)
    GridView mGridView;
    @BindView(R.id.profileProgressBar)
    ProgressBar mProgressBar;
    private Context mContext = ProfileActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: staring.");
        ButterKnife.bind(this);


        /*setupBottomNavigationView();
        setupToolbar();
        setupActivityWidgets();
        setProfilePhoto();

        tempGridSetup();*/
    }

    private void init(){
        Log.d(TAG, "init: inflating " + getString(R.string.profile_fragment));

        ProfileFragment fragment = new ProfileFragment();
        FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.profile_fragment));
        transaction.commit();
    }

   /* private void tempGridSetup() {
        List<String> imgURLs = new ArrayList<>();
        imgURLs.add("https://images4.alphacoders.com/678/thumb-1920-678317.jpg");
        imgURLs.add("https://images6.alphacoders.com/418/thumb-1920-418342.jpg");
        imgURLs.add("https://images7.alphacoders.com/593/thumb-1920-593278.jpg");
        imgURLs.add("https://images7.alphacoders.com/403/thumb-1920-403509.jpg");
        imgURLs.add("https://images.alphacoders.com/465/thumb-1920-465254.jpg");
        imgURLs.add("https://images6.alphacoders.com/642/thumb-1920-642268.jpg");
        imgURLs.add("https://images3.alphacoders.com/719/thumb-1920-719051.jpg");
        imgURLs.add("https://images2.alphacoders.com/659/thumb-1920-659623.png");
        imgURLs.add("https://images5.alphacoders.com/653/thumb-1920-653698.jpg");
        imgURLs.add("https://images5.alphacoders.com/474/thumb-1920-474474.jpg");
        imgURLs.add("https://images5.alphacoders.com/611/thumb-1920-611136.png");
        imgURLs.add("https://images8.alphacoders.com/659/thumb-1920-659626.png");
        imgURLs.add("https://images4.alphacoders.com/742/thumb-1920-742220.png");
        imgURLs.add("https://images8.alphacoders.com/761/thumb-1920-761063.png");
        imgURLs.add("https://images7.alphacoders.com/867/thumb-1920-867450.png");
        imgURLs.add("https://images8.alphacoders.com/864/thumb-1920-864900.png");
        imgURLs.add("https://images6.alphacoders.com/803/thumb-1920-803643.png");
        imgURLs.add("https://images5.alphacoders.com/810/thumb-1920-810680.png");
        imgURLs.add("https://images3.alphacoders.com/886/thumb-1920-886032.png");
        imgURLs.add("https://images8.alphacoders.com/851/thumb-1920-851512.png");
        imgURLs.add("https://images3.alphacoders.com/712/thumb-1920-712467.jpg");

        setupImageGrid(imgURLs);
    }

    private void setupImageGrid(List<String> imgURLs) {
        GridImageAdapter adapter = new GridImageAdapter(mContext,
                R.layout.layout_grid_imageview,
                "",
                imgURLs);
        mGridView.setAdapter(adapter);

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        mGridView.setColumnWidth(imageWidth);

    }

    private void setProfilePhoto() {
        Log.d(TAG, "setProfilePhoto: setting profile photo.");
        String imgURL = "https://cnet2.cbsistatic.com/img/3JQUEv_h8xcJ8QEcVNteWVADsew=/936x527/2017/08/21/ae78abff-be85-45e7-bae1-242ca5609f2c/androidoreolockup.jpg";
        UniversalImageLoader.setImage(imgURL, profilePhoto, mProgressBar, "");
    }

    private void setupActivityWidgets() {
        mProgressBar.setVisibility(View.GONE);
    }

    *//**
     * responsible for setting up profile toolbar
     *//*

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings");
                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    *//**
     * BottomNavigationView setup
     *//*
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);

    }
*/
}
