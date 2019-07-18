package com.lu.tool.adapter.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * create by lqh
 *
 * @param <T>
 */
public abstract class CommonRecyclerAdapter<T> extends Adapter<RecyclerView.ViewHolder> implements
        IAdapter<T> {

    private static final int TYPE_HEAD = 0x1111;
    private static final int TYPE_FOOTER = 0x1112;
    private static final int TYPE_EMPTY = 0x1113;

    protected boolean hasHeader;
    protected boolean hasFooter;

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<T> mData = new ArrayList<>();
    protected OnItemClickListener<T> itemClickListener;

    public CommonRecyclerAdapter(Context context) {
        this(context, new ArrayList<T>());
    }

    public CommonRecyclerAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        if (data != null) {
            this.mData.addAll(data);
        }
    }

    @Override
    public void setItemClickListener(OnItemClickListener<T> listener) {
        this.itemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        int count = getRealDataCount();
        if (count == 0 && willSetEmpty()) return 1;
        hasFooter = willSetFooter() && (showFootAlways() || count > 0);
        hasHeader = willSetHeader() && count > 0;
        if (hasFooter) count++;
        if (hasHeader) count++;
        return count;
    }

    /**
     * 真实数据量
     *
     * @return
     */
    public int getRealDataCount() {
        return mData == null ? 0 : mData.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (getRealDataCount() == 0 && position == 0 && willSetEmpty()) {
            return TYPE_EMPTY;
        }
        if (position == 0 && hasHeader) {
            return TYPE_HEAD;
        }
        if (position == getItemCount() - 1 && hasFooter) {
            return TYPE_FOOTER;
        }
        return super.getItemViewType(position);
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
    public long getItemId(int position) {
        return position;
    }

    public T getItem(int position) {
        int realP = hasHeader ? position - 1 : position;
        return mData.get(realP < 0 ? 0 : realP);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEAD:
                return new HeaderHolder(getHeaderView(parent));
            case TYPE_FOOTER:
                return new FooterHolder(getFooterView(parent));
            case TYPE_EMPTY:
                return new EmptyHolder(getEmptyView(parent));
            default:
                BaseHolder<T> holder = new BaseHolder<>(getItemView(parent, viewType));
                holder.setOnItemClickListener(itemClickListener);
                onCreateHolder(holder, viewType);
                return holder;
        }
    }

    /**
     * 创建holder的时候进行一些操作
     *
     * @param holder
     * @param viewType
     */
    protected void onCreateHolder(BaseHolder<T> holder, int viewType) {

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof BaseHolder) {
                BaseHolder<T> bHolder = (BaseHolder<T>) holder;
                int realP = hasHeader ? position - 1 : position;
                bHolder.setItemData(mData.get(realP));
                bHolder.setPosition(position);
                binItemData(bHolder, position, mData.get(realP));
                return;
            }

            if (holder instanceof HeaderHolder) {
                setHeader((HeaderHolder) holder);
                return;
            }

            if (holder instanceof FooterHolder) {
                setFooter((FooterHolder) holder);
                return;
            }

            if (holder instanceof EmptyHolder) {
                setEmpty((EmptyHolder) holder);
                return;
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (payloads == null || payloads.size() == 0) {
            onBindViewHolder(holder, position);
        } else {
            if (holder instanceof BaseHolder) {
                refreshWidthPayLoad((BaseHolder<T>) holder, position, payloads);
            } else if (holder instanceof HeaderHolder) {
                setHeader((HeaderHolder) holder);
            }
        }
    }

    /**
     * 用于局部更新布局
     *
     * @param payloads
     */
    protected void refreshWidthPayLoad(BaseHolder<T> holder, int position, List<Object> payloads) {

    }

    @Override
    public void insert(Collection<T> data) {
        if (data == null || data.size() == 0) return;
        int oldRange = mData.size();
        if (hasHeader) oldRange++;
        mData.addAll(data);
        notifyItemRangeChanged(oldRange, data.size());
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
        if (position < 0 || position >= getRealDataCount()) return;
        mData.remove(position);
        notifyDataSetChanged();
    }

    protected boolean willSetHeader() {
        return false;
    }

    protected boolean willSetFooter() {
        return false;
    }

    protected boolean willSetEmpty() {
        return false;
    }

    protected View getHeaderView(ViewGroup parent) {
        return new View(mContext);
    }


    protected View getFooterView(ViewGroup parent) {
        return new View(mContext);
    }


    protected void setFooter(FooterHolder holder) {

    }

    protected void setHeader(HeaderHolder holder) {

    }

    protected void setEmpty(EmptyHolder holder) {

    }

    protected boolean showFootAlways() {
        return false;
    }

    protected static class HeaderHolder extends RecyclerView.ViewHolder {
        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    protected static class FooterHolder extends RecyclerView.ViewHolder {
        public FooterHolder(View itemView) {
            super(itemView);
        }
    }

    protected static class EmptyHolder extends RecyclerView.ViewHolder {
        public EmptyHolder(View itemView) {
            super(itemView);
        }
    }

}
