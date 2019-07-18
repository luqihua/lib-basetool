package com.lu.tool.widget.banner.indicator;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import java.util.Locale;

import lu.basetool.R;

/**
 * author: luqihua
 * date:2018/7/16
 * description:
 **/
public class NumberIndicator extends AppCompatTextView implements IIndicator {

    public static final String INDICATOR_NUM_FORMAT = "%d/%d";
    private int mCount;

    public NumberIndicator(Context context) {
        this(context, null);
    }

    public NumberIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        iniView();
    }

    private void iniView() {
        setBackgroundResource(R.drawable.bg_indicator_number);
        setTextColor(Color.WHITE);
        setPadding(10, 5, 10, 5);
    }

    @Override
    public void initIndicator(int count, int index) {
        this.mCount = count;
        setText(String.format(Locale.CHINA, INDICATOR_NUM_FORMAT, index + 1, mCount));
    }

    @Override
    public void onPageScrolled(int position, float offset, int offsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setText(String.format(Locale.CHINA, INDICATOR_NUM_FORMAT, position + 1, mCount));
    }

    @Override
    public void onPageScrollStateChanged(int position) {

    }

}
