package com.lu.tool.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Author: luqihua
 * Time: 2017/12/22
 * Description: FileUtil
 */

public class FileUtil {
    private static final String CACHE_DIR_IMAGE = "cache_dir_image";//缓存图片的文件夹

    private static String sAppCacheDir = "";
    private static boolean hasInitialize = false;

    /**
     * 初始化app外部存储跟目录，以包名为文件夹名(这一步不创建文件)
     *
     * @param context
     */
    public static void init(Context context) {
        if (!hasInitialize) {
            if (context == null) {
                throw new RuntimeException("FileUtil: context can't be null");
            }
            hasInitialize = true;
            String rootDir = context.getPackageName();
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                sAppCacheDir = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/" + rootDir;
            } else {
                sAppCacheDir = context.getCacheDir().getAbsolutePath() + "/" + rootDir;
            }
        }
    }

    /**
     * 获取已存在的文件
     *
     * @param dir
     * @param filename
     * @return
     */
    public static File getExistFile(String dir, String filename) {
        if (!hasInitialize) {
            throw new RuntimeException("FileUtil : doest has initialize ");
        }

        String dirPath = checkPath(dir);
        File dirFile = new File(sAppCacheDir + dirPath);
        if (!dirFile.exists()) {
            return null;
        }
        File file = new File(dirFile, filename);
        if (!file.exists()) {
            return null;
        }
        return file;
    }


    /**
     * 创建一个缓存文件夹
     *
     * @param cacheDir 路径
     * @return
     */
    public static File newCacheDir(String cacheDir) {
        if (!hasInitialize) {
            throw new RuntimeException("FileUtil : doest has initialize ");
        }

        String dirPath = checkPath(cacheDir);

        File file = new File(sAppCacheDir + dirPath);

        if (!file.exists()) {
            if (file.mkdirs()) {
                return file;
            }
        } else {
            return file;
        }
        return null;
    }

    /**
     * 创建一个普通缓存文件
     *
     * @param paths xxxx/xxxx/xxxxx/xxxx.txt
     * @return
     */
    public static File newCacheFile(String... paths) {
        final StringBuilder dirPath = new StringBuilder(sAppCacheDir);

        int len = paths.length;
        for (int i = 0; i < len - 1; i++) {
            dirPath.append("/").append(paths[i]);
        }
        File dir = new File(dirPath.toString());
        if (!dir.exists())
            dir.mkdirs();
        return new File(dir, paths[len - 1]);
    }

    /**
     * 创建一个新的图片文件
     *
     * @param format 图片格式
     * @param paths  子目录 可不填写
     * @return
     */
    public static File newCacheImageFile(Bitmap.CompressFormat format, String... paths) {
        final StringBuilder dirPath = new StringBuilder(sAppCacheDir)
                .append("/")
                .append(CACHE_DIR_IMAGE);
        for (String path : paths) {
            dirPath.append("/").append(path);
        }
        File dir = new File(dirPath.toString());
        if (!dir.exists())
            dir.mkdirs();
        return new File(dir, "cache_" + UUID.randomUUID() + "." + format.toString().toLowerCase());
    }

    /**
     * 清除图片缓存
     *
     * @param paths
     */
    public static void deleteImageCache(String... paths) {
        final StringBuilder dirPath = new StringBuilder(sAppCacheDir)
                .append("/").append(CACHE_DIR_IMAGE);
        for (String path : paths) {
            dirPath.append("/").append(path);
        }
        File file = new File(dirPath.toString());
        deleteFile(file, true);
    }

    /**
     * 清除普通文件缓存
     *
     * @param paths
     */
    public static void deleteFileCache(String... paths) {
        final StringBuilder dirPath = new StringBuilder(sAppCacheDir);
        for (String path : paths) {
            dirPath.append("/").append(path);
        }
        File file = new File(dirPath.toString());
        deleteFile(file, true);
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param file
     * @param deleteThisPath 是否删除当前目录
     */
    public static boolean deleteFile(File file, boolean deleteThisPath) {

        if (!file.exists()) {
            return true;
        }

        boolean result = true;
        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                result &= deleteFile(childFile, true);
            }
        }
        if (deleteThisPath)
            result &= file.delete();
        return result;
    }

    /**
     * @param file
     * @return
     */
    public static long getFileSize(File file) {
        if (!file.exists()) return 0;
        long size = 0;
        if (file.isFile()) {
            size = size + file.length();
        } else {
            for (File childFile : file.listFiles()) {
                size += getFileSize(childFile);
            }
        }
        return size;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {

        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "B";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }


    /**
     * 生成 content:// 类型uri
     */
    public static Uri path2ContentUri(Context context, String filePath) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            return context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    /**
     * uri提取文件path
     *
     * @param context
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    public static String contentUri2Path(Context context, Uri uri, String selection,
                                         String[] selectionArgs) {
        if (uri.getScheme().contains("file")) {
            return uri.getPath();
        } else if (uri.getScheme().equals("content")) {

            Cursor cursor = null;
            final String column = MediaStore.Images.Media.DATA;
            final String[] projection = {column};

            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(column_index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
        return null;
    }


    /**
     * 检查文件路径
     * @param originalPath
     * @return
     */
    private static String checkPath(String originalPath) {
        if (originalPath == null) return null;
        originalPath = originalPath
                .trim()
                .replaceAll("//", "/");
        if (!originalPath.startsWith("/")) {
            originalPath = "/" + originalPath;
        }
        return originalPath;
    }
}
