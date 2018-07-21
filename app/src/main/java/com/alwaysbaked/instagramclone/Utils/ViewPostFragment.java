package com.alwaysbaked.instagramclone.Utils;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysbaked.instagramclone.Models.Comment;
import com.alwaysbaked.instagramclone.Models.Like;
import com.alwaysbaked.instagramclone.Models.Photo;
import com.alwaysbaked.instagramclone.Models.User;
import com.alwaysbaked.instagramclone.Models.UserAccountSettings;
import com.alwaysbaked.instagramclone.R;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPostFragment extends Fragment {
    private static final String TAG = "ViewPostFragment";

    public interface OnCommentThreadSelectedListener {
        void OnCommentThreadSelectedListener(Photo photo);
    }

    private OnCommentThreadSelectedListener mOnCommentThreadSelectedListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mOnCommentThreadSelectedListener = (OnCommentThreadSelectedListener) getActivity();

        } catch (ClassCastException e) {
            Log.d(TAG, "onAttach: ClassCastException" + e.getMessage());
        }
    }

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
    private boolean mLikedByCurrentUser;
    private StringBuilder mUsers;
    private String mLikesString;
    private Context mContext;
    private User mCurrentUser;

    //widgets
    @BindView(R.id.post_image)
    SquareImageView mPostImage;

    @BindView(R.id.bottomNavViewBar)
    BottomNavigationViewEx bottomNavigationViewEx;

    @BindView(R.id.profile_photo)
    CircleImageView mProfilePhoto;

    @BindView(R.id.tvUsername)
    TextView mUsername;
    @BindView(R.id.tvLikes)
    TextView mLikes;
    @BindView(R.id.tvCaption)
    TextView mCaption;
    @BindView(R.id.tvComments)
    TextView mComments;
    @BindView(R.id.tvTimeStamp)
    TextView mTimeStamp;

    @BindView(R.id.backArrow)
    ImageView mBack;
    @BindView(R.id.dotMenu)
    ImageView mDotMenu;
    @BindView(R.id.heart_white)
    ImageView mHeartWhite;
    @BindView(R.id.heart_red)
    ImageView mHeartRed;
    @BindView(R.id.commentBubble)
    ImageView mCommentBubble;
    @BindView(R.id.send)
    ImageView mSend;
    @BindView(R.id.collection)
    ImageView mCollection;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        Log.d(TAG, "onCreateView: starting");
        mContext = getActivity();
        mGestureDetector = new GestureDetector(mContext, new GestureListener());

        ButterKnife.bind(this, view);

        mHeart = new Heart(mHeartWhite, mHeartRed);

        setupFirebaseAuth();
        setupBottomNavigationView();

        return view;
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        try {
            //mPhoto = getPhotoFromBundle();

            UniversalImageLoader.setImage(getPhotoFromBundle().getImage_path(), mPostImage, null, "");
            mActivityNumber = getActivityNumberFromBundle();

            String photoID = getPhotoFromBundle().getPhoto_id();

            Query query = FirebaseDatabase.getInstance().getReference()
                    .child(mContext.getString(R.string.dbname_photos))
                    .orderByChild(mContext.getString(R.string.field_photo_id))
                    .equalTo(photoID);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        Photo photo = new Photo();
                        Map<String, Object> objectMap = (HashMap<String, Object>) snap.getValue();

                        photo.setCaption(objectMap.get(mContext.getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(mContext.getString(R.string.field_tags)).toString());
                        photo.setDate_created(objectMap.get(mContext.getString(R.string.field_date_created)).toString());
                        photo.setPhoto_id(objectMap.get(mContext.getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(mContext.getString(R.string.field_user_id)).toString());
                        photo.setImage_path(objectMap.get(mContext.getString(R.string.field_image_path)).toString());

                        List<Comment> commentList = new ArrayList<>();

                        for (DataSnapshot dSnap : snap.child(mContext.getString(R.string.field_comments)).getChildren()) {
                            Comment comment = new Comment();
                            comment.setUser_id(dSnap.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnap.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnap.getValue(Comment.class).getDate_created());

                            commentList.add(comment);
                        }
                        photo.setComments(commentList);
                        mPhoto = photo;

                        getCurrentUser();
                        getPhotoDetails();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: query cancelled");

                }
            });

        } catch (NullPointerException e) {
            Log.d(TAG, "onCreateView: NullPointerException: " + e.getMessage());
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (isAdded())
            init();
    }

    private void getLikesString() {
        Log.d(TAG, "getLikesString: getting mLikes string");

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        Query query = mRef
                .child(mContext.getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(mContext.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers = new StringBuilder();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                    Query query = mRef
                            .child(mContext.getString(R.string.dbname_users))
                            .orderByChild(mContext.getString(R.string.field_user_id))
                            .equalTo(snap.getValue(Like.class).getUser_id());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: found mLike: " + snap.getValue(User.class).getUsername());

                                mUsers.append(snap.getValue(User.class).getUsername());
                                mUsers.append(", ");
                            }

                            String[] splitUsers = mUsers.toString().split(", ");

                            mLikedByCurrentUser = mUsers.toString().contains(mCurrentUser.getUsername() + ", ");

                            int likes = splitUsers.length;

                            if (likes == 1) {
                                mLikesString = "Liked by " + splitUsers[0];

                            } else if (likes == 2) {
                                mLikesString = "Liked by " + splitUsers[0] + " and " + splitUsers[1];

                            } else if (likes == 3) {
                                mLikesString = "Liked by " + splitUsers[0] + ", " + splitUsers[1] + " and " + splitUsers[2];

                            } else if (likes == 4) {
                                mLikesString = "Liked by " + splitUsers[0] + ", " + splitUsers[1] + ", " + splitUsers[2] + " and " + splitUsers[3];

                            } else if (likes > 4) {
                                mLikesString = "Liked by " + splitUsers[0] + ", " + splitUsers[1] + ", " + splitUsers[2] + " and " + (splitUsers.length - 3) + " other";
                            }

                            setupWidgets();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                if (!dataSnapshot.exists()) {
                    mLikesString = "";
                    mLikedByCurrentUser = false;
                    setupWidgets();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getCurrentUser() {
        Log.d(TAG, "getCurrentUser: getting the current mUser");

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        Query query = mRef
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    mCurrentUser = snap.getValue(User.class);
                }
                getLikesString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled");

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

            final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
            Query query = mRef
                    .child(getString(R.string.dbname_photos))
                    .child(mPhoto.getPhoto_id())
                    .child(getString(R.string.field_likes));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        String keyID = snap.getKey();
                        //case#1 The mUser already liked the photo
                        if (mLikedByCurrentUser &&
                                snap.getValue(Like.class).getUser_id()
                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                            mRef.child(getString(R.string.dbname_photos))
                                    .child(mPhoto.getPhoto_id())
                                    .child(getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();

                            mRef.child(getString(R.string.dbname_user_photos))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(mPhoto.getPhoto_id())
                                    .child(getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();

                            mHeart.toggleLike();
                            getLikesString();
                        }

                        //case#2 The mUser has not liked the photo
                        else if (!mLikedByCurrentUser) {
                            //add new mLike
                            addNewLike();
                            break;
                        }
                    }

                    if (!dataSnapshot.exists()) {
                        //add new mLike
                        addNewLike();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return true;
        }

    }

    private void addNewLike() {
        Log.d(TAG, "addNewLike: add a new mLike.");

        String newLikeID = mRef.push().getKey();
        Like like = new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mRef.child(getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mRef.child(getString(R.string.dbname_user_photos))
                .child(mPhoto.getUser_id())
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mHeart.toggleLike();
        getLikesString();
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
                //setupWidgets();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled");

            }
        });

    }

    private void setupWidgets() {
        String timeStampDifference = getTimeStampDifference();
        if (timeStampDifference.equals("0")) {
            mTimeStamp.setText("TODAY");

        } else if(timeStampDifference.equals("1")) {
            mTimeStamp.setText(timeStampDifference + " DAY AGO");

        } else {
            mTimeStamp.setText(timeStampDifference + " DAYS AGO");
        }

        Log.d(TAG, "setupWidgets: mUsername: " + mUserAccountSettings.getUsername());
        UniversalImageLoader.setImage(mUserAccountSettings.getProfile_photo(), mProfilePhoto, null, "");
        mUsername.setText(mUserAccountSettings.getUsername());
        mLikes.setText(mLikesString);
        mCaption.setText(mPhoto.getCaption());

        if (mPhoto.getComments().size() > 0) {
            mComments.setText("View All " + mPhoto.getComments().size() + " Comments...");
        }else {
            mComments.setText("");
        }

        mComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to comments thread.");

                mOnCommentThreadSelectedListener.OnCommentThreadSelectedListener(mPhoto);
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mCommentBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to comments thread");
                mOnCommentThreadSelectedListener.OnCommentThreadSelectedListener(mPhoto);
            }
        });



        if (mLikedByCurrentUser) {
            mHeartWhite.setVisibility(View.GONE);
            mHeartRed.setVisibility(View.VISIBLE);
            mHeartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: red mHeart touch detected");
                    return mGestureDetector.onTouchEvent(event);
                }
            });

        } else {
            mHeartWhite.setVisibility(View.VISIBLE);
            mHeartRed.setVisibility(View.GONE);
            mHeartWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: white mHeart touch detected");
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }


    }

    /**
     * returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimeStampDifference() {
        Log.d(TAG, "getTimeStampDifference: gettting mTimeStamp difference");

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
        // Check if mUser is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }
}
