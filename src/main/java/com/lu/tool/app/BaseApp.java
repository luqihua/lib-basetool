package com.lu.tool.app;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.lu.tool.util.ActivityStackUtil;
import com.lu.tool.util.FileUtil;
import com.lu.tool.util.ResourceUtil;
import com.lu.tool.util.SystemUtil;
import com.lu.tool.util.ToastUtil;

/**
 * 基础Application,一些初始化操作放在这里执行
 *
 * @author lqh
 * @hide
 */
public class BaseApp extends Application {

    protected static Application sInstance;
    /*---当前版本名称------*/
    public static String sVersionName;
    /*---当前版本号-------*/
    public static int sVersionCode;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        if (SystemUtil.isCurrentProcess(this)) {
            /*初始化版本信息*/
            initVersionInfo();
            /*初始化小图标库*/
            initIconify();
            /*初始化toast通知*/
            ToastUtil.init(this);
            /*初始化文件工具*/
            FileUtil.init(this);
            ResourceUtil.init(this);
            ActivityStackUtil.getInstance().init(this);
        }
    }

    public static <T> T getService(String name) {
        Object o = sInstance.getSystemService(name);
        return (T) o;
    }

    public static Application getApplication() {
        return sInstance;
    }

    /**
     * 获取当前版本信息
     */
    private void initVersionInfo() {
        if (TextUtils.isEmpty(sVersionName)) {
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                sVersionName = packageInfo.versionName;
                sVersionCode = packageInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    private void initIconify() {
        Iconify.with(new FontAwesomeModule());
    }
}
