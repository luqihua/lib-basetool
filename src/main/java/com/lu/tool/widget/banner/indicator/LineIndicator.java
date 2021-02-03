package com.lu.tool.widget.banner.indicator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.lu.tool.widget.banner.Constants;


/**
 * author: luqihua
 * date:2018/7/13
 * description: 圆点指示器
 **/
public class LineIndicator extends LinearLayout implements IIndicator {

    private Context mContext;

    private int mCount = -1;//总的数量
    private int mIndex = -1;//选中的下表

    private Drawable mNormalColor;
    private Drawable mSelectColor;

    private int mIndicatorSize = Constants.DEFAULT_INDICATOR_SIZE;

    public LineIndicator(Context context) {
        this(context, null);
    }

    public LineIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        this.mNormalColor = createBackground(Constants.DEFAULT_INDICATOR_COLOR);
        this.mSelectColor = createBackground(Constants.DEFAULT_INDICATOR_SELECT_COLOR);
    }

    private Drawable createBackground(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(4);
        drawable.setColor(color);
        return drawable;
    }

    private LayoutParams getChildLayoutParams(boolean isSelect) {
        int width = isSelect ? mIndicatorSize : mIndicatorSize * 2;
        LayoutParams params = new LayoutParams(width, mIndicatorSize / 2);
        params.leftMargin = mIndicatorSize / 2;
        return params;
    }

    /**
     * 设置圆点的颜色
     *
     * @param normalColor 正常圆点的颜色
     * @param selectColor 选中圆点的颜色
     */
    public void setIndicatorColor(int normalColor, int selectColor) {
        if (normalColor != -1) {
            this.mNormalColor = createBackground(normalColor);
        }
        if (selectColor != -1) {
            this.mSelectColor = createBackground(selectColor);
        }
        if (mCount != -1) {
            initIndicator(mCount, mIndex);
        }
    }

    @Override
    public void initIndicator(int count, int index) {
        this.mCount = count;
        this.mIndex = index;
        removeAllViews();
        for (int i = 0; i < mCount; i++) {
            View view = new View(mContext);
            if (i == mIndex) {
                view.setLayoutParams(getChildLayoutParams(true));
                view.setBackground(mSelectColor);
            } else {
                view.setLayoutParams(getChildLayoutParams(false));
                view.setBackground(mNormalColor);
            }
            addView(view, i);
        }
    }

    @Override
    public void onPageScrolled(int position, float offset, int offsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mIndex == position) return;
        View view = getChildAt(mIndex);
        view.setLayoutParams(getChildLayoutParams(false));
        view.setBackground(mNormalColor);

        this.mIndex = position;
        view = getChildAt(mIndex);
        view.setLayoutParams(getChildLayoutParams(true));
        view.setBackground(mSelectColor);
    }

    @Override
    public void onPageScrollStateChanged(int position) {

    }
}
