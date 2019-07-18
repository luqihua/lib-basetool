package com.lu.tool.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lu.tool.util.DeviceUtil;

/**
 * radioGroup可获取当前选中的子view position
 * 可在xml中绑定数据集
 * Created  on 2017/3/22.
 * by luqihua
 */

public class CheckRadioGroup extends RadioGroup {

    private static final String NAME_SPACE = "http://schemas.android.com/apk/res/android";
    private static final String ATTR_ENTRIES = "entries";
    private static final String ATTR_CHECKED = "checked";
    private static final int DEFAULT_COLOR = 0xff2c2c2c;

    private String[] mData = new String[]{};

    private onCheckChangeListener listener;
    private boolean bChecked;

    public void setListener(onCheckChangeListener listener) {
        this.listener = listener;
    }

    public CheckRadioGroup(Context context, int arrayRes) {
        super(context);
        mData = getResources().getStringArray(arrayRes);
        createItem(mData);
        init();
    }

    public CheckRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            bChecked = attrs.getAttributeBooleanValue(NAME_SPACE, ATTR_CHECKED, false);
            int arrayId = attrs.getAttributeResourceValue(NAME_SPACE, ATTR_ENTRIES, 0);
            mData = getResources().getStringArray(arrayId);
            createItem(mData);
        }
        init();
    }


    /**
     * 添加item
     *
     * @param data
     */
    public void createItem(String[] data) {
        for (int i = 0; i < data.length; i++) {
            addView(createRadioButton(i, data[i]));
        }
        if (bChecked && data.length > 0) check(getChildAt(0).getId());
    }

    /**
     * 创建一个radioButton
     *
     * @param text
     * @return
     */
    private View createRadioButton(int index, String text) {
        RadioButton radioButton = new RadioButton(getContext());
        radioButton.setText(text);
        radioButton.setTextColor(DEFAULT_COLOR);
        LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        if (index > 0)
            params.leftMargin = DeviceUtil.dp2px(5);
        params.weight = 1;
        radioButton.setLayoutParams(params);
        return radioButton;
    }

    private void init() {
        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (listener == null) return;
                RadioButton button = (RadioButton) group.findViewById(checkedId);
                if (button != null) {
                    String text = button.getText().toString();
                    int index = group.indexOfChild(button);
                    listener.onCheckChange(text, index);
                }
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).setEnabled(enabled);
        }
    }


    public void setChecked(boolean bChecked) {
        this.bChecked = bChecked;
    }

    /**
     * 设置选中项
     *
     * @param checkText 有可能是下标  也有可能是选中的字符串
     */
    public void setCheckItem(String checkText) {
        int position = -1;
        if (!TextUtils.isEmpty(checkText)) {
            try {
                position = Integer.valueOf(checkText);
            } catch (Exception e) {
                for (int i = 0; i < mData.length; i++) {
                    if (checkText.equals(mData[i]))
                        position = i;
                }
            }
        }
        setCheckItem(position);
    }

    public void setCheckItem(int position) {
        if (position < 0 || position > getChildCount() - 1)
            check(-1);
        else
            check(getChildAt(position).getId());
    }

    /**
     * 获取当前选中的item的下标 以int返回
     *
     * @return
     */
    public int getCheckInteger() {
        return indexOfChild(findViewById(getCheckedRadioButtonId()));
    }

    /**
     * 获取当前选中的item的下标 以string返回
     *
     * @return
     */
    public String getCheckString() {
        return String.valueOf(getCheckInteger());
    }

    /**
     * 获取选中的文字
     *
     * @return
     */
    public String getCheckText() {
        RadioButton button = (RadioButton) findViewById(getCheckedRadioButtonId());
        return button == null ? "" : button.getText().toString();
    }

    /**
     * 选中改变的监听
     */
    public interface onCheckChangeListener {
        void onCheckChange(String itemText, int position);
    }


}
