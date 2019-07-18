package com.lu.tool.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

/**
 * 系统工具类
 * Created by lqh on 2016/01/01.
 */
public class DeviceUtil {

    private static final DisplayMetrics S_METRICS;

    static {
        S_METRICS = Resources.getSystem().getDisplayMetrics();
    }
    /**
     * @return 屏幕宽度(px)
     */
    @SuppressLint("NewApi")
    public static int getScreenWidth() {
        return S_METRICS.widthPixels;
    }

    /**
     * @return 获取屏幕高度(px)
     */
    @SuppressLint("NewApi")
    public static int getScreenHeight() {
        return S_METRICS.heightPixels;
    }


    public static int densityDpi() {
        return S_METRICS.densityDpi;
    }
    /**
     * 根据手机分辨率将dp的单位转换为px
     *
     * @param dpValue
     * @return
     */

    public static int dp2px(float dpValue) {
        final float scale = S_METRICS.density;
        return (int) (dpValue * scale + 0.5f);// 四舍五入
    }

    /**
     * 根据手机分辨率将px的单位转换为dp
     *
     * @param pxValue
     * @return
     */
    public static int px2dp(float pxValue) {
        final float scale = S_METRICS.density;

        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(float pxValue) {
        final float scale = S_METRICS.density;
        return (int) (pxValue / scale + 0.5);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue) {
        final float scale = S_METRICS.scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setStatusBarColor(Activity activity, int color) {
        Window win = activity.getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        View view = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));

        ViewGroup group = (ViewGroup) activity.getWindow().getDecorView();

        view.setLayoutParams(params);
        view.setBackgroundColor(color);
        group.addView(view);
    }

    public static void setNavigationColor(Activity activity) {

    }

    /**
     * 获取状态栏高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Context activity) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = activity.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 获取底部导航栏高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        if (!checkDeviceHasNavigationBar(context))
            return 0;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 判断手机是否有导航栏
     *
     * @param activity
     * @return
     */
    @SuppressLint("NewApi")
    private static boolean checkDeviceHasNavigationBar(Context activity) {

        //通过判断设备是否有返回键、菜单键来确定是否有navigation bar
        boolean hasMenuKey = ViewConfiguration.get(activity).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        return !hasMenuKey && !hasBackKey;

    }


}
