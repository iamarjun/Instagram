package com.alwaysbaked.instagramclone.Utils;

import android.os.Environment;

public class FilePaths {

    //"storage/emulated/0"
    public String ROOT_DIR = Environment.getDownloadCacheDirectory().getPath();
    public String CAMERA = ROOT_DIR + "/DCIM/Camera";
    public String PICTURES = ROOT_DIR + "/Pictures ";
}
