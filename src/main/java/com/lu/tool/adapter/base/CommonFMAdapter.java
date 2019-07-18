package com.lu.tool.adapter.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by lqh on 2016/10/13.
 */
public class CommonFMAdapter extends FragmentPagerAdapter {

    private String[] mTitle;
    private List<Fragment> mFragments;

    public CommonFMAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.mFragments = fragments;
    }

    public CommonFMAdapter(FragmentManager fm, String[] title, List<Fragment> fragments) {
        super(fm);
        this.mTitle = title;
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitle != null) {
            return mTitle[position];
        }
        return super.getPageTitle(position);
    }
}
