package com.alwaysbaked.instagramclone.Utils;

import android.content.Context;
import android.util.Log;

import com.alwaysbaked.instagramclone.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String Uid;
    private Context mContext;


    public FirebaseMethods(Context mContext) {
        this.mContext = mContext;
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null)
            Uid = mAuth.getCurrentUser().getUid();
    }

    public boolean checkIfUsernameExists(String username, DataSnapshot dataSnapshot) {
        Log.d(TAG, "checkIfUsernameExists: checking if" + username + "already exists.");

        User user = new User();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Log.d(TAG, "checkIfUsernameExists: datasnapshot: " + ds);

            user.setUsername(ds.getValue(User.class).getUsername());
            Log.d(TAG, "checkIfUsernameExists: username: " + user.getUsername());

            if (StringManipulation.expandUsername(user.getUsername()).equals(username)){
                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + user.getUsername());
                return true;
            }
        }
        return false;
    }
}
