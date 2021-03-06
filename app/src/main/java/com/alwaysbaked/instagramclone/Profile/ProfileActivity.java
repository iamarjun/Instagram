package com.alwaysbaked.instagramclone.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.alwaysbaked.instagramclone.Models.Photo;
import com.alwaysbaked.instagramclone.Models.User;
import com.alwaysbaked.instagramclone.R;
import com.alwaysbaked.instagramclone.Utils.ViewCommentsFragment;
import com.alwaysbaked.instagramclone.Utils.ViewPostFragment;
import com.alwaysbaked.instagramclone.Utils.ViewProfileFragment;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity implements
        ProfileFragment.OnGridImageSelectedListener,
        ViewProfileFragment.OnGridImageSelectedListener,
        ViewPostFragment.OnCommentThreadSelectedListener {
    private static final String TAG = "ProfileActivity";

    @Override
    public void onGridImageSelected(Photo photo, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image from profile gridview." + photo.toString());

        ViewPostFragment fragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putInt(getString(R.string.activity_number), activityNumber);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.viewpager_container, fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();
    }

    @Override
    public void OnCommentThreadSelectedListener(Photo photo) {
        Log.d(TAG, "OnCommentThreadSelectedListener: selected a comment thread");

        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.viewpager_container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();
    }

    private static final int ACTIVITY_NUMBER = 4;
    private static final int NUM_GRID_COLUMNS = 3;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: staring.");
        ButterKnife.bind(this);

        init();

    }

    private void init() {
        Log.d(TAG, "init: inflating " + getString(R.string.profile_fragment));

        Intent intent = getIntent();

        if (intent.hasExtra(getString(R.string.calling_activity))) {
            Log.d(TAG, "init: searching for user object attached as intent");

            if (intent.hasExtra(getString(R.string.intent_user))) {
                User user = intent.getParcelableExtra(getString(R.string.intent_user));
                if (!user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Log.d(TAG, "init: inflating view profile");
                    ViewProfileFragment fragment = new ViewProfileFragment();
                    Bundle args = new Bundle();
                    args.putParcelable(getString(R.string.intent_user), intent.getParcelableExtra(getString(R.string.intent_user)));
                    fragment.setArguments(args);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.viewpager_container, fragment);
                    transaction.addToBackStack(getString(R.string.view_profile_fragment));
                    transaction.commit();

                } else {
                    Log.d(TAG, "init: inflating profile");

                    ProfileFragment fragment = new ProfileFragment();
                    FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.viewpager_container, fragment);
                    transaction.addToBackStack(getString(R.string.profile_fragment));
                    transaction.commit();
                }

            } else {
                Toast.makeText(this, "Something went Wrongs", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d(TAG, "init: inflating profile");

            ProfileFragment fragment = new ProfileFragment();
            FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.viewpager_container, fragment);
            transaction.addToBackStack(getString(R.string.profile_fragment));
            transaction.commit();
        }
    }
}
