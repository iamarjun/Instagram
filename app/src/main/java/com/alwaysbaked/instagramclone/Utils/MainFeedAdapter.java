package com.alwaysbaked.instagramclone.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysbaked.instagramclone.Models.Like;
import com.alwaysbaked.instagramclone.Models.Photo;
import com.alwaysbaked.instagramclone.Models.User;
import com.alwaysbaked.instagramclone.Models.UserAccountSettings;
import com.alwaysbaked.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainFeedAdapter extends ArrayAdapter<Photo>{
    private static final String TAG = "MainFeedAdapter";

    //variables
    private LayoutInflater mInflater;
    private int mLayoutResource;
    private Context mContext;
    private String mCurrentUsername;

    //Firebase
    private DatabaseReference mRef;


    public MainFeedAdapter(@NonNull Context context, int resource, @NonNull List<Photo> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mLayoutResource = resource;
    }

    static class ViewHolder {
        CircleImageView mProfilePhoto;
        TextView mUsername, mLikes, mCaption, mComment, mTimeStamp, m;
        SquareImageView mImage;
        ImageView mRedHeart, mWhiteHeart, mCommentBubble;

        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();
        StringBuilder users;
        String likesString;
        boolean isLikedByCurrentUser;
        Heart heart;
        GestureDetector detector;
        Photo mPhoto;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutResource, parent, false);
            holder = new ViewHolder();

            holder.mUsername = convertView.findViewById(R.id.tvUsername);
            holder.mImage = convertView.findViewById(R.id.post_image);
            holder.mProfilePhoto = convertView.findViewById(R.id.profile_photo);
            holder.mRedHeart = convertView.findViewById(R.id.heart_red);
            holder.mWhiteHeart = convertView.findViewById(R.id.heart_white);
            holder.mComment = convertView.findViewById(R.id.tvComments);
            holder.mCaption = convertView.findViewById(R.id.tvCaption);
            holder.mLikes = convertView.findViewById(R.id.tvLikes);
            holder.mTimeStamp = convertView.findViewById(R.id.tvTimeStamp);

            holder.heart = new Heart(holder.mWhiteHeart, holder.mRedHeart);
            holder.mPhoto = getItem(position);
            holder.detector = new GestureDetector(mContext, new GestureListener(holder));
            holder.users = new StringBuilder();

            convertView.setTag(holder);

        }



        return convertView;
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {

        ViewHolder holder;

        public GestureListener(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: double tap detected");

            Query query = mRef
                    .child(mContext.getString(R.string.dbname_photos))
                    .child(holder.mPhoto.getPhoto_id())
                    .child(mContext.getString(R.string.field_likes));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        String keyID = snap.getKey();
                        //case#1 The user already liked the photo
                        if (holder.isLikedByCurrentUser &&
                                snap.getValue(Like.class).getUser_id()
                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                            mRef.child(mContext.getString(R.string.dbname_photos))
                                    .child(holder.mPhoto.getPhoto_id())
                                    .child(mContext.getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();

                            mRef.child(mContext.getString(R.string.dbname_user_photos))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(holder.mPhoto.getPhoto_id())
                                    .child(mContext.getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();

                            holder.heart.toggleLike();
                            getLikesString();
                        }

                        //case#2 The user has not liked the photo
                        else if (!holder.isLikedByCurrentUser) {
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

}
