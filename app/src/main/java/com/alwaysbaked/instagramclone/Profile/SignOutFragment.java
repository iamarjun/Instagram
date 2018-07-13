package com.alwaysbaked.instagramclone.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alwaysbaked.instagramclone.Login.LoginActivity;
import com.alwaysbaked.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignOutFragment extends Fragment {
    private static final String TAG = "SignOutFragment";
    @BindView(R.id.progressBar)
    ProgressBar mProgressbar;
    @BindView(R.id.tvSigningOut)
    TextView tvSigningOut;
    @BindView(R.id.tvConfirmSignOut)
    TextView tvSignOut;
    @BindView(R.id.btnConfirmSignOut)
    Button btnSignOut;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signout, container, false);

        ButterKnife.bind(this, view);

        mProgressbar.setVisibility(View.GONE);
        tvSigningOut.setVisibility(View.GONE);

        setupFrebaseAuth();

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to sign out.");
                mProgressbar.setVisibility(View.VISIBLE);
                tvSigningOut.setVisibility(View.VISIBLE);
                mAuth.signOut();
                getActivity().finish();
            }
        });
        return view;
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
                else {
                    Log.d(TAG, "onAuthStateChanged: signed out");

                    Log.d(TAG, "onAuthStateChanged: navigating back to 'LoginActivity' ");
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
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
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }


}
