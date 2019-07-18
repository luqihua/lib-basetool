package com.lu.tool.app.update;

/**
 * author: luqihua
 * date:2019-07-17
 * description: 版本信息接口
 **/
public interface IVersionInfo {
    //apk下载地址
    String getApkUrl();

    //版本名称
    String getVersionName();

    //版本描述
    String getVersionDesc();

    //版本号
    int getVersionCode();
}
