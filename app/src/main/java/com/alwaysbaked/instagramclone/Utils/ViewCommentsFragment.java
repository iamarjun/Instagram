package com.alwaysbaked.instagramclone.Utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alwaysbaked.instagramclone.Home.HomeActivity;
import com.alwaysbaked.instagramclone.Models.Comment;
import com.alwaysbaked.instagramclone.Models.Photo;
import com.alwaysbaked.instagramclone.Models.UserAccountSettings;
import com.alwaysbaked.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ViewCommentsFragment extends Fragment {
    private static final String TAG = "ViewCommentsFragment";

    public ViewCommentsFragment() {
        super();
        setArguments(new Bundle());
    }

    //widgets
    @BindView(R.id.backArrow)
    ImageView mBack;
    @BindView(R.id.send)
    ImageView mSend;
    @BindView(R.id.profile_photo)
    CircleImageView mProfilePhoto;

    @BindView(R.id.comment)
    EditText mComment;

    @BindView(R.id.tvPost)
    TextView mPost;

    @BindView(R.id.lvComments)
    ListView mCommentListView;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseStorage storage;


    //variables
    private Context mContext;
    private Photo mPhoto;
    private List<Comment> mComments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_comments, container, false);
        Log.d(TAG, "onCreateView: starting");
        mContext = getActivity();
        mComments = new ArrayList<>();

        ButterKnife.bind(this, view);

        try {
            mPhoto = getPhotoFromBundle();

        } catch (NullPointerException e) {
            Log.d(TAG, "onCreateView: NullPointerException: " + e.getMessage());
        }

        setupFirebaseAuth();

        return view;
    }


    private void setupWidgets() {
        CommentListAdapter adapter = new CommentListAdapter(mContext, R.layout.layout_comment, mComments);
        mCommentListView.setAdapter(adapter);

        //set the profile photo
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        Query query = mRef
                .child(mContext.getString(R.string.dbname_users_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(snap.getValue(UserAccountSettings.class).getProfile_photo(), mProfilePhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled");

            }
        });

        //set the comment post button
        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mComment.getText().toString().equals("")) {
                    Log.d(TAG, "onClick: attempting to submit a new comment");
                    addNewComment(mComment.getText().toString());

                    mComment.setText("");
                    closeKeyBoard();

                } else {
                    Toast.makeText(getContext(), "comment can't be black", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // set the back arrow
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back.");
                if (getCallingActivityFromBundle().equals(getString(R.string.home_activity))) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    ((HomeActivity)mContext).showLayout();

                } else {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
    }
    private void closeKeyBoard() {
        View view = getActivity().getCurrentFocus();

        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addNewComment(String newComment) {
        Log.d(TAG, "addNewComment: adding new Comment: " + newComment);

        String commentID = mRef.push().getKey();

        Comment comment = new Comment();
        comment.setComment(newComment);
        comment.setDate_created(getTimeStamp());
        comment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //insert into photo node
        mRef.child(mContext.getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(mContext.getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

        //insert into user_photo node
        mRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(mPhoto.getUser_id())
                .child(mPhoto.getPhoto_id())
                .child(mContext.getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

    }

    private String getTimeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        return simpleDateFormat.format(new Date());
    }

    /**
     * retrieving photo from the incoming bundle from 'ProfileActivity' interface
     *
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
     * retrieving calling activity from the incoming bundle from 'ProfileActivity' interface
     *
     * @return
     */
    private String getCallingActivityFromBundle() {
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getString(getString(R.string.calling_activity));
        } else
            return null;
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

        if (mPhoto.getComments().size() == 0) {
            mComments.clear();

            Comment firstComment = new Comment();
            firstComment.setComment(mPhoto.getCaption());
            firstComment.setUser_id(mPhoto.getUser_id());
            firstComment.setDate_created(mPhoto.getDate_created());

            mComments.add(firstComment);
            mPhoto.setComments(mComments);
            setupWidgets();
        }

        mRef.child(mContext.getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(mContext.getString(R.string.field_comments))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Query query = mRef
                                .child(mContext.getString(R.string.dbname_photos))
                                .orderByChild(mContext.getString(R.string.field_photo_id))
                                .equalTo(mPhoto.getPhoto_id());

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

                                    mComments.clear();

                                    Comment firstComment = new Comment();
                                    firstComment.setComment(mPhoto.getCaption());
                                    firstComment.setUser_id(mPhoto.getUser_id());
                                    firstComment.setDate_created(mPhoto.getDate_created());

                                    mComments.add(firstComment);

                                    for (DataSnapshot dSnap : snap.child(mContext.getString(R.string.field_comments)).getChildren()) {
                                        Comment comment = new Comment();
                                        comment.setUser_id(dSnap.getValue(Comment.class).getUser_id());
                                        comment.setComment(dSnap.getValue(Comment.class).getComment());
                                        comment.setDate_created(dSnap.getValue(Comment.class).getDate_created());

                                        mComments.add(comment);
                                    }
                                    photo.setComments(mComments);
                                    mPhoto = photo;

                                    setupWidgets();
//
//                    List<Like> likesList = new ArrayList<>();
//
////                    for (DataSnapshot dSnap : snap.child(getString(R.string.field_likes)).getChildren()) {
////                        Like like = new Like();
////                        like.setUser_id(dSnap.getValue(Like.class).getUser_id());
////
////                        likesList.add(like);
////                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d(TAG, "onCancelled: query cancelled");

                            }
                        });

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
