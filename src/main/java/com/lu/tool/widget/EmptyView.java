package com.lu.tool.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lu.tool.util.DimensionTools;

import lu.basetool.R;

/**
 * @author luzeyan
 * @time 2017/11/6 上午10:50
 * @description
 */


public class EmptyView extends FrameLayout implements View.OnClickListener {

    private final int DEFAULT_TEXT_COLOR = 0xff999999;
    private final int DEFAULT_TEXT_SIZE = 16;
    private final boolean DEFAULT_REFRESH_ENABLE = false;
    private final int DEFAULT_EMPTY_ICON = 0;
    private final String DEFAULT_EMPTY_TEXT = "";

    private boolean mRefreshEnable = DEFAULT_REFRESH_ENABLE;
    private int mEmptyTextColor = DEFAULT_TEXT_COLOR;
    private int mEmptyTextSize = DEFAULT_TEXT_SIZE;
    private int mEmptyIcon = DEFAULT_EMPTY_ICON;
    private String mEmptyText = DEFAULT_EMPTY_TEXT;

    private Context mContext;

    private OnRefreshClickListener mRefreshClickListener;
    private ImageView mIconIv;
    private TextView mEmptyTv;
    private TextView mRefreshTv;

    public void setOnRefreshClickListener(OnRefreshClickListener refreshClickListener) {
        mRefreshClickListener = refreshClickListener;
    }

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.EmptyView);
            mEmptyText = array.getString(R.styleable.EmptyView_emptyText);
            mRefreshEnable = array.getBoolean(R.styleable.EmptyView_refreshEnable, DEFAULT_REFRESH_ENABLE);
            mEmptyTextColor = array.getColor(R.styleable.EmptyView_emptyTextColor, DEFAULT_TEXT_COLOR);
            mEmptyTextSize = array.getInt(R.styleable.EmptyView_emptyTextSize, DEFAULT_TEXT_SIZE);
            mEmptyIcon = array.getResourceId(R.styleable.EmptyView_emptyIcon, DEFAULT_EMPTY_ICON);
            array.recycle();
        }

        initView();
    }

    private void initView() {
        addView(createContainer());
    }


    public LinearLayout createContainer() {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        //设置LayoutParams
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        layout.setLayoutParams(params);


        //添加空Icon
        mIconIv = createEmptyIcon();
        layout.addView(mIconIv);
        //添加空Text
        mEmptyTv = createEmptyText(mEmptyText);
        layout.addView(mEmptyTv);
        //添加刷新按钮
        mRefreshTv = createRefreshBtn();
        layout.addView(mRefreshTv);
        mRefreshTv.setVisibility(mRefreshEnable?VISIBLE:GONE);

        return layout;
    }

    public ImageView createEmptyIcon() {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(mEmptyIcon);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
        imageView.setLayoutParams(params);
        return imageView;
    }

    public TextView createEmptyText(String text) {
        TextView textView = new TextView(mContext);
        textView.setGravity(Gravity.CENTER);
        textView.setText(text);
        textView.setTextSize(mEmptyTextSize);
        textView.setTextColor(mEmptyTextColor);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (DimensionTools.getScreenWidth() * 0.6), -2);
        params.topMargin = 20;
        textView.setLayoutParams(params);
        return textView;
    }

    public TextView createRefreshBtn() {
        TextView textView = new TextView(mContext);
        textView.setText("刷新");
        textView.setTextColor(mEmptyTextColor);
        textView.setPadding(20, 3, 20, 3);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(1, DEFAULT_TEXT_COLOR);
        drawable.setCornerRadius(20f);

        textView.setBackground(drawable);
        textView.setOnClickListener(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
        params.topMargin = 20;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        textView.setLayoutParams(params);
        return textView;
    }


    public void setRefreshEnable(boolean refreshEnable) {
        mRefreshEnable = refreshEnable;
        mRefreshTv.setVisibility(refreshEnable ? VISIBLE : GONE);
    }

    public void setEmptyTextColor(int emptyTextColor) {
        mEmptyTextColor = emptyTextColor;
        mEmptyTv.setTextColor(emptyTextColor);
    }

    public void setEmptyTextSize(int emptyTextSize) {
        mEmptyTextSize = emptyTextSize;
        mEmptyTv.setTextSize(emptyTextSize);
    }

    public void setEmptyIcon(@DrawableRes int emptyIcon) {
        mEmptyIcon = emptyIcon;
        if (mIconIv != null) {
            mIconIv.setImageResource(mEmptyIcon);
        }
    }

    public void setEmptyText(String emptyText) {
        mEmptyText = emptyText;
        if (mEmptyTv != null) {
            mEmptyTv.setText(mEmptyText);
        }
    }

    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        if (mRefreshClickListener != null) {
            mRefreshClickListener.onRefresh();
        }
    }

    public interface OnRefreshClickListener {
        /**
         * 点击刷新
         */
        void onRefresh();
    }
}
