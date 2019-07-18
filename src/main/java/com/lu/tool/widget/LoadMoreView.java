package com.lu.tool.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.lu.tool.adapter.BaseLoadMoreAdapter;
import com.lu.tool.util.ResourceUtil;

import java.util.List;

import lu.basetool.R;


/**
 * Author: luqihua
 * Time: 2017/12/20
 * Description: LoadMoreView
 */

public class LoadMoreView<T> extends SwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener {
    private int mPage = 1;
    private int mCount = 0;
    private Context mContext;
    private LoadMoreRecyclerView recyclerView;
    private BaseLoadMoreAdapter<T> mAdapter;
    private OnDataChangeListener dataChangeListener;


    public LoadMoreView(Context context) {
        this(context, null);
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.widget_load_more, this);
        initStyle();
        initView();
    }

    private void initStyle() {
        setOnRefreshListener(this);
        setBackgroundColor(ResourceUtil.getColor(R.color.line_color));
        setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light);
    }

    private void initView() {
        recyclerView = (LoadMoreRecyclerView) findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setFocusable(false);
        recyclerView.setLoadMoreListener(new LoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (dataChangeListener != null) {
                    dataChangeListener.onLoadMore(++mPage);
                }
            }
        });
    }

    public void setAdapter(BaseLoadMoreAdapter<T> loadMoreAdapter) {
        mAdapter = loadMoreAdapter;
        recyclerView.setAdapter(mAdapter);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        recyclerView.addItemDecoration(decor);
    }

    public void setOnDataChangeListener(OnDataChangeListener listener) {
        this.dataChangeListener = listener;
    }
    /*=====================================================*/


    public void dataChange(final List<T> data) {
        dataChange(mCount, data);
    }

    public void dataChange(int count, final List<T> data) {
        if (isRefreshing()) {
            update(count, data);
        } else {
            insert(count, data);
        }
    }

    /**
     * 更新数据
     *
     * @param count
     * @param data
     */
    private void update(int count, final List<T> data) {
        setRefreshing(false);
        this.mPage = 1;
        this.mCount = count;
        //计算是否可以加载更多
        boolean isLoadMoreEnable = (data != null && mCount > data.size());
        mAdapter.update(data, isLoadMoreEnable);
        recyclerView.setLoadMoreAble(isLoadMoreEnable);
        //第一次初始化时隐藏的，因此要判断显示
        if (recyclerView.getVisibility() == View.GONE) {
            recyclerView.setVisibility(View.VISIBLE);
        }
        recyclerView.scrollToPosition(0);
    }

    /**
     * 插入新数据
     *
     * @param count
     * @param data
     */
    private void insert(int count, List<T> data) {
        this.mCount = count;
        //计算是否可以加载更多
        boolean isLoadMoreEnable = (data != null && mCount > mAdapter.getRealDataCount() + data.size());
        mAdapter.insert(data, isLoadMoreEnable);
        recyclerView.setLoadMoreAble(isLoadMoreEnable);
    }

    @Override
    public void onRefresh() {
        this.mPage = 1;
        if (dataChangeListener != null) {
            dataChangeListener.onRefresh(mPage);
        }
    }


    public interface OnDataChangeListener {
        void onLoadMore(int page);

        void onRefresh(int page);
    }

}
