package com.lu.tool.app.update;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import androidx.core.content.FileProvider;
import android.widget.Toast;

import com.google.gson.JsonIOException;
import com.lu.tool.app.BaseApp;
import com.lu.tool.util.ToastUtil;

import java.io.File;

/**
 * author: luqihua
 * date:2019-07-17
 * description:
 **/
public abstract class AUpdateTools {
    private static final String FILE_PROVIDER = "%s.fileProvider";//fileProvider
    private Activity mActivity;

    private boolean mIsCancel;

    private ProgressDialog mDownloadDialog;
    private DownloadTools mDownloadTools;

    private IVersionInfo mVersionInfo;

    public AUpdateTools(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 检测是否有版本需要升级
     */
    public void checkUpdate() {

        final String url = getUpdateUrl();

        mIsCancel = false;

        new HttpConnectionTools()
                .get(url, new HttpConnectionTools.IHttpCallback() {
                    @Override
                    public void onHttpSuccess(String response) {
                        try {
                            mVersionInfo = parseVersionData(response);

                        } catch (JsonIOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onHttpError(String errorMsg) {
                        Toast.makeText(mActivity, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * 确认更新
     */
    protected void confirmUpdate() {
        startDownFile();
    }

    /**
     * 取消更新
     */
    protected void cancelUpdate() {
        mActivity.finish();
    }

    /**
     * 下载apk
     */
    private void startDownFile() {
        //显示下载框
        showDownloadD();
        mDownloadTools = new DownloadTools(mActivity, mVersionInfo.getApkUrl());
        mDownloadTools.setNotificationTitle(mActivity.getPackageName() + "-" + mVersionInfo.getVersionName() + ".apk");
        mDownloadTools.setCallback(new DownloadTools.DownloadCallback() {
            @Override
            public void onProgress(int progress) {
                mDownloadDialog.setProgress(progress);
            }

            @Override
            public void onComplete(String path) {
                mDownloadDialog.dismiss();
                if (!mIsCancel) {
                    installApk(path);
                    return;
                }
                ToastUtil.showShort("下载失败");
                mActivity.finish();
            }
        });
        mDownloadTools.start();
    }

    /**
     * 显示正在下载的对话框
     */
    private void showDownloadD() {

        mDownloadDialog = new ProgressDialog(mActivity);
        mDownloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDownloadDialog.setCancelable(false);
        mDownloadDialog.setMax(100);
        mDownloadDialog.setButton(Dialog.BUTTON_NEGATIVE, "取消"
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mIsCancel = true;
                        mDownloadTools.stop();
                        mDownloadDialog.dismiss();
                    }
                });
        mDownloadDialog.show();
    }

    /**
     * 安装apk
     *
     * @param path
     */
    private void installApk(String path) {
        File apkFile = new File(path);
        if (!apkFile.exists())
            return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(mActivity
                    , String.format(FILE_PROVIDER, BaseApp.getApplication().getPackageName())
                    , apkFile);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            mActivity.startActivity(intent);
        } else {
            Uri apkUri = Uri.fromFile(apkFile);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            mActivity.startActivity(intent);
            Process.killProcess(Process.myPid());
        }
    }

    /*================================*/

    //版本检测地址
    protected abstract String getUpdateUrl();

    //解析版本信息  类对象必须实现IVersionInfo 接口
    protected abstract IVersionInfo parseVersionData(String response);


    //构建新版本提示Dialog  确认按钮回调confirmUpdate()   取消按钮回调cancelUpdate()
    protected abstract Dialog getUpdateNotifyDialog(Context context, String versionName, String versionDesc);


}
