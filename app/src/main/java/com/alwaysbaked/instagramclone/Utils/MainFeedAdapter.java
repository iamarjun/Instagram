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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
        User mUser = new User();
        StringBuilder mUsers;
        String mLikesString;
        boolean mLikedByCurrentUser;
        Heart mHeart;
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
            holder.mCommentBubble = convertView.findViewById(R.id.commentBubble);

            holder.mHeart = new Heart(holder.mWhiteHeart, holder.mRedHeart);
            holder.mPhoto = getItem(position);
            holder.detector = new GestureDetector(mContext, new GestureListener(holder));
            holder.mUsers = new StringBuilder();

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    private void addNewLike(ViewHolder holder) {
        Log.d(TAG, "addNewLike: add a new mLike.");

        String newLikeID = mRef.push().getKey();
        Like like = new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mRef.child(mContext.getString(R.string.dbname_photos))
                .child(holder.mPhoto.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(holder.mPhoto.getUser_id())
                .child(holder.mPhoto.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        holder.mHeart.toggleLike();
        getLikesString(holder);
    }

    private void getLikesString(final ViewHolder holder) {
        Log.d(TAG, "getLikesString: getting mLikes string");

        try {
            Query query = mRef
                    .child(mContext.getString(R.string.dbname_photos))
                    .child(holder.mPhoto.getPhoto_id())
                    .child(mContext.getString(R.string.field_likes));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    holder.mUsers = new StringBuilder();
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        Query query = mRef
                                .child(mContext.getString(R.string.dbname_users))
                                .orderByChild(mContext.getString(R.string.field_user_id))
                                .equalTo(snap.getValue(Like.class).getUser_id());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                    Log.d(TAG, "onDataChange: found mLike: " + snap.getValue(User.class).getUsername());

                                    holder.mUsers.append(snap.getValue(User.class).getUsername());
                                    holder.mUsers.append(", ");
                                }

                                String[] splitUsers = holder.mUsers.toString().split(", ");

                                holder.mLikedByCurrentUser = holder.mUsers.toString().contains(holder.mUser.getUsername() + ", ");

                                int likes = splitUsers.length;

                                if (likes == 1) {
                                    holder.mLikesString = "Liked by " + splitUsers[0];

                                } else if (likes == 2) {
                                    holder.mLikesString = "Liked by " + splitUsers[0] + " and " + splitUsers[1];

                                } else if (likes == 3) {
                                    holder.mLikesString = "Liked by " + splitUsers[0] + ", " + splitUsers[1] + " and " + splitUsers[2];

                                } else if (likes == 4) {
                                    holder.mLikesString = "Liked by " + splitUsers[0] + ", " + splitUsers[1] + ", " + splitUsers[2] + " and " + splitUsers[3];

                                } else if (likes > 4) {
                                    holder.mLikesString = "Liked by " + splitUsers[0] + ", " + splitUsers[1] + ", " + splitUsers[2] + " and " + (splitUsers.length - 3) + " other";
                                }

                                //setup likes string
                                setupLikesString(holder, holder.mLikesString);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    if (!dataSnapshot.exists()) {
                        holder.mLikesString = "";
                        holder.mLikedByCurrentUser = false;

                        //setup likes string
                        setupLikesString(holder, holder.mLikesString);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (NullPointerException e) {
            Log.d(TAG, "getLikesString: NullPointerException: " + e.getMessage());
            holder.mLikesString = "";
            holder.mLikedByCurrentUser = false;

            //setup likes string
            setupLikesString(holder, holder.mLikesString);
        }
    }

    private void setupLikesString(final ViewHolder holder, String mLikesString) {
        Log.d(TAG, "setupLikesString: likes string: " + mLikesString);

        if (holder.mLikedByCurrentUser) {
            Log.d(TAG, "setupLikesString: photo liked current user");
            holder.mWhiteHeart.setVisibility(View.GONE);
            holder.mRedHeart.setVisibility(View.VISIBLE);
            holder.mRedHeart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return holder.detector.onTouchEvent(event);
                }
            });
        } else {
            Log.d(TAG, "setupLikesString: photo not liked current user");
            holder.mWhiteHeart.setVisibility(View.VISIBLE);
            holder.mRedHeart.setVisibility(View.GONE);
            holder.mWhiteHeart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return holder.detector.onTouchEvent(event);
                }
            });
        }

        holder.mLikes.setText(mLikesString);
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
                        //case#1 The mUser already liked the photo
                        if (holder.mLikedByCurrentUser &&
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

                            holder.mHeart.toggleLike();
                            getLikesString(holder);
                        }

                        //case#2 The mUser has not liked the photo
                        else if (!holder.mLikedByCurrentUser) {
                            //add new mLike
                            addNewLike(holder);
                            break;
                        }
                    }

                    if (!dataSnapshot.exists()) {
                        //add new mLike
                        addNewLike(holder);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return true;
        }

    }

    /**
     * returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimeStampDifference(Photo mPhoto) {
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

}
