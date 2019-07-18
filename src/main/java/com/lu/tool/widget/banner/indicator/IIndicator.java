package com.lu.tool.widget.banner.indicator;

import android.view.ViewGroup;

/**
 * author: luqihua
 * date:2018/7/16
 * description:
 **/
public interface IIndicator {

    void setLayoutParams(ViewGroup.LayoutParams params);

    void initIndicator(int count, int index);

    void onPageScrolled(int position, float offset, int offsetPixels);

    void onPageSelected(int position);

    void onPageScrollStateChanged(int position);

}
