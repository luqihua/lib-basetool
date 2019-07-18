package com.lu.tool.adapter.base;

import android.view.View;

/**
 * Author: luqihua
 * Time: 2017/12/21
 * Description: IHolder
 */

public interface IHolder<T> {
    <E extends View> E getView(int viewId);

    void setPosition(int position);

    <E extends View> E getItemView();

    void setItemData(T data);

    T getItemData();

    void setOnItemClickListener(OnItemClickListener<T> listener);
}
