package com.alwaysbaked.instagramclone;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysbaked.instagramclone.Models.Photo;
import com.alwaysbaked.instagramclone.Utils.BottomNavigationViewHelper;
import com.alwaysbaked.instagramclone.Utils.SquareImageView;
import com.alwaysbaked.instagramclone.Utils.UniversalImageLoader;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPostFragment extends Fragment {
    private static final String TAG = "ViewPostFragment";

    public ViewPostFragment() {
        super();
        setArguments(new Bundle());
    }

    //variables
    private Photo mPhoto;
    private int mActivityNumber = 0;

    //widgets
    @BindView(R.id.post_image)
    SquareImageView mPostImage;

    @BindView(R.id.bottomNavViewBar)
    BottomNavigationViewEx bottomNavigationViewEx;

    @BindView(R.id.profile_photo)
    CircleImageView mProfilePhoto;

    @BindView(R.id.tvUsername)
    TextView mUsername;
    @BindView(R.id.image_likes)
    TextView mImageLikes;
    @BindView(R.id.image_caption)
    TextView mImageCaption;
    @BindView(R.id.image_comments)
    TextView mImageComments;
    @BindView(R.id.image_timeStamp)
    TextView mImageTimeStamp;

    @BindView(R.id.dotMenu)
    ImageView mDotMenu;
    @BindView(R.id.heart)
    ImageView mHeart;
    @BindView(R.id.heart_red)
    ImageView mHeartRed;
    @BindView(R.id.comments)
    ImageView mComments;
    @BindView(R.id.send)
    ImageView mSend;
    @BindView(R.id.collection)
    ImageView mCollection;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        Log.d(TAG, "onCreateView: starting");

        ButterKnife.bind(this, view);

        try {
            mPhoto = getPhotoFromBundle();
            UniversalImageLoader.setImage(mPhoto.getImage_path(), mPostImage, null, "");
            mActivityNumber = getActivityNumberFromBundle();
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreateView: NullPointerException: " + e.getMessage());
        }

        setupBottomNavigationView();
        setupWidgets();

        return view;
    }

    private void setupWidgets() {
        String timeStampDifference = getTimeStampDifference();
        if (!timeStampDifference.equals("0")) {
            mImageTimeStamp.setText(timeStampDifference + " DAYS AGO");
        } else {
            mImageTimeStamp.setText("TODAY");
        }
    }

    /**
     * returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimeStampDifference() {
        Log.d(TAG, "getTimeStampDifference: gettting timestamp difference");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("America/NewYork"));
        Date today = c.getTime();
        sdf.format(today);
        Date timeStamp;
        final String photoTimeStamp = mPhoto.getDate_created();
        try {
            timeStamp  = sdf.parse(photoTimeStamp);
            difference = String.valueOf(Math.round(((today.getTime() - timeStamp.getTime()) / 1000 / 60 / 60 / 24)));
        } catch (ParseException e) {
            Log.d(TAG, "getTimeStampDifference: ParseException: " + e.getMessage());
            difference = "0";
        }
        return difference;
    }

    /**
     * retrieving photo from the incoming bundle from 'ProfileActivity' interface
     * @return
     */
    private Photo getPhotoFromBundle() {
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getString(R.string.photo));
        } else
            return null;
    }

    /**
     * retrieving activity number from the incoming bundle from 'ProfileActivity' interface
     * @return
     */
    private int getActivityNumberFromBundle() {
        Log.d(TAG, "getActivityNumberFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getInt(getString(R.string.activity_number));
        } else
            return 0;
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(getContext(), getActivity(), bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(mActivityNumber);
        menuItem.setChecked(true);
    }
}
