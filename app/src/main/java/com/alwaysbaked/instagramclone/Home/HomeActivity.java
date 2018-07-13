package com.alwaysbaked.instagramclone.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.alwaysbaked.instagramclone.Login.LoginActivity;
import com.alwaysbaked.instagramclone.R;
import com.alwaysbaked.instagramclone.Utils.BottomNavigationViewHelper;
import com.alwaysbaked.instagramclone.Utils.SectionsPagerAdapter;
import com.alwaysbaked.instagramclone.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUMBER = 0;
    @BindView(R.id.bottomNavViewBar)
    BottomNavigationViewEx bottomNavigationViewEx;
    @BindView(R.id.container)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    private Context mContext = HomeActivity.this;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting.");

        setupFrebaseAuth();

        ButterKnife.bind(this);

        initImageLoader();

        setupBottomNavigationView();
        setupViewPager();

        //mAuth.signOut();
    }

    private void initImageLoader() {
        UniversalImageLoader imageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(imageLoader.getConfig());
    }

    /**
     * Responsible for adding tabs: Camera, Home, Messages
     */
    public void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment());// index 0
        adapter.addFragment(new HomeFragment());// index 1
        adapter.addFragment(new MessagesFragment());// index 2
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_instagram_black_48dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_send);
    }

    /**
     * BottomNavigationView setup
     */
    public void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);

    }


    /*
    ------------------------------------------ Firebase --------------------------------------------
     */

    /**
     * @param user checked to see if user is logged in.
     */

    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");
        if (user == null) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * setup firebase auth object.
     */

    private void setupFrebaseAuth() {
        Log.d(TAG, "setupFrebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // check if the user is logged in.
                checkCurrentUser(user);
                if (user != null)
                    Log.d(TAG, "onAuthStateChanged: signed in:" + user.getUid());
                else
                    Log.d(TAG, "onAuthStateChanged: signed out");
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthStateListener);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }


}
