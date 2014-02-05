package com.vivavu.dream.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by yuja on 14. 1. 24.
 */
public class ImageUtil {
    public static Bitmap getImageFromUrl(String strUrl){
        try {
            URL url = new URL(strUrl);
            URLConnection conn = url.openConnection();
            conn.connect();
            BufferedInputStream bis = new
                    BufferedInputStream(conn.getInputStream());
            Bitmap bm = BitmapFactory.decodeStream(bis);
            bis.close();
            return bm;
        } catch (IOException e) {
            Log.e("dream", e.toString());
        }
        return null;
    }
}
