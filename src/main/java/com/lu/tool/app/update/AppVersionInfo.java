package com.lu.tool.app.update;

import androidx.annotation.Keep;

/**
 * 版本信息
 */
@Keep
public class AppVersionInfo implements IVersionInfo{

    private String url;
    private String versionName;
    private String versionDesc;
    private int versionCode;

    @Override
    public String getApkUrl() {
        return url;
    }

    public String getVersionName() {
        return versionName;
    }

    @Override
    public String getVersionDesc() {
        return versionDesc;
    }

    public int getVersionCode() {
        return versionCode;
    }

}