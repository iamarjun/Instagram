package com.alwaysbaked.instagramclone.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.alwaysbaked.instagramclone.Login.LoginActivity;
import com.alwaysbaked.instagramclone.Models.Photo;
import com.alwaysbaked.instagramclone.Models.UserAccountSettings;
import com.alwaysbaked.instagramclone.R;
import com.alwaysbaked.instagramclone.Utils.BottomNavigationViewHelper;
import com.alwaysbaked.instagramclone.Utils.SectionsPagerAdapter;
import com.alwaysbaked.instagramclone.Utils.UniversalImageLoader;
import com.alwaysbaked.instagramclone.Utils.ViewCommentsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUMBER = 0;

    //widgets
    @BindView(R.id.bottomNavViewBar)
    BottomNavigationViewEx bottomNavigationViewEx;
    @BindView(R.id.container)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;

    //variables
    private Context mContext = HomeActivity.this;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting.");

        setupFirebaseAuth();

        ButterKnife.bind(this);

        initImageLoader();

        setupBottomNavigationView();
        setupViewPager();

        //mAuth.signOut();
    }

    public void onCommentThreadSelected(Photo photo, UserAccountSettings settings) {
        Log.d(TAG, "onCommentThreadSelected: selected a comment thread");

        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.bundle_photo), photo);
        args.putParcelable(getString(R.string.bundle_user_account_settings), settings);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();
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
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera).setCustomView(R.layout.layout_tab_custom);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_instagram).setCustomView(R.layout.layout_tab_custom);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_send).setCustomView(R.layout.layout_tab_custom);
    }

    /**
     * BottomNavigationView setup
     */
    public void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
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

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

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
