package com.alwaysbaked.instagramclone.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.alwaysbaked.instagramclone.Models.User;
import com.alwaysbaked.instagramclone.Models.UserAccountSettings;
import com.alwaysbaked.instagramclone.Models.UserSettings;
import com.alwaysbaked.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;
    private String userID;
    private Context mContext;


    public FirebaseMethods(Context mContext) {
        this.mContext = mContext;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();

        if (mAuth.getCurrentUser() != null)
            userID = mAuth.getCurrentUser().getUid();
    }

    public int getImageCount(DataSnapshot dataSnapshot){
        int count = 0;
        for (DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()){

            count++;
        }
        return  count;
    }

    /**
     * update username in 'users's' and 'user_account_settings's' nodes
     * @param username
     */
    public void updateUsername(String username){
        Log.d(TAG, "updateUsername: updating username to " + username);
        mRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        mRef.child(mContext.getString(R.string.dbname_users_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }

    /**
     * update email in 'users's' node
     * @param email
     */
    public void updateEmail(String email){
        Log.d(TAG, "updateEmail: updating email to " + email);
        mRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);

    }

    public void updateUserSettings(String displayName, String website, String description, long phoneNumber){
        Log.d(TAG, "updateUserSettings: updating user settings to " + displayName + " " + website + " " + description + " " + phoneNumber + " ");

        if (displayName != null)
            //updating display name
            mRef.child(mContext.getString(R.string.dbname_users_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_display_name))
                    .setValue(displayName);

        if (website != null)
            //updating website
            mRef.child(mContext.getString(R.string.dbname_users_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_website))
                    .setValue(website);

        if (description != null)
            //updating description
            mRef.child(mContext.getString(R.string.dbname_users_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_description))
                    .setValue(description);

        if (phoneNumber != 0)
            //updating phone number
            mRef.child(mContext.getString(R.string.dbname_users))
                    .child(userID)
                    .child(mContext.getString(R.string.field_phone_number))
                    .setValue(phoneNumber);
    }

    public void createAccount(final String email, final String password, final String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(mContext, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();

                            // send email verification
                            sendVerificationEmail();

                            userID = mAuth.getCurrentUser().getUid();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });

    }

   /* public boolean checkIfUsernameExists(String username, DataSnapshot dataSnapshot) {
        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists.");

        User user = new User();

        Log.d(TAG, "checkIfUsernameExists: userid: " + userID);

        for (DataSnapshot ds : dataSnapshot.child(userID).getChildren()) {
            Log.d(TAG, "checkIfUsernameExists: datasnapshot: " + ds.child(userID));

            user.setUsername(ds.getValue(User.class).getUsername());
            Log.d(TAG, "checkIfUsernameExists: username: " + user.getUsername());

            if (StringManipulation.expandUsername(user.getUsername()).equals(username)) {
                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + user.getUsername());
                return true;
            }
        }
        return false;
    }*/

    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Toast.makeText(mContext, "verification email send successfully", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(mContext, "couldn't send verification email", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    /**
     * adding information to user and user_account_settings node.
     *
     * @param email         email of the user, also user for email verification
     * @param username      username of the user ??!! duh !!
     * @param description   user's description
     * @param website       user's website link
     * @param profile_photo user's profile photo.
     */

    public void addNewUser(String email, String username, String description, String website, String profile_photo) {
        User user = new User(
                userID,
                0,
                email,
                StringManipulation.condenseUsername(username));

        mRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);

        UserAccountSettings settings = new UserAccountSettings(
                description,
                username,
                0,
                0,
                0,
                profile_photo,
                StringManipulation.condenseUsername(username),
                website);
        mRef.child(mContext.getString(R.string.dbname_users_account_settings))
                .child(userID)
                .setValue(settings);
    }

    public UserSettings getUserSettings(DataSnapshot dataSnapshot) {
        Log.d(TAG, "getUserSettings: retrieveing user account settings from firebase");

        User user = new User();
        UserAccountSettings settings = new UserAccountSettings();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            // user_account_settings node.
            if (ds.getKey().equals(mContext.getString(R.string.dbname_users_account_settings))) {
                Log.d(TAG, "getUserSettings: datasnapshot: " + ds);

                try {

                    settings.setDisplay_name(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDisplay_name()
                    );
                    settings.setUsername(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getUsername()
                    );
                    settings.setWebsite(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getWebsite()
                    );
                    settings.setDescription(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDescription()
                    );
                    settings.setProfile_photo(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getProfile_photo()
                    );
                    settings.setPosts(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getPosts()
                    );
                    settings.setFollowers(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowers()
                    );
                    settings.setFollowing(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowing()
                    );

                    Log.d(TAG, "getUserSettings: retrieved user_account_settings info: " + settings.toString());

                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserSettings: NullPointerException: " + e.getMessage());
                }

            }

            // user_account_settings node.
            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                Log.d(TAG, "getUserSettings: datasnapshot: " + ds);

                try {
                    user.setUsername(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getUsername()
                    );
                    user.setEmail(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getEmail()
                    );
                    user.setPhone_number(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getPhone_number()
                    );
                    user.setUser_id(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getUser_id()
                    );

                    Log.d(TAG, "getUserSettings: retrieved users info: " + user.toString());

                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserSettings: NullPointerException: " + e.getMessage());
                }
            }
        }

        return new UserSettings(user, settings);
    }
}
