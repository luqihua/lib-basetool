package com.lu.tool.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;
import com.wang.avi.indicators.LineSpinFadeLoaderIndicator;

import lu.basetool.R;

/**
 * Author: luqihua
 * Time: 2017/9/20
 * Description: 加载提示圈圈
 */

public class LoadingDialog extends Dialog {

    private final int DEFAULT_PADDING = 12;
    private final int DEFAULT_SIZE = 80;
    private Context mContext;
    private TextView tvMsgV;
    private AVLoadingIndicatorView ivLoadingImageV;
    private boolean outsideCloseEnable = false;

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.dialog_loading);
        this.mContext = context;
    }

    @Override
    public void onAttachedToWindow() {
        Log.d("LoadingDialog", "attach");
        if (ivLoadingImageV != null) ivLoadingImageV.show();
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        Log.d("LoadingDialog", "detach");
        if (ivLoadingImageV != null) ivLoadingImageV.hide();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(createContentView());
        setCanceledOnTouchOutside(outsideCloseEnable);
    }

    //创建主布局
    public LinearLayout createContentView() {
        LinearLayout layout = new LinearLayout(mContext);

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
        layout.setBackground(createBackground());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);


        tvMsgV = createLoadingText();
        ivLoadingImageV = createLoadingImage();

        layout.addView(ivLoadingImageV);
        layout.addView(tvMsgV);


        return layout;
    }

    //创建加载圆圈
    public AVLoadingIndicatorView createLoadingImage() {
        AVLoadingIndicatorView loadingIndicatorView = new AVLoadingIndicatorView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DEFAULT_SIZE, DEFAULT_SIZE);
        loadingIndicatorView.setLayoutParams(params);

        loadingIndicatorView.setIndicator(new LineSpinFadeLoaderIndicator());
        loadingIndicatorView.setIndicatorColor(Color.WHITE);
        return loadingIndicatorView;
    }

    //创建加载文字
    public TextView createLoadingText() {
        TextView view = new TextView(mContext);
        view.setTextColor(Color.WHITE);
        view.setGravity(Gravity.CENTER);
        view.setTextSize(10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return view;
    }

    //主背景drawable
    private Drawable createBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(10);
        drawable.setColor(0xaa000000);
        return drawable;
    }

    public LoadingDialog setOutsideCloseEnable(boolean outsideCloseEnable) {
        this.outsideCloseEnable = outsideCloseEnable;
        return this;
    }

    /**
     * 不带提示语的加载圈
     */
    @Override
    public void show() {
        super.show();
        tvMsgV.setVisibility(View.GONE);
    }

    /**
     * 带提示语的加载圈
     *
     * @param text
     */
    public void show(String text) {
        this.show();
        if (!TextUtils.isEmpty(text)) {
            tvMsgV.setVisibility(View.VISIBLE);
            tvMsgV.setText(text);
        }
    }
}
