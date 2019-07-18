package com.lu.tool.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lu.tool.ui.IUIInterface;

import butterknife.ButterKnife;

/**
 * @author lqh
 * @time 2018/3/13 上午10:15
 * @description
 */

public abstract class BaseFragment extends Fragment implements IUIInterface {

    protected View mContentView;
    protected Context mContext;

    @Nullable
    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        handlerIntent(bundle == null ? new Bundle() : bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(getLayoutId(), container, false);
            ButterKnife.bind(this, mContentView);
            initView();
            bindListener();
        }
        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindData();
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
