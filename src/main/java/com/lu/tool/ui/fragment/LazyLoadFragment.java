package com.lu.tool.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lu.tool.ui.IUIInterface;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

/**
 * author: luqihua
 * date:2018/8/31
 * description: 懒加载fragment
 **/
public abstract class LazyLoadFragment extends Fragment implements IUIInterface {
    private boolean isVisible = false;//当前fragment是否可见
    private boolean isInitView = false;//是否与View建立起映射关系
    protected boolean isFirstLoad = true;//是否是第一次加载

    protected View mContentView;
    protected Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handlerIntent(getArguments());
        if (isUseEventBus()){
            EventBus.getDefault().register(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(getLayoutId(), container, false);
            ButterKnife.bind(this, mContentView);
            initView();
            bindListener();
            isInitView = true;
            lazyLoadData();
        }
        return mContentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInitView = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        isVisible = isVisibleToUser;
        if (isVisibleToUser)
            lazyLoadData();
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void lazyLoadData() {
        if (!isFirstLoad || !isVisible || !isInitView) {
            return;
        }
        bindData();
        isFirstLoad = false;
    }


    @Override
    public boolean isUseEventBus() {
        return false;
    }

    @Override
    public void handlerIntent(Bundle bundle) {

    }

    @Override
    public void initView() {

    }

    @Override
    public void bindListener() {

    }

    @Override
    public void bindData() {

    }
}
