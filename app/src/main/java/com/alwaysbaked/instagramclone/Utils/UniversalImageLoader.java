package com.alwaysbaked.instagramclone.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.alwaysbaked.instagramclone.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class UniversalImageLoader {
    private static final int defaultImage = R.drawable.ic_android;
    private Context mContext;

    public UniversalImageLoader(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * this method can be used to set images that re static. it an't be used if the images are'nt
     * being changes in the fragment/activity - OR if they are being set in a list or a grid.
     *
     * @param imgURL
     * @param imageView
     * @param mProgressbar
     * @param append
     */

    public static void setImage(String imgURL, ImageView imageView, final ProgressBar mProgressbar, String append) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imgURL, imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (mProgressbar != null)
                    mProgressbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (mProgressbar != null)
                    mProgressbar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (mProgressbar != null)
                    mProgressbar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (mProgressbar != null)
                    mProgressbar.setVisibility(View.GONE);
            }
        });
    }

    public ImageLoaderConfiguration getConfig() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImage)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .cacheOnDisk(true).cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024)
                .build();

        return configuration;

    }
}
