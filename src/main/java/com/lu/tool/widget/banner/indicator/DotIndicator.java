package com.lu.tool.widget.banner.indicator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
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
public class DotIndicator extends LinearLayout implements IIndicator{

    private Context mContext;

    private int mCount = -1;//总的数量
    private int mIndex = -1;//选中的下表

    private Drawable mNormalDotColor;
    private Drawable mSelectDotColor;

    private int mIndicatorSize = Constants.DEFAULT_INDICATOR_SIZE;

    public DotIndicator(Context context) {
        this(context, null);
    }

    public DotIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        this.mNormalDotColor = createBackground(Constants.DEFAULT_INDICATOR_COLOR);
        this.mSelectDotColor = createBackground(Constants.DEFAULT_INDICATOR_SELECT_COLOR);
    }

    private Drawable createBackground(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        return drawable;
    }

    private LayoutParams getChildLayoutParams(boolean isSelect) {
        LayoutParams params = new LayoutParams(mIndicatorSize, mIndicatorSize);
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
        if (normalColor!=-1){
            this.mNormalDotColor = createBackground(normalColor);
        }
        if (selectColor!=-1){
            this.mSelectDotColor = createBackground(selectColor);
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
            view.setLayoutParams(getChildLayoutParams(false));
            if (i == mIndex) {
                view.setBackground(mSelectDotColor);
            } else {
                view.setBackground(mNormalDotColor);
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
        getChildAt(mIndex).setBackground(mNormalDotColor);
        this.mIndex = position;
        getChildAt(mIndex).setBackground(mSelectDotColor);
    }

    @Override
    public void onPageScrollStateChanged(int position) {

    }

}
