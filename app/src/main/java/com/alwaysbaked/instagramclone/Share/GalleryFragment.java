package com.alwaysbaked.instagramclone.Share;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.alwaysbaked.instagramclone.R;
import com.alwaysbaked.instagramclone.Utils.FilePaths;
import com.alwaysbaked.instagramclone.Utils.FileSearch;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";

    //widgets
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

    //variables
    private ArrayList<String> directories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        Log.d(TAG, "onCreateView: started");

        ButterKnife.bind(this, view);

        directories = new ArrayList<>();

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

        init();

        return view;
    }

    private void init(){
        FilePaths filePaths = new FilePaths();

        //check for other folders inside "storage/emulated/0/Pictures"
        if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
            directories = FileSearch.getDirectoryPaths((filePaths).PICTURES);
        }

        directories.add(filePaths.CAMERA);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, directories);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mDirectorySpinner.setAdapter(arrayAdapter);

        mDirectorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + directories.get(position));

                //setup image grid for the directory chosen
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}
