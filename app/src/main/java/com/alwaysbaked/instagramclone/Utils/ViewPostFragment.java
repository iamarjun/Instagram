package com.alwaysbaked.instagramclone.Utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysbaked.instagramclone.Models.Photo;
import com.alwaysbaked.instagramclone.Models.UserAccountSettings;
import com.alwaysbaked.instagramclone.R;
import com.alwaysbaked.instagramclone.Utils.BottomNavigationViewHelper;
import com.alwaysbaked.instagramclone.Utils.FirebaseMethods;
import com.alwaysbaked.instagramclone.Utils.GridImageAdapter;
import com.alwaysbaked.instagramclone.Utils.SquareImageView;
import com.alwaysbaked.instagramclone.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private String username = "";
    private String profleURL = "";
    private UserAccountSettings mUserAccountSettings;
    private GestureDetector mGestureDetector;
    private Heart mHeart;

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
    @BindView(R.id.heart_white)
    ImageView mHeartWhite;
    @BindView(R.id.heart_red)
    ImageView mHeartRed;
    @BindView(R.id.comments)
    ImageView mComments;
    @BindView(R.id.send)
    ImageView mSend;
    @BindView(R.id.collection)
    ImageView mCollection;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        Log.d(TAG, "onCreateView: starting");
        mGestureDetector = new GestureDetector(getContext(), new GestureListener());

        ButterKnife.bind(this, view);

        mHeart = new Heart(mHeartWhite, mHeartRed);

        mHeartRed.setVisibility(View.GONE);
        mHeartWhite.setVisibility(View.VISIBLE);

        try {
            mPhoto = getPhotoFromBundle();
            UniversalImageLoader.setImage(mPhoto.getImage_path(), mPostImage, null, "");
            mActivityNumber = getActivityNumberFromBundle();
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreateView: NullPointerException: " + e.getMessage());
        }

        setupFirebaseAuth();
        setupBottomNavigationView();
        getPhotoDetails();
        testToggle();


        return view;
    }

    private void testToggle() {
        mHeartRed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch: red heart touch detected");
                return mGestureDetector.onTouchEvent(event);
            }
        });

        mHeartWhite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch: white heart touch detected");
                return mGestureDetector.onTouchEvent(event);
            }
        });

    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: double tap detected");
            mHeart.toggleLike();
            return true;
        }

    }

    private void getPhotoDetails() {
        Log.d(TAG, "getPhotoDetails: retrieving details from 'photo' node in firebase database");

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

        Query query = mRef
                .child(getString(R.string.dbname_users_account_settings))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(mPhoto.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    mUserAccountSettings = snap.getValue(UserAccountSettings.class);
                }
                setupWidgets();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled");

            }
        });

    }

    private void setupWidgets() {
        String timeStampDifference = getTimeStampDifference();
        if (!timeStampDifference.equals("0")) {
            mImageTimeStamp.setText(timeStampDifference + " DAYS AGO");
        } else {
            mImageTimeStamp.setText("TODAY");
        }

        Log.d(TAG, "setupWidgets: username: " + mUserAccountSettings.getUsername());
        UniversalImageLoader.setImage(mUserAccountSettings.getProfile_photo(), mProfilePhoto, null, "");
        mUsername.setText(mUserAccountSettings.getUsername());
    }

    /**
     * returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimeStampDifference() {
        Log.d(TAG, "getTimeStampDifference: gettting timestamp difference");

        String difference;
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


     /*
    ------------------------------------------ Firebase --------------------------------------------
     */

    /**
     * setup firebase auth object.
     */

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

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
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }
}
