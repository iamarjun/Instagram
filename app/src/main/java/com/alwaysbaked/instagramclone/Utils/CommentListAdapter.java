package com.alwaysbaked.instagramclone.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysbaked.instagramclone.Models.Comment;
import com.alwaysbaked.instagramclone.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentListAdapter extends ArrayAdapter<Comment> {
    private static final String TAG = "CommentListAdapter";

    private LayoutInflater mInflater;
    private int layoutResource;
    private Context mContext;

    public CommentListAdapter(@NonNull Context context, int resource, @NonNull List<Comment> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }

    private static class ViewHolder {
        TextView mComment, mUsername, mTimeStamp, mReply, mLikes;
        CircleImageView mProfilePhoto;
        ImageView mLike;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        /**
         * viewholder build patter similar to recyclerview
         */
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.mComment = convertView.findViewById(R.id.tvComment);
            viewHolder.mUsername = convertView.findViewById(R.id.tvUsername);
            viewHolder.mTimeStamp = convertView.findViewById(R.id.tvTimeStamp);
            viewHolder.mReply = convertView.findViewById(R.id.tvReply);
            viewHolder.mLikes = convertView.findViewById(R.id.tvLikes);
            viewHolder.mLike = convertView.findViewById(R.id.like);
            viewHolder.mProfilePhoto = convertView.findViewById(R.id.profile_photo);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //set the mComment
        viewHolder.mComment.setText(getItem(position).getComment());

        //set the mTimeStamp
        String timeStampDifference = getTimeStampDifference(getItem(position));

        if (!timeStampDifference.equals("0")) {
            viewHolder.mTimeStamp.setText(timeStampDifference + " d");

        } else {
            viewHolder.mTimeStamp.setText("today");
        }





        String imgURL = getItem(position);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mAppend + imgURL, viewHolder.image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (viewHolder.mProgressBar != null)
                    viewHolder.mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (viewHolder.mProgressBar != null)
                    viewHolder.mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (viewHolder.mProgressBar != null)
                    viewHolder.mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (viewHolder.mProgressBar != null)
                    viewHolder.mProgressBar.setVisibility(View.GONE);
            }
        });

        return convertView;
    }

    /**
     * returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimeStampDifference(Comment comment) {
        Log.d(TAG, "getTimeStampDifference: gettting mTimeStamp difference");

        String difference;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("America/NewYork"));
        Date today = c.getTime();
        sdf.format(today);
        Date timeStamp;
        final String photoTimeStamp = comment.getDate_created();
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
