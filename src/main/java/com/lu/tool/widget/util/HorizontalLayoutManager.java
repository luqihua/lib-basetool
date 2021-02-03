package com.lu.tool.widget.util;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created  on 2017/3/3.
 * by luqihua
 */

public class HorizontalLayoutManager extends RecyclerView.LayoutManager {

    private SparseArray<Rect> mAllItems = new SparseArray<>();

    /*是否处于可见状态*/
    private SparseBooleanArray mItemStates = new SparseBooleanArray();

    private int mTotalHeight = 0;

    private int mVerticalScrollOffset = 0;

    private int mHeight, mWidth;
    private int mOffsetLeft;

    private RecyclerView recyclerView;

    public HorizontalLayoutManager(RecyclerView v) {
        this.recyclerView = v;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (mWidth == 0) {
            mWidth = getHorizontalSpace();
        }

        if (mHeight == 0) {
            mHeight = getVerticalSpace();
        }
        detachAndScrapAttachedViews(recycler);
        layoutView(recycler, state);


        if (recyclerView!=null){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , mTotalHeight + recyclerView.getPaddingTop() + recyclerView.getPaddingBottom());

            recyclerView.setLayoutParams(params);
        }
    }


    private void layoutView(RecyclerView.Recycler recycler, RecyclerView.State state) {
        mTotalHeight = getPaddingTop();
        mOffsetLeft = getPaddingLeft();

        if (getItemCount() == 0) return;

        for (int i = 0; i < getItemCount(); i++) {
            View v = recycler.getViewForPosition(i);
            addView(v);
            measureChildWithMargins(v, 0, 0);//测量子view  为其宽高赋值
            int width = getDecoratedMeasuredWidth(v);
            int height = getDecoratedMeasuredHeight(v);//计算view 的实际大小，包含ItemDecorator

            calculateItemDecorationsForChild(v, new Rect());//调用这个方法可以调整itemView的大小，以去除itemDecorator

            Rect mTmpRect = mAllItems.get(i);
            if (mTmpRect == null) {
                mTmpRect = new Rect();
                mAllItems.put(i, mTmpRect);
            }

            if (mOffsetLeft + width > mWidth) {
                mOffsetLeft = getPaddingLeft();
                mTotalHeight += height;
            }
            mTmpRect.set(mOffsetLeft, mTotalHeight, mOffsetLeft + width, mTotalHeight + height);

            layoutDecorated(v, mTmpRect.left, mTmpRect.top, mTmpRect.right, mTmpRect.bottom);

            mOffsetLeft += width;

            if (i == getItemCount() - 1) {
                mTotalHeight += height;
            }

            mItemStates.put(i, false);//
        }
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }


    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int travel = dy;

        if (mTotalHeight < mHeight) return 0;

        if (mVerticalScrollOffset + dy < 0) {//滑动到顶部
            travel = -mVerticalScrollOffset;
        } else if (mVerticalScrollOffset + dy > mTotalHeight - mHeight) {//滑动到底部
            travel = mTotalHeight - mHeight - mVerticalScrollOffset;
        }

        mVerticalScrollOffset += travel;

        offsetChildrenVertical(-travel);

        return travel;
    }


    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }


    private int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }


}
