package com.alwaysbaked.instagramclone.Login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alwaysbaked.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private Context mContext = SignUpActivity.this;
    private String email, fullname, password;

    @BindView(R.id.input_email)
    EditText mEmail;
    @BindView(R.id.input_fullname)
    EditText mFullName;
    @BindView(R.id.input_password)
    EditText mPassword;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.signingUp)
    TextView mSigningUp;

    @BindView(R.id.link_signup)
    TextView linkLogin;

    @BindView(R.id.btn_signup)
    Button btnSignUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Log.d(TAG, "onCreate: started.");

        ButterKnife.bind(this);

        mProgressBar.setVisibility(View.GONE);
        mSigningUp.setVisibility(View.GONE);

        setupFrebaseAuth();
    }

    private void updateUI(FirebaseUser user){
        if (user != null){
            mProgressBar.setVisibility(View.GONE);
            mSigningUp.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            mSigningUp.setVisibility(View.VISIBLE);
        }
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


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthStateListener);
        //updateUI(currentUser);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }

}
