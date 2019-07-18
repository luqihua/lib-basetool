package com.lu.tool.ui;

import android.os.Bundle;

/**
 * author: luqihua
 * date:2018/8/31
 * description: UI视图的基本方法
 **/
public interface IUIInterface {
    boolean isUseEventBus();

    void handlerIntent(Bundle bundle);

    int getLayoutId();

    void initView();

    void bindListener();

    void bindData();
}
