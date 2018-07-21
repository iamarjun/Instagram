package com.alwaysbaked.instagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alwaysbaked.instagramclone.R;
import com.alwaysbaked.instagramclone.Utils.BottomNavigationViewHelper;
import com.alwaysbaked.instagramclone.Utils.FirebaseMethods;
import com.alwaysbaked.instagramclone.Utils.SectionsStatePagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountSettingsActivity extends AppCompatActivity {
    private static final String TAG = "AccountSettingsActivity";
    private static final int ACTIVITY_NUMBER = 4;
    @BindView(R.id.bottomNavViewBar)
    BottomNavigationViewEx bottomNavigationViewEx;
    @BindView(R.id.container)
    ViewPager mViewPager;
    @BindView(R.id.relLayout1)
    RelativeLayout mRelativeLayout;
    @BindView(R.id.lvAccountSettings)
    ListView listView;
    @BindView(R.id.backArrow)
    ImageView backArrow;
    private Context mContext = AccountSettingsActivity.this;
    public SectionsStatePagerAdapter pagerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);
        Log.d(TAG, "onCreate: started");

        ButterKnife.bind(this);

        setupBottomNavigationView();
        setupSettingsList();
        setupFragments();
        getIncomingIntent();

        //setup mBackArrow for navigating back to "ProfileActivity"
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to \"ProfileActivity\".");
                finish();
            }
        });
    }

    private void getIncomingIntent() {
        Intent intent = getIntent();

        if (intent.hasExtra(getString(R.string.select_image)) || (intent.hasExtra(getString(R.string.select_bitmap)))) {

            //if there is an imageURl attached as an extra, then it was chosen from the galley/photo fragment.

            Log.d(TAG, "getIncomingIntent: new incoming imageURl");
            if (intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.edit_profile_fragment))) {

                if (intent.hasExtra(getString(R.string.select_image))) {
                    //set the new profile picture from gallery fragment
                    FirebaseMethods mFirebaseMethods = new FirebaseMethods(mContext);
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.profile_photo),
                            null,
                            0,
                            intent.getStringExtra(getString(R.string.select_image)),
                            null);

                } else if (intent.hasExtra(getString(R.string.select_bitmap))) {
                    //set the new profile picture from photo fragment
                    FirebaseMethods mFirebaseMethods = new FirebaseMethods(mContext);
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.profile_photo),
                            null,
                            0,
                            null,
                            (Bitmap) intent.getParcelableExtra(getString(R.string.select_bitmap)));

                }

            }
        }



        if (intent.hasExtra(getString(R.string.calling_activity))) {
            Log.d(TAG, "getIncomingIntent: received incoming intent from " + getString(R.string.profile_activity));
            setViewPager(pagerAdapter.getFragmentNumber(getString(R.string.edit_profile_fragment)));
        }
    }

    private void setupFragments() {
        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile_fragment)); //fragment #0
        pagerAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out_fragment));  //fragment #1

    }

    public void setViewPager(int fragmentNumber) {
        mRelativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: navigating to fragment #" + fragmentNumber);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(fragmentNumber);
    }

    private void setupSettingsList() {
        Log.d(TAG, "setupSettingsList: initializing account settings list.");
        ArrayList<String> option = new ArrayList<>();
        option.add(getString(R.string.edit_profile_fragment));//fragment #0
        option.add(getString(R.string.sign_out_fragment));//fragment #1

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, option);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: navigating to fragment #" + position);
                setViewPager(position);
            }
        });
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);

    }

}
