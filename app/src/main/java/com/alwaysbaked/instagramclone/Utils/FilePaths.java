package com.alwaysbaked.instagramclone.Utils;

import android.os.Environment;

public class FilePaths {
    private static final String TAG = "FilePaths";

    //"storage/emulated/0"
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();
    public String CAMERA = ROOT_DIR + "/DCIM/Camera";
    public String PICTURES = ROOT_DIR + "/Pictures";
    public String DOWNLOAD = ROOT_DIR + "/Download";

    //firebase storage path
    public String FIREBASE_IMAGE_STORAGE = "photos/users/";

}
