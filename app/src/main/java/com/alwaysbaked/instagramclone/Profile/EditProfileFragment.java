package com.alwaysbaked.instagramclone.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alwaysbaked.instagramclone.R;
import com.alwaysbaked.instagramclone.Utils.UniversalImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileFragment extends Fragment {
    private static final String TAG = "EditProfileFragment";

    @BindView(R.id.profile_photo)
    ImageView mProfilePhoto;
    @BindView(R.id.backArrow)
    ImageView backArrow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);

        ButterKnife.bind(this, view);

        setProfileImage();

        //back arrow for navigating back to "ProfileActivity"
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to \"ProfileActivity\"");
                getActivity().finish();
            }
        });


        return view;
    }


    private void setProfileImage() {
        Log.d(TAG, "setProfileImage: setting profile image.");
        String imgURL = "https://cnet2.cbsistatic.com/img/3JQUEv_h8xcJ8QEcVNteWVADsew=/936x527/2017/08/21/ae78abff-be85-45e7-bae1-242ca5609f2c/androidoreolockup.jpg";
        UniversalImageLoader.setImage(imgURL, mProfilePhoto, null, "");
    }
}
