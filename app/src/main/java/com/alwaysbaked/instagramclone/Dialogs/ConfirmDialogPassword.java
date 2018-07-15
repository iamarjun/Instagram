package com.alwaysbaked.instagramclone.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alwaysbaked.instagramclone.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmDialogPassword extends DialogFragment {
    private static final String TAG = "ConfirmDialogPassword";

    public interface OnConfirmpasswordListener{
        public void confirmPassword(String password);
    }

    private OnConfirmpasswordListener mOnConfirmpasswordListener;

    @BindView(R.id.tvDialogCancel)
    TextView mCancel;
    @BindView(R.id.tvDialogConfirm)
    TextView mConfirm;
    @BindView(R.id.confirm_password)
    TextView mConfirmPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_password, container, false);
        Log.d(TAG, "onCreateView: ");

        ButterKnife.bind(this, view);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the dialog box.");
                getDialog().dismiss();
            }
        });
        
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: captured password and confirming.");

                String password = mConfirmPassword.getText().toString();
                if (!password.equals("")) {
                    mOnConfirmpasswordListener.confirmPassword(password);
                    getDialog().dismiss();
                }

                else
                    Toast.makeText(getActivity(), "Enter password to confirm email change", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mOnConfirmpasswordListener = (OnConfirmpasswordListener) getTargetFragment();
        }catch (ClassCastException e) {
            Log.d(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
