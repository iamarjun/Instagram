package com.alwaysbaked.instagramclone.Login;

import android.content.Context;
import android.content.Intent;
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

import com.alwaysbaked.instagramclone.Home.HomeActivity;
import com.alwaysbaked.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private Context mContext = LoginActivity.this;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.input_email)
    EditText mEmail;
    @BindView(R.id.input_password)
    EditText mPassword;

    @BindView(R.id.authentication)
    TextView mAuthentication;
    @BindView(R.id.link_signup)
    TextView linkSignUp;

    @BindView(R.id.btn_login)
    Button btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: started.");

        ButterKnife.bind(this);

        mProgressBar.setVisibility(View.GONE);
        mAuthentication.setVisibility(View.GONE);

        setupFrebaseAuth();

        init();
    }

    /*
    ------------------------------------------ Firebase --------------------------------------------
     */

    private void init() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to login.");

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(mContext, "Fields can't be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mAuthentication.setVisibility(View.VISIBLE);
                    signIn(email, password);

                }
            }

        });

        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to sign up screen.");
                Intent intent = new Intent(mContext, SignUpActivity.class);
                startActivity(intent);
            }
        });

        /*
         * if the user is logged in then navigate to 'HomeActivity' and call 'finish()'
         */
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(mContext, HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void signIn(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            mProgressBar.setVisibility(View.GONE);
                            mAuthentication.setVisibility(View.GONE);
                            try {
                                if (user.isEmailVerified()) {
                                    Log.d(TAG, "onComplete: email verified");
                                    Intent intent = new Intent(mContext, HomeActivity.class);
                                    startActivity(intent);
                                }else {
                                    Toast.makeText(mContext, "Verify email to sign in.", Toast.LENGTH_SHORT).show();
                                    mProgressBar.setVisibility(View.GONE);
                                    mAuthentication.setVisibility(View.GONE);
                                    mAuth.signOut();
                                }


                            }catch (NullPointerException e) {
                                Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage());
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                            mAuthentication.setVisibility(View.GONE);
                        }

                        // ...
                    }
                });
    }

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

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }

}
