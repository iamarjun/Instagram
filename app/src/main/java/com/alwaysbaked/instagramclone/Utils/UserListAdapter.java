package com.alwaysbaked.instagramclone.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alwaysbaked.instagramclone.Models.User;
import com.alwaysbaked.instagramclone.Models.UserAccountSettings;
import com.alwaysbaked.instagramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends ArrayAdapter<User> {
    private static final String TAG = "UserListAdapter";

    private LayoutInflater mInflater;
    private List<User> mUsers;
    private int layoutResource;
    private Context mContext;

    public UserListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
        this.mUsers = objects;
    }

    private static class ViewHolder {
        TextView mUsername, mEmail;
        CircleImageView mProfilePhoto;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.mUsername = convertView.findViewById(R.id.tvUsername);
            viewHolder.mEmail = convertView.findViewById(R.id.tvName);
            viewHolder.mProfilePhoto = convertView.findViewById(R.id.profile_photo);

            convertView.setTag(viewHolder);

        } else  {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        viewHolder.mUsername.setText(getItem(position).getUsername());
        viewHolder.mEmail.setText(getItem(position).getEmail());

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

        Query query = mRef.child(mContext.getString(R.string.dbname_users_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user: " + ds.getValue(UserAccountSettings.class).toString());

                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(ds.getValue(UserAccountSettings.class).getProfile_photo(), viewHolder.mProfilePhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return convertView;
    }
}
