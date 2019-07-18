package com.lu.tool.adapter.base;

import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;

/**
 * Author: luqihua
 * Time: 2017/12/21
 * Description: IAdapter
 */

public interface IAdapter<T> {
    int getItemLayoutId(int viewType);

    View getItemView(ViewGroup parent, int viewType);

    View getEmptyView(ViewGroup parent);

    void setItemClickListener(OnItemClickListener<T> listener);

    void binItemData(BaseHolder<T> holder, int position, T itemData);

    void update(Collection<T> data);

    void insert(Collection<T> data);

    void remove(int position);

}
