package com.alwaysbaked.instagramclone.Home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alwaysbaked.instagramclone.Models.Comment;
import com.alwaysbaked.instagramclone.Models.Like;
import com.alwaysbaked.instagramclone.Models.Photo;
import com.alwaysbaked.instagramclone.Models.User;
import com.alwaysbaked.instagramclone.Models.UserAccountSettings;
import com.alwaysbaked.instagramclone.R;
import com.alwaysbaked.instagramclone.Utils.MainFeedAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    //variables
    private List<Photo> mPhotos;
    private List<String> mFollowing;
    private List<User> mUsers;
    private MainFeedAdapter adapter;

    //widgets
    @BindView(R.id.lvFeed)
    ListView mFeed;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d(TAG, "onCreateView: starting");

        ButterKnife.bind(this, view);

        mPhotos = new ArrayList<>();
        mFollowing = new ArrayList<>();

        getFollowing();

        return view;
    }

    private void getFollowing() {
        Log.d(TAG, "getFollowing: searching for following");

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        Query query = mRef
                .child(getString(R.string.dbname_followings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + snap.child(getString(R.string.field_user_id)).getValue());
                    mFollowing.add(snap.child(getString(R.string.field_user_id)).getValue().toString());
                }
                //get photos
                getPhotos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPhotos() {
        Log.d(TAG, "getPhotos: getting photos");

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

        for (int i = 0; i < mFollowing.size() ; i++) {
            final int count = i;
            Query query = mRef
                    .child(getString(R.string.dbname_user_photos))
                    .child(mFollowing.get(i))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(i));

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
                        mPhotos.add(photo);
                    }

                    if (count >= mFollowing.size() -1) {
                        //display photos
                        displayPhotos();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void displayPhotos() {
        Log.d(TAG, "displayPhotos: display photos.");
        if (mPhotos != null) {
            Collections.sort(mPhotos, new Comparator<Photo>() {
                @Override
                public int compare(Photo o1, Photo o2) {
                    return o2.getDate_created().compareTo(o1.getDate_created());
                }
            });

            adapter = new MainFeedAdapter(getContext(), R.layout.layout_feed_listitem, mPhotos);
            mFeed.setAdapter(adapter);
        }
    }
}
