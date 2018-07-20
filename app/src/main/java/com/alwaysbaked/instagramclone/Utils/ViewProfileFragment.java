package com.alwaysbaked.instagramclone.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alwaysbaked.instagramclone.Models.Comment;
import com.alwaysbaked.instagramclone.Models.Like;
import com.alwaysbaked.instagramclone.Models.Photo;
import com.alwaysbaked.instagramclone.Models.UserAccountSettings;
import com.alwaysbaked.instagramclone.Models.UserSettings;
import com.alwaysbaked.instagramclone.Profile.AccountSettingsActivity;
import com.alwaysbaked.instagramclone.Profile.ProfileActivity;
import com.alwaysbaked.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileFragment extends Fragment {
    private static final String TAG = "ViewProfileFragment";

    public interface OnGridImageSelectedListener {
        void onGridImageSelected(Photo photo, int activityNumber);
    }

    private OnGridImageSelectedListener mOnGridImageSelectedListener;

    @Override
    public void onAttach(Context context) {
        try {
            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
        } catch (ClassCastException e) {
            Log.d(TAG, "onAttach: ClassCastException" + e.getMessage());
        }
        super.onAttach(context);
    }

    private static final int ACTIVITY_NUMBER = 4;
    private static final int GRID_COLUMN = 3;

    private Context mContext;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseStorage storage;
    private FirebaseMethods mFirebaseMethods;


    //widgets
    @BindView(R.id.tvUsername)
    TextView mUsername;
    @BindView(R.id.tvPosts)
    TextView mPosts;
    @BindView(R.id.tvFollowers)
    TextView mFollowers;
    @BindView(R.id.tvFollowing)
    TextView mFollowing;
    @BindView(R.id.tvDislplayName)
    TextView mDisplayName;
    @BindView(R.id.tvDescription)
    TextView mDescription;
    @BindView(R.id.tvWebsite)
    TextView mWebsite;

    @BindView(R.id.tvEditProfile)
    TextView mEditProfile;


    @BindView(R.id.profileProgressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.profile_photo)
    CircleImageView mProfilePhoto;

    @BindView(R.id.gridView)
    GridView mGridView;

    @BindView(R.id.profileToolBar)
    Toolbar toolbar;

    @BindView(R.id.profileMenu)
    ImageView profileMenu;

    @BindView(R.id.bottomNavViewBar)
    BottomNavigationViewEx bottomNavigationViewEx;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Log.d(TAG, "onCreateView: started");
        mContext = getActivity();
        mFirebaseMethods = new FirebaseMethods(mContext);
        storage = FirebaseStorage.getInstance();

        ButterKnife.bind(this, view);

        setupToolbar();
        setupBottomNavigationView();
        setupFrebaseAuth();
        setupGridView();

        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to " + mContext.getString(R.string.edit_profile_fragment));
                Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        return view;
    }

    private void setupGridView() {
        Log.d(TAG, "setupGridView: setting up image grid.");

        final ArrayList<Photo> photos = new ArrayList<>();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

        Query query = mRef
                .child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) snap.getValue();

                    photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                    photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                    photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                    photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                    photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                    photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                    List<Comment> comments = new ArrayList<>();

                    for (DataSnapshot dSnap : snap.child(getString(R.string.field_comments)).getChildren()) {
                        Comment comment = new Comment();
                        comment.setUser_id(dSnap.getValue(Comment.class).getUser_id());
                        comment.setComment(dSnap.getValue(Comment.class).getComment());
                        comment.setDate_created(dSnap.getValue(Comment.class).getDate_created());

                        comments.add(comment);
                    }
                    photo.setComments(comments);



                    List<Like> likesList = new ArrayList<>();

                    for (DataSnapshot dSnap : snap.child(getString(R.string.field_likes)).getChildren()) {
                        Like like = new Like();
                        like.setUser_id(dSnap.getValue(Like.class).getUser_id());

                        likesList.add(like);
                    }

                    photo.setLikes(likesList);
                    photos.add(photo);
                }

                //setup image grid
                int grisWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = grisWidth / GRID_COLUMN;

                mGridView.setColumnWidth(imageWidth);

                //extracting imgURLs
                ArrayList<String> imgURLs = new ArrayList<>();
                for (int i = 0; i < photos.size(); i++) {
                    Log.d(TAG, "onDataChange: image #" + i + " url: " + photos.get(i).getImage_path());
                    imgURLs.add(photos.get(i).getImage_path());

                }

                GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, "", imgURLs);
                mGridView.setAdapter(adapter);

                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mOnGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY_NUMBER);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled");

            }
        });
    }

    private void setProfileWidget(UserSettings userSettings) {
        Log.d(TAG, "setProfileWidget: settings widgets with data retrieved from firebase: " + userSettings.toString());

        //User user = userSettings().getUser();
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mPosts.setText(String.valueOf(settings.getPosts()));
        mFollowers.setText(String.valueOf(settings.getFollowers()));
        mFollowing.setText(String.valueOf(settings.getFollowing()));

        mProgressBar.setVisibility(View.GONE);

    }

    /**
     * responsible for setting up profile toolbar
     */

    private void setupToolbar() {
        ((ProfileActivity) getActivity()).setSupportActionBar(toolbar);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings");
                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, getActivity(), bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }


    /*
    ------------------------------------------ Firebase --------------------------------------------
     */

    /**
     * setup firebase auth object.
     */

    private void setupFrebaseAuth() {
        Log.d(TAG, "setupFrebaseAuth: setting up firebase auth.");

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

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: settings user's data");

                //retrieve user information from database.
                setProfileWidget(mFirebaseMethods.getUserSettings(dataSnapshot));


                //retrieve images for the user in question.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
