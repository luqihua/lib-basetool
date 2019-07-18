package com.lu.tool.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * 本应用数据清除管理器
 *
 * @author lqh
 */
public class AppCacheUtil {
    /**
     * 应用程序内部缓存+外部缓存大小
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static String getTotalCacheSize(Context context) throws Exception {
        long cacheSize = FileUtil.getFileSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += FileUtil.getFileSize(context.getExternalCacheDir());
        }
        return FileUtil.getFormatSize(cacheSize);
    }

    /**
     * 清楚app缓存
     *
     * @param context
     */
    public static void clearAllCache(Context context) {
        FileUtil.deleteFile(context.getCacheDir(), false);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileUtil.deleteFile(context.getExternalCacheDir(), false);
        }
    }

    /**
     * * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * *
     *
     * @param context
     */
    public static void cleanInternalCache(Context context) {
        FileUtil.deleteFile(context.getCacheDir(), false);
    }

    /**
     * * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * *
     *
     * @param context
     */
    public static void cleanDatabases(Context context) {
        FileUtil.deleteFile(new File("/data/data/"
                + context.getPackageName() + "/databases"), false);
    }

    /**
     * * 按名字清除本应用数据库 * *
     *
     * @param context
     * @param dbName
     */
    public static void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) *
     *
     * @param context
     */
    public static void cleanSharedPreference(Context context) {
        FileUtil.deleteFile(new File("/data/data/"
                + context.getPackageName() + "/shared_prefs"), false);
    }

    /**
     * * 清除/data/data/com.xxx.xxx/files下的内容 * *
     *
     * @param context
     */
    public static void cleanFiles(Context context) {
        FileUtil.deleteFile(context.getFilesDir(), false);
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     *
     * @param context
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            FileUtil.deleteFile(context.getExternalCacheDir(), false);
        }
    }

    /**
     * * 清除本应用所有的数据 * *
     *
     * @param context
     */
    public static void cleanApplicationData(Context context) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
    }

}