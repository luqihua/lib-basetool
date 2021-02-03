package com.lu.tool.widget.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import lu.basetool.R;


/**
 * Created by luqihua on 2016/9/30.
 */
public class LDialogBuilder {

    private Context mContext;
    private Dialog dialog;
    private float widthRadio;//对话框宽度占屏幕的百分比

    private CharSequence mTitle = "";
    private float mTitleSize = 12;

    private CharSequence mContent = "";
    private float mContentSize = 16;
    private boolean mCenter = true;
    private boolean mCanCancelAble = true;
    private boolean mCanceledOnTouchOutside = true;

    private BtnClickListener mPositiveListener;
    private BtnClickListener mNegativeListener;
    private String mPositive = "确定", mNegative = "取消";
    private int backGroundColor = Color.WHITE;

    public LDialogBuilder(Context context) {
        this.mContext = context;
    }

    public LDialogBuilder setTitle(CharSequence title, float size) {
        this.mTitleSize = size;
        this.mTitle = title;
        return this;
    }

    public LDialogBuilder setTitle(CharSequence title) {
        return setTitle(title, 16);
    }

    public LDialogBuilder setContent(CharSequence content, float size, boolean center) {
        this.mCenter = center;
        this.mContentSize = size;
        this.mContent = content;
        return this;
    }

    public LDialogBuilder setContent(CharSequence content) {
        return setContent(content, 16, true);
    }

    public LDialogBuilder setWidthRadio(float widthRadio) {
        this.widthRadio = widthRadio;
        return this;
    }

    public LDialogBuilder setCanCancelAble(boolean canCancelAble) {
        this.mCanCancelAble = canCancelAble;
        return this;
    }

    public LDialogBuilder setCanceledOnTouchOutside(boolean cancel) {
        this.mCanceledOnTouchOutside = cancel;
        return this;
    }

    public LDialogBuilder setBackGroundColor(int backGroundColor) {
        this.backGroundColor = backGroundColor;
        return this;
    }

    public LDialogBuilder setPositiveText(String positive){
        this.mPositive = positive;
        return this;
    }

    public LDialogBuilder setNegativeText(String negative){
        this.mNegative = negative;
        return this;
    }


    public LDialogBuilder setPositiveClickListener(BtnClickListener listener) {
        this.mPositiveListener = listener;
        return this;
    }

    public LDialogBuilder setNegativeClickListener(BtnClickListener listener) {
        this.mNegativeListener = listener;
        return this;
    }
    public Dialog build() {
        View contentView = getRootView();
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (widthRadio != 0) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams((int) (getScreenWidth() * widthRadio)
                    , ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setContentView(contentView, params);

        } else {
            dialog.setContentView(contentView);
        }
        dialog.setCancelable(mCanCancelAble);
        dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);
        return dialog;
    }

    private View getRootView() {
        LinearLayout rootView = new LinearLayout(mContext);
        rootView.setBackgroundColor(backGroundColor);
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setGravity(Gravity.CENTER);
        /*--标题--*/
        if (!TextUtils.isEmpty(mTitle)) {
            rootView.addView(createTitleView());
            rootView.addView(getLine(Color.LTGRAY, LinearLayout.VERTICAL, 5));
            rootView.setGravity(Gravity.LEFT);
        }
        /*--内容--*/
        rootView.addView(createContentView());
        /*--底部导航按键--*/
        rootView.addView(getLine(Color.LTGRAY, LinearLayout.VERTICAL, 0));
        rootView.addView(createNavigation());
        return rootView;
    }

    /**
     * 创建标题
     * @return
     */
    private View createTitleView() {
        TextView v = new TextView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setGravity(Gravity.CENTER);
        v.setPadding(10, 10, 10, 10);
        v.setText(mTitle);
        v.setTextSize(mTitleSize);
        v.setLayoutParams(params);
        return v;
    }

    /**
     * 创建内容展示模块
     * @return
     */
    private View createContentView() {
        TextView v = new TextView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setPadding(20, 40, 20, 40);
        v.setText(mContent);
        v.setTextSize(mContentSize);
        if (mCenter)
            v.setGravity(Gravity.CENTER);
        v.setLayoutParams(params);
        return v;
    }

    /**
     * 创建底部按钮模块
     * @return
     */
    private View createNavigation() {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        if (!TextUtils.isEmpty(mNegative) && !TextUtils.isEmpty(mPositive)) {
            layout.addView(createNavBtn(mNegative));
            layout.addView(getLine(Color.LTGRAY, LinearLayout.HORIZONTAL, 0));
            layout.addView(createNavBtn(mPositive));
        } else {
            if (!TextUtils.isEmpty(mNegative)) {
                layout.addView(createNavBtn(mNegative));
            }
            if (!TextUtils.isEmpty(mPositive)) {
                layout.addView(createNavBtn(mPositive));
            }

        }
        return layout;
    }

    /**
     * 创建确定和取消按钮
     * @param tip
     * @return
     */
    private View createNavBtn(String tip) {
        TextView v = new TextView(mContext);
        v.setText(tip);
        v.setBackground(createStateList());
        v.setGravity(Gravity.CENTER);
        v.setPadding(0, 30, 0, 30);
        v.setTextSize(16);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        v.setLayoutParams(params);

        if (tip.equals(mPositive)) {
            v.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog.isShowing()) dialog.dismiss();
                    if (mPositiveListener != null) {
                        mPositiveListener.click(dialog);
                    }
                }
            });
        } else if (tip.equals(mNegative)) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog.isShowing()) dialog.dismiss();
                    if (mNegativeListener != null) {
                        mNegativeListener.click(dialog);
                    }
                }
            });
        }
        return v;
    }

    private Drawable createStateList() {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(Color.LTGRAY));
        drawable.addState(new int[]{}, new ColorDrawable(Color.WHITE));
        return drawable;
    }

    private View getLine(int color, int orientation, int margin) {
        int lineColor = color == 0 ? ContextCompat.getColor(mContext, R.color.colorAccent) : color;
        View v = new View(mContext);
        v.setBackgroundColor(lineColor);
        LinearLayout.LayoutParams params;
        if (orientation == LinearLayout.VERTICAL) {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            params.setMargins(margin, 0, margin, 0);
        } else {
            params = new LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0, margin, 0, margin);
        }
        v.setLayoutParams(params);
        return v;
    }

    private int getScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public interface BtnClickListener {
        void click(Dialog dialog);
    }

}
