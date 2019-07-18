package com.lu.tool.widget.banner;

import android.content.Context;
import android.widget.Scroller;

/**
 * 用于控制滚动时间的Scroller
 */
public class BannerScroller extends Scroller {
    private int mScrollTime = BannerConstants.DEFAULT_BANNER_SCROLL_TIME;

    public BannerScroller(Context context) {
        super(context);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mScrollTime);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mScrollTime);
    }

    public void setDuration(int time) {
        mScrollTime = time;
    }

}
