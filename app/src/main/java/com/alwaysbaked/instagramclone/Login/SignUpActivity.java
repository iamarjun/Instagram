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
import android.widget.Toast;

import com.alwaysbaked.instagramclone.Models.User;
import com.alwaysbaked.instagramclone.R;
import com.alwaysbaked.instagramclone.Utils.FirebaseMethods;
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

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    @BindView(R.id.input_email)
    EditText mEmail;
    @BindView(R.id.input_username)
    EditText mUsername;
    @BindView(R.id.input_password)
    EditText mPassword;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.signingUp)
    TextView mSigningUp;
    @BindView(R.id.link_login)
    TextView linkLogin;
    @BindView(R.id.btn_signup)
    Button btnSignUp;
    private Context mContext = SignUpActivity.this;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;
    private FirebaseMethods firebaseMethods;
    private String email, username, password;
    private String append = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Log.d(TAG, "onCreate: started.");

        ButterKnife.bind(this);

        firebaseMethods = new FirebaseMethods(mContext);

        mProgressBar.setVisibility(View.GONE);
        mSigningUp.setVisibility(View.GONE);

        setupFirebaseAuth();
        init();
    }

    /*
    ------------------------------------------ Firebase --------------------------------------------
     */

    private void init() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: signin in new user");

                email = mEmail.getText().toString().trim();
                username = mUsername.getText().toString().trim();
                password = mPassword.getText().toString().trim();

                if (email.isEmpty() && username.isEmpty() && password.isEmpty()) {
                    Toast.makeText(mContext, "Fields can't be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mSigningUp.setVisibility(View.VISIBLE);
                    firebaseMethods.createAccount(email, password, username);

                }


            }
        });

        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to login screen.");
                finish();
            }
        });
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

                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH " + singleSnapshot.getValue(User.class).getUsername());
                        append = mRef.push().getKey().substring(3, 10);
                        Log.d(TAG, "onDataChange: username already exists. Appending random string to name: " + append);
                    }
                }

                String mUsername = "";

                mUsername = username + append;

                //add new user to the database
                firebaseMethods.addNewUser(email, mUsername, "", "", "");
                Toast.makeText(mContext, "Sign Up Successful. Sending verification email.", Toast.LENGTH_SHORT).show();

                // sign out user after successful sign up for email verification
                mAuth.signOut();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    /**
     * setup firebase auth object.
     */

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: signed in:" + user.getUid());

                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            checkIfUsernameExists(username);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    //moving back to login screen after successful sign up.
                    finish();

                } else
                    Log.d(TAG, "onAuthStateChanged: signed out");
            }
        };


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }

}
