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
import android.widget.Toast;

import com.alwaysbaked.instagramclone.Dialogs.ConfirmDialogPassword;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment implements ConfirmDialogPassword.OnConfirmpasswordListener{

    @Override
    public void confirmPassword(String password) {
        Log.d(TAG, "confirmPassword: got the passowrd: " + password);
    }
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

    //variables
    private UserSettings mUserSettings;

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

        //checkmark to save changes

        mSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes.");
                saveProfileSettings();
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


        //case 1: if the user made changes to the username.
        if (!mUserSettings.getUser().getUsername().equals(username)) {

            checkIfUsernameExists(username);

        }
        //case 2: if the user made changes to their email.
        if (!mUserSettings.getUser().getEmail().equals(email)) {

            //#1 Re-Authenticate
            //        -confirm the current email and password.

            ConfirmDialogPassword dialogPassword = new ConfirmDialogPassword();
            dialogPassword.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
            dialogPassword.setTargetFragment(EditProfileFragment.this, 1);


            //#2 Check if the email already registered.
            //        -'fetchProvidersForEmail(String email)'
            //#3 Change the email
            //        - submit the new email to the database and verify.


        }


    }

    /**
     *check if @patram username exists in database.
     * @param username
     */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    //add username
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(getActivity(), "Username changed", Toast.LENGTH_SHORT).show();

                }
                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH " + singleSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(getActivity(), "That username already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setProfileWidget(UserSettings userSettings){
        Log.d(TAG, "setProfileWidget: settings widgets with data retrieved from firebase: " + userSettings.toString());

        mUserSettings = userSettings;

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
