package com.lu.tool.app.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

/**
 * author: luqihua
 * date:2019-06-05
 * description: 下载文件
 **/
public class DownloadTools {
    private DownloadManager mDownloadManager;
    private long mDownloadId;
    private Context mContext;
    private String mDownloadUrl;
    private String mDestinationPath;
    private DownloadCallback mCallback;
    private final String mFileName;
    private String mNotifyTitle;

    public DownloadTools(Context context, String downloadUrl) {
        this.mContext = context;
        this.mDownloadUrl = downloadUrl;
        this.mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //文件名
        mFileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
    }

    //下载变化监听
    private ContentObserver mDownloadObserver = new ContentObserver(new Handler(Looper.myLooper())) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            onProgress();
        }
    };

    //下载完成
    private BroadcastReceiver mCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onComplete();
        }
    };


    public void start() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mDownloadUrl));

        //默认将apk文件下载到Android/data/package-name/files/Download/
        mDestinationPath = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + mFileName;
        request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, mFileName)
        //通知栏标题
        .setTitle(mNotifyTitle)
        //通知栏描述信息
//        .setDescription("download-desc")
        //设置类型为.apk
        .setMimeType("application/vnd.android.package-archive")
        // 设置为可见和可管理
        .setVisibleInDownloadsUi(true)
        //下载完成后通知
//        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();

        mDownloadId = mDownloadManager.enqueue(request);

        mContext.getContentResolver()
                .registerContentObserver(Uri.parse("content://downloads/my_downloads")
                        , true, mDownloadObserver);
        mContext.registerReceiver(mCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    public void stop() {
        mContext.getContentResolver().unregisterContentObserver(mDownloadObserver);
        mContext.unregisterReceiver(mCompleteReceiver);
        mDownloadManager.remove(mDownloadId);
    }


    /**
     * 处理下载进度
     */
    private void onProgress() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mDownloadId);

        Cursor cursor = mDownloadManager.query(query);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                //当前下载量
                int byte_so_far = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //总 大小
                int byte_total = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                //下载状态
//                int download_status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
//                Log.d("TestDownloadManagerActi", "当前下载：" + byte_so_far + "  总大小：" + byte_total + "   下载状态" + download_status);

                if (mCallback != null) {
                    mCallback.onProgress(byte_so_far * 100 / byte_total);
                }
            } finally {
                cursor.close();
            }
        }
    }

    /**
     * 处理下载完成
     */
    private void onComplete() {
        mContext.getContentResolver().unregisterContentObserver(mDownloadObserver);
        mContext.unregisterReceiver(mCompleteReceiver);
        if (mCallback != null) {
            mCallback.onComplete(mDestinationPath);
        }
    }
    /**
     * 设置下载监听
     *
     * @param callback
     */
    public void setCallback(DownloadCallback callback) {
        this.mCallback = callback;
    }


    public void setNotificationTitle(String title) {
        mNotifyTitle = title;
    }


    interface DownloadCallback {

        void onProgress(int progress);

        void onComplete(String path);
    }
}
