package com.lu.tool.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lqh on 2016/1/20.
 */
public abstract class CommonAdapter<T> extends BaseAdapter implements IAdapter<T> {

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<T> mData = new ArrayList<>();
    protected OnItemClickListener<T> onItemClickListener;

    public CommonAdapter(Context context) {
        this(context, new ArrayList<T>());
    }

    public CommonAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        if (data != null) {
            this.mData.addAll(data);
        }
    }

    @Override
    public void setItemClickListener(OnItemClickListener<T> listener) {
        this.onItemClickListener = listener;
    }

    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getItemView(ViewGroup parent, int viewType) {
        if (getItemLayoutId(viewType) != 0) {
            return mInflater.inflate(getItemLayoutId(viewType), parent, false);
        }
        return new View(parent.getContext());
    }

    @Override
    public View getEmptyView(ViewGroup parent) {
        return new View(mContext);
    }

    @Override
    public int getCount() {
        int count = mData == null ? 0 : mData.size();
        if (count == 0 && willSetEmpty()) return 1;
        return count;
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if ((mData == null || mData.size() == 0) && willSetEmpty()) {
            return getEmptyView(parent);
        }
        BaseHolder<T> holder;
        T itemData = mData.get(position);
        if (convertView == null || convertView.getTag() == null) {
            convertView = getItemView(parent, getItemViewType(position));
            holder = new BaseHolder<>(convertView);
            holder.setOnItemClickListener(onItemClickListener);
            convertView.setTag(holder);
        } else {
            holder = (BaseHolder<T>) convertView.getTag();
        }
        holder.setItemData(itemData);
        holder.setPosition(position);
        binItemData(holder, position, itemData);
        return convertView;
    }


    protected boolean willSetEmpty() {
        return false;
    }

    @Override
    public void insert(Collection<T> data) {
        if (data == null || data.size() == 0) return;
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void update(Collection<T> data) {
        if (data == null || data.size() == 0) {
            mData.clear();
        } else if (data != mData) {
            mData.clear();
            mData.addAll(data);

        }
        notifyDataSetChanged();
    }

    @Override
    public void remove(int position) {
        if (position < 0 || position >= getCount()) return;
        mData.remove(position);
        notifyDataSetChanged();
    }
}
