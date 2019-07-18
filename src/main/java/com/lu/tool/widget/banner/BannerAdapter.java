package com.lu.tool.widget.banner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * author: luqihua
 * date:2018/7/13
 * description:
 **/
public class BannerAdapter extends PagerAdapter {
    //为了达到无限循环的效果(实际上只是将数量增加到900倍实现伪的无限循环，这个数字可以定义大一些)
    public static final int CYCLE = 900;
    private Context mContext;
    private List<Object> mData = new ArrayList<>();
    private SparseArray<FrameLayout> mItemViews = new SparseArray<>();
    private IBannerCreator mBannerCreator;

    public BannerAdapter(Context context, IBannerCreator creator, List<?> data) {
        this.mContext = context;
        this.mBannerCreator = creator;
        this.mData.addAll(data);
    }

    @Override
    public int getCount() {
        if (mData == null || mData.size() == 0) return 0;
        if (mData.size() == 1) return 1;
        return mData.size() * CYCLE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final int realPosition = position % mData.size();
        FrameLayout itemView = mItemViews.get(realPosition);
        if (itemView == null || itemView.getParent() != null) {
            itemView = createBanner(mContext);
            mItemViews.put(realPosition, itemView);
        }

        ImageView imageView = (ImageView) itemView.getChildAt(0);
        mBannerCreator.showBanner(mContext, imageView, mData.get(realPosition));
        container.addView(itemView);
        return itemView;
    }

    //创建banner 一般是ImageView
    //但如果用了fresco框架  也可能是SimpleDrawee，所以这里通过自定义的mPageCreator来获取
    private FrameLayout createBanner(Context context) {
        ImageView imageView = mBannerCreator.createBanner(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);

        //使用一个FrameLayout包装一下，这是为了解决直接添加给ViewPager后无法点击问题
        FrameLayout frameLayout = new FrameLayout(mContext);
        frameLayout.addView(imageView);
        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        frameLayout.setLayoutParams(params);
        return frameLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}

