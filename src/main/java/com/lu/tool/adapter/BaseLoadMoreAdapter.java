package com.lu.tool.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lu.tool.adapter.base.CommonRecyclerAdapter;
import com.lu.tool.widget.EmptyView;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Collection;

import lu.basetool.R;


/**
 * Author: luqihua
 * Time: 2017/12/20
 * Description: BaseLoadMoreAdapter
 */

public abstract class BaseLoadMoreAdapter<T> extends CommonRecyclerAdapter<T> {

    private boolean isLoadMoreAble = true;
    private String emptyText;
    private int emptyImage;

    public BaseLoadMoreAdapter(Context context) {
        super(context);
        this.emptyImage = getEmptyImage();
        this.emptyText = getEmptyText();
    }

    @Override
    protected boolean willSetEmpty() {
        return true;
    }

    @Override
    protected boolean willSetFooter() {
        return isLoadMoreAble;
    }

    @Override
    public View getEmptyView(ViewGroup parent) {
        EmptyView emptyView = new EmptyView(mContext);
        emptyView.setRefreshEnable(false);
        emptyView.setEmptyIcon(emptyImage);
        emptyView.setEmptyText(emptyText);

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        emptyView.setLayoutParams(params);
        return emptyView;
    }

    @Override
    protected View getFooterView(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.layout_load_more_footer, parent, false);
        AVLoadingIndicatorView avLoadingIndicatorView = view.findViewById(R.id.iv_loading_icon);
        avLoadingIndicatorView.show();
        return view;
    }

    /**
     * 没有数据时的文字提示
     *
     * @return
     */
    protected String getEmptyText() {
        return "暂无结果";
    }

    /**
     * 没有数据时的图片
     *
     * @return
     */
    protected int getEmptyImage() {
        return R.drawable.img_no_data;
    }

    public void insert(Collection<T> data, boolean isLoadMoreEnable) {
        this.isLoadMoreAble = isLoadMoreEnable;
        super.insert(data);
    }

    public void update(Collection<T> data, boolean isLoadMoreEnable) {
        this.isLoadMoreAble = isLoadMoreEnable;
        super.update(data);
    }
}
