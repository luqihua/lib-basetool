package com.lu.tool.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Author: luqihua
 * Time: 2017/9/26
 * Description: LoadMoreRecyclerView
 */

public class LoadMoreRecyclerView extends RecyclerView {

    private boolean loadMoreAble;
    private boolean isBottom = false;
    private boolean isSmoothDrag = false;
    private LinearLayoutManager mLayoutManager;
    private OnLoadMoreListener mLoadMoreListener;


    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addOnScrollListener(onScrollListener);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        this.mLayoutManager = (LinearLayoutManager) layout;
    }

    public void setLoadMoreAble(boolean loadMoreAble) {
        this.loadMoreAble = loadMoreAble;
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.mLoadMoreListener = loadMoreListener;
    }

    private OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            /**
             * 如果当前处于加载中,不处理该次滑动,防止异步多次调用
             */
            if (!loadMoreAble || mLoadMoreListener == null) return;

            int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            switch (newState) {
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    if (lastVisibleItem + 1 == getAdapter().getItemCount()) {
                        //--正常情况是DRAGGING-->SETTLING-->IDLE;
                        //手指快速做多次滑动的时候，会出现--DRAGGING-->SETTLING-->DRAGGING-->SETTLING-->IDLE,
                        // 因此要判断如果从SETTLING-->DRAGGING,并其已经到达底部的时候，做刷加载更多操作*/
                        if (isSmoothDrag && !isBottom) {
                            //等待数据加载完毕之后再次开启
                            loadMoreAble = false;
                            mLoadMoreListener.onLoadMore();
                        }
                        isBottom = true;
                    }
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    isSmoothDrag = true;
                    break;
                case RecyclerView.SCROLL_STATE_IDLE:
                    isSmoothDrag = false;
                    if (isBottom) {
                        isBottom = false;
                        return;
                    }
                    if (lastVisibleItem + 1 == getAdapter().getItemCount()) {
                        //等待数据加载完毕之后再次开启
                        loadMoreAble = false;
                        mLoadMoreListener.onLoadMore();
                    }
                    break;
            }
        }
    };

    /**
     * 上拉的回调接口
     *
     * @author Administrator
     */

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
