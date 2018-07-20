package com.alwaysbaked.instagramclone.Search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alwaysbaked.instagramclone.Models.User;
import com.alwaysbaked.instagramclone.R;
import com.alwaysbaked.instagramclone.Utils.BottomNavigationViewHelper;
import com.alwaysbaked.instagramclone.Utils.UserListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUMBER = 1;

    //widgets
    @BindView(R.id.bottomNavViewBar)
    BottomNavigationViewEx bottomNavigationViewEx;

    @BindView(R.id.tvSearch)
    EditText mSearch;

    @BindView(R.id.lvSearchResult)
    ListView mSearchResult;

    //variables
    private Context mContext = SearchActivity.this;
    private List<User> mUserList;
    private UserListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "onCreate: staring.");

        ButterKnife.bind(this);

        hideSoftKeyboard();
        setupBottomNavigationView();
        initTextListener();

    }

    private void initTextListener() {
        Log.d(TAG, "initTextListener: initializing");
         mUserList = new ArrayList<>();

         mSearch.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {

             }

             @Override
             public void afterTextChanged(Editable s) {

                 String text = mSearch.getText().toString().toLowerCase(Locale.getDefault());
                 searchForMatch(text);

             }
         });

    }

    private void searchForMatch(String keywoard) {
        Log.d(TAG, "searchForMatch: searching for a match: " + keywoard);
        mUserList.clear();

        //update users list view
        if (keywoard.length() == 0) {

        } else {
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
            Query query = mRef
                    .child(getString(R.string.dbname_users))
                    .orderByChild(getString(R.string.field_username))
                    .equalTo(keywoard);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: found user: "  + ds.getValue(User.class).toString());

                        mUserList.add(ds.getValue(User.class));

                        //update users list view
                        updateUsersList();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateUsersList() {
        Log.d(TAG, "updateUsersList: updating users list");

        adapter = new UserListAdapter(mContext, R.layout.layout_user_listitem, mUserList);
        mSearchResult.setAdapter(adapter);

        mSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected user: " + mUserList.get(position).toString());
            }
        });
    }

    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * BottomNavigationView setup
     */
    public void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);

    }
}
