package com.alwaysbaked.instagramclone.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysbaked.instagramclone.Models.User;
import com.alwaysbaked.instagramclone.Models.UserAccountSettings;
import com.alwaysbaked.instagramclone.Models.UserSettings;
import com.alwaysbaked.instagramclone.R;
import com.alwaysbaked.instagramclone.Utils.FirebaseMethods;
import com.alwaysbaked.instagramclone.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {
    private static final String TAG = "EditProfileFragment";

    @BindView(R.id.profile_photo)
    CircleImageView mProfilePhoto;
    @BindView(R.id.backArrow)
    ImageView mBackArrow;
    @BindView(R.id.saveChanges)
    ImageView mSaveChanges;


    @BindView(R.id.name)
    EditText mDisplayName;
    @BindView(R.id.username)
    EditText mUsername;
    @BindView(R.id.website)
    EditText mWebsite;
    @BindView(R.id.description)
    EditText mDescription;
    @BindView(R.id.email)
    EditText mEmail;
    @BindView(R.id.phone_number)
    EditText mPhoneNumber;

    @BindView(R.id.tvChangeProfilePhoto)
    TextView mChangeProfilePhoto;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);

        ButterKnife.bind(this, view);

        mFirebaseMethods = new FirebaseMethods(getActivity());

        //setProfileImage();
        setupFrebaseAuth();

        //back arrow for navigating back to "ProfileActivity"
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to \"ProfileActivity\"");
                getActivity().finish();
            }
        });


        return view;
    }

    /**
     * Retrieves the data saved in the widgets and submits it to the database
     * Before doing so it check sto make sure the username chosen is unique.
     */
    private void saveProfileSettings(){
        final String displayName = mDisplayName.getText().toString();
        final String username = mUsername.getText().toString();
        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmail.getText().toString();
        final long phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = new User();
                for (DataSnapshot ds: dataSnapshot.child(getString(R.string.dbname_users)).getChildren()){
                    if (ds.getKey().equals(userID)){
                        user.setUsername(ds.getValue(User.class).getUsername());
                    }
                }

                Log.d(TAG, "onDataChange: CURRENT USERNAME: " + user.getUsername());

                //case 1: user did not change their username.

                if (user.getUsername().equals(username)) {

                }
                //case 2: user changed their username therefore we need to check uniqueness.

                else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setProfileWidget(UserSettings userSettings){
        Log.d(TAG, "setProfileWidget: settings widgets with data retrieved from firebase: " + userSettings.toString());

        //User user = userSettings().getUser();
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mEmail.setText(userSettings.getUser().getEmail());
        mPhoneNumber.setText(String.valueOf(userSettings.getUser().getPhone_number()));
    }

    /*
    ------------------------------------------ Firebase --------------------------------------------
     */

    /**
     * setup firebase auth object.
     */

    private void setupFrebaseAuth() {
        Log.d(TAG, "setupFrebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();

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

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: settings user's data");

                //retrieve user information from database.
                setProfileWidget(mFirebaseMethods.getUserSettings(dataSnapshot));


                //retrieve images for the user in question.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }
}
