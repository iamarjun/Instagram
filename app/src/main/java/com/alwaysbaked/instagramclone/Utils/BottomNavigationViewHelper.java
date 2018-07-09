package com.alwaysbaked.instagramclone.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.alwaysbaked.instagramclone.HomeActivity;
import com.alwaysbaked.instagramclone.LikesActivity;
import com.alwaysbaked.instagramclone.ProfileActivity;
import com.alwaysbaked.instagramclone.R;
import com.alwaysbaked.instagramclone.SearchActivity;
import com.alwaysbaked.instagramclone.ShareActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {
    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomnavigationView");

        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx viewEx){
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.ic_home:
                        Intent intent1 = new Intent(context, HomeActivity.class);//ACTIVITY_NUMBER = 0
                        context.startActivity(intent1);
                        break;

                    case R.id.ic_search:
                        Intent intent2 = new Intent(context, SearchActivity.class);//ACTIVITY_NUMBER = 1
                        context.startActivity(intent2);
                        break;

                    case R.id.ic_share:
                        Intent intent3 = new Intent(context, ShareActivity.class);//ACTIVITY_NUMBER = 2
                        context.startActivity(intent3);
                        break;

                    case R.id.ic_like:
                        Intent intent4 = new Intent(context, LikesActivity.class);//ACTIVITY_NUMBER = 3
                        context.startActivity(intent4);
                        break;

                    case R.id.ic_profile:
                        Intent intent5 = new Intent(context, ProfileActivity.class);//ACTIVITY_NUMBER = 4
                        context.startActivity(intent5);
                        break;
                }
                return false;
            }
        });
    }
}
