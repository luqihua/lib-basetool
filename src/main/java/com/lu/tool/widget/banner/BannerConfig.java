package com.lu.tool.widget.banner;

import android.support.v4.view.ViewPager;
import android.view.Gravity;

import com.lu.tool.widget.banner.indicator.IIndicator;


/**
 * author: luqihua
 * date:2018/7/14
 * description:轮播控制参数
 **/
public class BannerConfig {
    private int mInterval;//轮播间隔
    private int mScrollTime;//轮播单页滚动时长
    private int mIndicatorGravity;//指示器位置
    private boolean isAutoScroll;//是否自动轮播
    private IIndicator mIndicator;//指示器样式   可自定义
    private ViewPager.PageTransformer mBannerTransformer;//轮播动画

    public BannerConfig(Builder builder) {
        this.mInterval = builder.interval;
        this.mScrollTime = builder.scrollTime;
        this.mBannerTransformer = builder.bannerTransformer;
        this.mIndicatorGravity = builder.indicatorGravity;
        this.isAutoScroll = builder.isAutoScroll;
        this.mIndicator = builder.indicator;
    }

    public int getInterval() {
        return mInterval;
    }

    public int getScrollTime() {
        return mScrollTime;
    }

    public ViewPager.PageTransformer getBannerTransformer() {
        return mBannerTransformer;
    }

    public IIndicator getIndicator() {
        return mIndicator;
    }

    public int getIndicatorGravity() {
        return this.mIndicatorGravity;
    }


    public boolean isAutoScroll() {
        return isAutoScroll;
    }

    public static class Builder {
        int interval = BannerConstants.DEFAULT_BANNER_INTERVAL;
        int scrollTime = BannerConstants.DEFAULT_BANNER_SCROLL_TIME;
        IIndicator indicator;
        boolean isAutoScroll;//是否自动轮播
        int indicatorGravity = Gravity.CENTER;
        ViewPager.PageTransformer bannerTransformer;

        public Builder setInterval(int interval) {
            this.interval = interval;
            return this;
        }

        public Builder setScrollTime(int scrollTime) {
            this.scrollTime = scrollTime;
            return this;
        }

        public Builder setBannerTransformer(ViewPager.PageTransformer bannerTransformer) {
            this.bannerTransformer = bannerTransformer;
            return this;
        }

        public Builder setIndicatorGravity(int gravity) {
            this.indicatorGravity = gravity;
            return this;
        }

        public Builder setIndicator(IIndicator indicator) {
            this.indicator = indicator;
            return this;
        }

        public Builder setAutoScroll(boolean autoScroll) {
            isAutoScroll = autoScroll;
            return this;
        }

        public BannerConfig build() {
            return new BannerConfig(this);
        }
    }
}
