package com.lu.tool.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.DrawableRes;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import lu.basetool.R;

/**
 * Created by lqh on 2016/5/18.
 */
public class ToastUtil {

    private static Toast mToast;
    private static Handler handler = new Handler(Looper.getMainLooper());

    private ToastUtil() {
        throw new UnsupportedOperationException("ToastUtil: cannot be instantiated");
    }


    @SuppressLint("ShowToast")
    public static void init(Context context) {
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
    }

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(final String message) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            show(message, Toast.LENGTH_SHORT);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    show(message, Toast.LENGTH_SHORT);
                }
            });
        }

    }

    public static void showLong(final String message) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            show(message, Toast.LENGTH_LONG);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    show(message, Toast.LENGTH_LONG);
                }
            });
        }
    }

    public static void show(String message, int time) {
        if (message == null || message.length() == 0) return;
        if (mToast == null) {
            throw new RuntimeException("please call ToastUtil.init() first");
        }
        mToast.setText(message);
        mToast.setDuration(time);
        mToast.show();
    }


    public static void showWithCustomView(Context context, CharSequence text, @DrawableRes int resId) {
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        //设置自定义的view
        toast.setView(createCustomView(context, text, resId));
        toast.show();
    }


    private static View createCustomView(Context context, CharSequence msg, @DrawableRes int resId) {
        View view = LayoutInflater.from(context).inflate(R.layout.toast_custom_icon, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.toast_icon);
        imageView.setImageResource(resId);

        TextView toastText = (TextView) view.findViewById(R.id.toast_text);
        toastText.setText(msg);

        return view;
    }
}
