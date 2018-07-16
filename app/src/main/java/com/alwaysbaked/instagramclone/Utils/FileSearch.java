package com.alwaysbaked.instagramclone.Utils;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class FileSearch {
    private static final String TAG = "FileSearch";

    /**
     * search a directory an return a list of all **directories** contained.
     * @param directory
     * @return
     */
    public static ArrayList<String> getDirectoryPaths(String directory){
        Log.d(TAG, "getDirectoryPaths: accessing " + directory);
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();
        for (File listFile : listFiles) {
            if (listFile.isDirectory()) {
                pathArray.add(listFile.getAbsolutePath());
            }
        }
        return pathArray;
    }

    /**
     * search a directory an return a list of all **files** contained.
     * @param directory
     * @return
     */
    public static ArrayList<String> getFilePath(String directory){
        Log.d(TAG, "getFilePath: accessing" + directory );
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();
        for (File listFile : listFiles) {
            if (listFile.isFile()) {
                pathArray.add(listFile.getAbsolutePath());
            }
        }
        return pathArray;
    }
}
