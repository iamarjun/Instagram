package com.alwaysbaked.instagramclone.Profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.alwaysbaked.instagramclone.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountSettingsActivity extends AppCompatActivity {
    private static final String TAG = "AccountSettingsActivity";
    private Context mContext = AccountSettingsActivity.this;

    @BindView(R.id.lvAccountSettngs)
    ListView listView;
    @BindView(R.id.backArrow)
    ImageView backArrow;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);
        Log.d(TAG, "onCreate: started");

        ButterKnife.bind(this);

        setupSettingsList();

        //setup backArrow for navigating back to "ProfileActivity"
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to \"ProfileActivity\".");
                finish();
            }
        });
    }

    private void setupSettingsList(){
        Log.d(TAG, "setupSettingsList: initializing account settings list.");
        List<String> option = new ArrayList<>();
        option.add(getString(R.string.edit_profile));
        option.add(getString(R.string.sign_out));

        ArrayAdapter arrayAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, option );
        listView.setAdapter(arrayAdapter);
    }


}
