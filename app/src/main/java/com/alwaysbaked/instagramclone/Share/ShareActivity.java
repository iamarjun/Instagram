package com.alwaysbaked.instagramclone.Share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.alwaysbaked.instagramclone.R;
import com.alwaysbaked.instagramclone.Utils.Permissions;
import com.alwaysbaked.instagramclone.Utils.SectionsPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = "ShareActivity";

    //constants
    private static final int ACTIVITY_NUMBER = 2;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    private Context mContext = ShareActivity.this;

    @BindView(R.id.viewpager_container)
    ViewPager mViewPager;
    @BindView(R.id.tabsBottom)
    TabLayout mTabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Log.d(TAG, "onCreate: staring.");
        ButterKnife.bind(this);

        if (checkPermissionsArray(Permissions.PERMISSIONS)) {
            setupViewPager();

        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }

    }

    /**
     * returns current tab number.
     * 0 - gallery
     * 1 - photo
     * @return
     */
    public int getCurrentTabNumber(){
        return mViewPager.getCurrentItem();
    }

    /**
     * setup viewpager for managing tabs
     */
    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new PhotoFragment());

        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setText(R.string.gallery);
        mTabLayout.getTabAt(1).setText(R.string.photo);
    }

    public int getTask() {
        Log.d(TAG, "getTask: TASK: " + getIntent().getFlags());
        return getIntent().getFlags();
    }

    /**
     * verify all the permission
     * @param permissions
     */
    private void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permission.");

        ActivityCompat.requestPermissions(ShareActivity.this, permissions, VERIFY_PERMISSIONS_REQUEST);
    }

    /**
     * checking for an array of permission.
     *
     * @param permissions
     * @return
     */
    private boolean checkPermissionsArray(String[] permissions) {
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for (String permission : permissions) {
            if (!checkPermissions(permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * checking for a single permission if granted or not.
     *
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission) {
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(mContext, permission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions: Permission Denied for: " + permission);
            return false;
        } else {
            Log.d(TAG, "checkPermissions: Permission Granted for: " + permission);
            return true;
        }
    }

}
