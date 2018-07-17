package com.alwaysbaked.instagramclone.Share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class NextActivity extends AppCompatActivity {
    private static final String TAG = "NextActivity";

    //constants
    private Context mContext = NextActivity.this;
    private static final String mAppend = "file:/";

    //widgets
    @BindView(R.id.imageShare)
    ImageView mShareImage;
    @BindView(R.id.caption)
    EditText mCaption;
    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.tvShare)
    TextView mShare;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;

    //variables
    private int imageCount = 0;
    private String imgURL;
    private Intent intent;
    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        Log.d(TAG, "onCreate: starting.");

        ButterKnife.bind(this);

        mFirebaseMethods = new FirebaseMethods(mContext);

        setupFirebaseAuth();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the activity.");
                finish();
            }
        });

        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to share the image to the feed.");
                Toast.makeText(mContext, "Uploading image to firebase cloud stroage", Toast.LENGTH_SHORT).show();
                String caption = mCaption.getText().toString();

                if (intent.hasExtra(getString(R.string.select_image))) {
                    imgURL = intent.getStringExtra(getString(R.string.select_image));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, imgURL, null);


                } else if (intent.hasExtra(getString(R.string.select_bitmap))){
                    bitmap = intent.getParcelableExtra(getString(R.string.select_bitmap));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, null, bitmap);
                }
            }
        });

        setImage();
    }

    /**
     * gets the image url from the incoming intent and displays the chosen image
     */
    private void setImage(){
        intent = getIntent();

        //from gallery fragment
        if (intent.hasExtra(getString(R.string.select_image))) {
            Log.d(TAG, "setImage: got a image url: " + imgURL);
            imgURL = intent.getStringExtra(getString(R.string.select_image));
            UniversalImageLoader.setImage(imgURL, mShareImage, null, mAppend);

        }//from photo fragment
        else if (intent.hasExtra(getString(R.string.select_bitmap))){
            bitmap = intent.getParcelableExtra(getString(R.string.select_bitmap));
            Log.d(TAG, "setImage: got a bitmap");
            mShareImage.setImageBitmap(bitmap);
        }

    }

     /*
    ------------------------------------------ Firebase --------------------------------------------
     */

    /**
     * setup firebase auth object.
     */

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        Log.d(TAG, "setupFirebaseAuth: image count: " + imageCount);

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
                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count: " + imageCount);

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
