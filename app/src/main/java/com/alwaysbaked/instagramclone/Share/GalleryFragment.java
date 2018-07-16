package com.alwaysbaked.instagramclone.Share;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.alwaysbaked.instagramclone.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";

    @BindView(R.id.gridView)
    GridView mGridView;
    @BindView(R.id.galleryImageView)
    ImageView mGalleryImage;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.spinnerDirectory)
    Spinner mDirectorySpinner;
    @BindView(R.id.close)
    ImageView mClose;
    @BindView(R.id.tvNext)
    TextView mNext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        Log.d(TAG, "onCreateView: started");

        ButterKnife.bind(this, view);

        mProgressBar.setVisibility(View.GONE);
        
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment.");
                getActivity().finish();
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to final share screen.");
            }
        });


        return view;
    }
}
