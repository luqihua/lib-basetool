package com.lu.tool.adapter.base;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * Created by lqh on 2016/1/20.
 */
public class BaseHolder<T> extends RecyclerView.ViewHolder implements IHolder<T>, OnClickListener {

    private SparseArray<View> mViews = new SparseArray<View>();
    //item的点击监听
    private OnItemClickListener<T> mListener = null;
    // holder绑定的数据源对象
    private T mItemData;
    // 在adapter中的位置
    private int position;

    public BaseHolder(View v) {
        super(v);
        this.itemView.setOnClickListener(this);
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void setItemData(T itemData) {
        this.mItemData = itemData;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.mListener = listener;
    }

    @Override
    public T getItemData() {
        return mItemData;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onItemClick(mItemData, position);
        }
    }

    @Override
    public <E extends View> E getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (E) view;
    }

    public void setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
    }

    @Override
    public <E extends View> E getItemView() {
        return (E) this.itemView;
    }
}
