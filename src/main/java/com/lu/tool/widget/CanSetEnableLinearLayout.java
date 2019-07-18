package com.lu.tool.widget;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 自定义linearLayout开放一个接口可以设置内部child是否可以点击
 * Created  on 2017/3/20.
 * by luqihua
 */

public class CanSetEnableLinearLayout extends LinearLayout {

    private boolean mCanChildEnable = true;//内部child是否可以点击

    public CanSetEnableLinearLayout(Context context) {
        this(context, null);
    }

    public CanSetEnableLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanSetEnableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setChildEnable(final boolean enable) {
        setChildViewEnable(this, enable);
    }

    /**
     * 递归判断改变viewGroup内所有view的enable
     *
     * @param viewGroup
     * @param enable
     */
    private void setChildViewEnable(ViewGroup viewGroup, boolean enable) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                setChildViewEnable((ViewGroup) v, enable);
            } else {
                if ((v instanceof TextView) && !enable) {
                    ((TextView) v).setInputType(InputType.TYPE_NULL);
                }
                v.setEnabled(enable);
            }
        }
    }

}
