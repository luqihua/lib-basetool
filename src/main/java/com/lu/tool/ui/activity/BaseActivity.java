package com.lu.tool.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;

import com.lu.tool.ui.IUIInterface;
import com.lu.tool.widget.SlideBackLayout;

/**
 * @author lqh
 * @time 2018/3/13 上午10:15
 * @description
 */
public abstract class BaseActivity extends AppCompatActivity implements  IUIInterface {
    protected Activity mCurrentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isSupportSwipeBack()) {
            new SlideBackLayout(this).attach2Activity(this, null);
        }
        super.onCreate(savedInstanceState);
        this.mCurrentActivity = this;
        setContentView(getLayoutId());
        //ButterKnife
        //处理上个界面传递的参数，处理避免空指针
        Bundle bundle = getIntent().getExtras();
        handlerIntent(bundle == null ? new Bundle() : bundle);
        initView();
        bindListener();
        bindData();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_BACK) {
            onKeyBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onKeyBack() {
        finish();
    }

    @Override
    public boolean isUseEventBus() {
        return false;
    }

    public boolean isSupportSwipeBack() {
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