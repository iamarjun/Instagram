package com.alwaysbaked.instagramclone.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.alwaysbaked.instagramclone.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

public class GridImageAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private List<String> imgURLs;

    public GridImageAdapter(Context mContext,
                            int layoutResource,
                            String mAppend,
                            List<String> imgURLs) {
        super(mContext, layoutResource, imgURLs);
        this.mContext = mContext;
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutResource = layoutResource;
        this.mAppend = mAppend;
        this.imgURLs = imgURLs;
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
            viewHolder.image = convertView.findViewById(R.id.gridImageView);
            viewHolder.mProgressBar = convertView.findViewById(R.id.gridImageProgressBar);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

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

    private static class ViewHolder {
        SquareImageView image;
        ProgressBar mProgressBar;
    }
}
