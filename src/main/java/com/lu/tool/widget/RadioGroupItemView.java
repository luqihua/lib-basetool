package com.lu.tool.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lu.tool.util.DeviceUtil;
import com.lu.tool.util.ResourceUtil;

import lu.basetool.R;

/**
 * @author lqh
 * @time 2017/10/31 上午11:30
 * @description
 */
public class RadioGroupItemView extends LinearLayout {

    private Context mContext;
    private CheckRadioGroup mCheckRadioGroup;
    private String mTitle;
    private boolean isRequired;
    private CheckRadioGroup mRadioGroup;

    public RadioGroupItemView(Context context) {
        this(context, null);
    }

    public RadioGroupItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioGroupItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        if (attrs != null) {
            TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.InputView);
            mTitle = array.getString(R.styleable.InputView_input_title);
            isRequired = array.getBoolean(R.styleable.InputView_required, false);
            array.recycle();
        }
        initView(attrs);

    }

    private void initView(AttributeSet attrs) {
        setBackgroundColor(Color.WHITE);

        addView(createTitleView(mTitle));
        setGravity(Gravity.CENTER_VERTICAL);

        mRadioGroup = createCheckRadioGroup(attrs);
        addView(mRadioGroup);

    }

    private CheckRadioGroup createCheckRadioGroup(AttributeSet attrs) {
        mCheckRadioGroup = new CheckRadioGroup(mContext, attrs);
        mCheckRadioGroup.setOrientation(HORIZONTAL);
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        mCheckRadioGroup.setLayoutParams(params);
        return mCheckRadioGroup;
    }

    private View createTitleView(String title) {
        TextView textView = new TextView(mContext);
        textView.setTextColor(ResourceUtil.getColor(R.color.text_color));
        textView.setTextSize(16);
        textView.setCompoundDrawablePadding(DeviceUtil.dp2px(5));
        textView.setText(title);

        if (isRequired) {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.asterisk, 0);
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.LEFT;
        textView.setLayoutParams(params);

        FrameLayout frameLayout = new FrameLayout(mContext);
        LayoutParams params1 = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.weight = 1;
        params.leftMargin = DeviceUtil.dp2px(10);
        frameLayout.setLayoutParams(params1);
        frameLayout.addView(textView);

        return frameLayout;
    }


    /**
     * 获取当前选中的item的下标 以string返回
     *
     * @return
     */
    public String getCheckString() {
        return mCheckRadioGroup.getCheckString();
    }

    public String getCheckText() {
        return mCheckRadioGroup.getCheckText();
    }

    public void setCheckItem(int position) {
        mCheckRadioGroup.setCheckItem(position);
    }

    public void setCheckItem(String checkText) {
        mCheckRadioGroup.setCheckItem(checkText);
    }
}
