package com.lu.tool.widget.banner;

import android.support.v4.view.ViewPager;
import android.view.Gravity;

import com.lu.tool.widget.banner.indicator.IIndicator;


/**
 * author: luqihua
 * date:2018/7/14
 * description:
 **/
public class BannerConfig {
    private int mInterval;
    private int mScrollTime;
    private int mIndicatorGravity;
    private IIndicator mIndicator;
    private ViewPager.PageTransformer mBannerTransformer;

    public BannerConfig(Builder builder) {
        this.mInterval = builder.interval;
        this.mScrollTime = builder.scrollTime;
        this.mBannerTransformer = builder.bannerTransformer;
        this.mIndicatorGravity = builder.indicatorGravity;
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

    public int getIndicatorGravity() {
        return this.mIndicatorGravity;
    }

    public static class Builder {
        int interval = Constants.DEFAULT_BANNER_INTERVAL;
        int scrollTime = Constants.DEFAULT_BANNER_SCROLL_TIME;
        private IIndicator indicator;
        private int indicatorGravity = Gravity.CENTER;
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

        public BannerConfig build() {
            return new BannerConfig(this);
        }
    }
}
