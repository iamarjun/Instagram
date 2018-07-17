package com.alwaysbaked.instagramclone.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageManager {
    private static final String TAG = "ImageManager";

    public static Bitmap getBitmap(String imgURL) {
        File imageFile = new File(imgURL);
        FileInputStream fileInputStream = null;
        Bitmap bitmap = null;
        try {
            fileInputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fileInputStream);

        } catch (FileNotFoundException e) {
            Log.d(TAG, "getBitmap: FileNotFoundException: " + e.getMessage());

        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                Log.d(TAG, "getBitmap: IOException: " + e.getMessage());

            }
        }

        return bitmap;
    }

    /**
     * returns byte array from bitmap
     * quality is grater than 0 but less than 100
     * @param bitmap
     * @param quality
     * @return
     */
    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);

        return stream.toByteArray();
    }
}
