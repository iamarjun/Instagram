package com.alwaysbaked.instagramclone.Login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.alwaysbaked.instagramclone.R;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Log.d(TAG, "onCreate: started.");
    }
}
