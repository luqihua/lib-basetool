package com.lu.tool.widget;

import android.content.Context;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Author: luqihua
 * Time: 2017/6/28
 * Description: TopBarListView
 */

public class TopBarListView extends RecyclerView {

    private TextView mLetterView;
    private LetterHolder mLetterHolder;
    private LinearLayoutManager mLayoutManager;

    public TopBarListView(@NonNull Context context) {
        this(context, null);
    }

    public TopBarListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopBarListView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLetterView(@NonNull TextView topBar) {

        if (topBar == mLetterView) {
            return;
        }

        this.mLetterView = topBar;
        this.mLetterView.setVisibility(GONE);

        addOnScrollListener(new OnScrollListener() {

            private int lastFistVisibleIndex;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {

                int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                //第一个item字母
                String first = mLetterHolder.getLetterFromIndex(firstVisibleItem);
                if (TextUtils.isEmpty(first)) {
                    mLetterView.setVisibility(GONE);
                    return;
                }
                mLetterView.setVisibility(VISIBLE);
                mLetterView.setText(first);
                //第二个item字母
                String second = mLetterHolder.getLetterFromIndex(firstVisibleItem + 1);

                if (first.equals(second)) {
                    if (firstVisibleItem != lastFistVisibleIndex) {
                        MarginLayoutParams params = (MarginLayoutParams) mLetterView.getLayoutParams();
                        params.topMargin = 0;
                        mLetterView.setLayoutParams(params);
                    }
                } else {
                    View child = view.getChildAt(0);
                    if (child != null) {
                        int height = mLetterView.getHeight();
                        MarginLayoutParams params = (MarginLayoutParams) mLetterView.getLayoutParams();
                        int bottom = child.getBottom();
                        if (bottom < height) {
                            params.topMargin = bottom - height;
                        } else {
                            if (params.topMargin != 0) {
                                params.topMargin = 0;
                            }
                        }
                        mLetterView.setLayoutParams(params);
                    }
                }

                lastFistVisibleIndex = firstVisibleItem;
            }
        });
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof LetterHolder) {
            mLetterHolder = (LetterHolder) adapter;
            super.setAdapter(adapter);
        } else {
            throw new RuntimeException("the adapter must implement the interface LetterHolder");
        }
    }


    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (layout instanceof LinearLayoutManager) {
            this.mLayoutManager = (LinearLayoutManager) layout;
            super.setLayoutManager(layout);
        } else {
            throw new RuntimeException("setLayoutManager()  must be  a LinearLayoutManager");
        }
    }

    public interface LetterHolder {
        /**
         * 根据下标获取对应的字母
         *
         * @param position
         * @return
         */
        String getLetterFromIndex(int position);

        /**
         * 根据字母获取对应的下标
         *
         * @param s
         * @return
         */
        int getIndexFromLetter(String s);
    }

}
