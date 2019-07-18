package com.lu.tool.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;


/**
 * <工具类>
 */

public class ResourceUtil {
    private static Resources sResources;

    public static void init(Context context){
        sResources = context.getResources();
    }

    public static int getColor(int resId) {
        return sResources.getColor(resId);
    }

    public static Drawable getDrawable(int resId) {
        return sResources.getDrawable(resId);
    }

    public static String getString(int resId) {
        return sResources.getString(resId);
    }

    public static String getString(int resId, Object... objects) {
        return sResources.getString(resId, objects);
    }

    public static String[] getStringArray(int resId) {
        return sResources.getStringArray(resId);
    }

    public static InputStream openAssets(String fileName) throws IOException {
        return sResources.getAssets().open(fileName);
    }
}
