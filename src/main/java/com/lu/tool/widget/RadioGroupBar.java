package com.lu.tool.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: luqihua
 * Time: 2018/1/3
 * Description: MyView
 */

public class RadioGroupBar extends RadioGroup implements RadioGroup.OnCheckedChangeListener {
    private static final String NAME_SPACE = "http://schemas.android.com/apk/res/android";
    private static final String ATTR_ENTRIES = "entries";

    private final int RADIUS = 30;
    private Context mContext;
    private int mNormalItemBgc = Color.LTGRAY;
    private int mCheckedItemBgc = Color.GRAY;
    private List<String> mItems = new ArrayList<>();

    private OnItemSwitchListener onItemCheckedListener;

    public RadioGroupBar(Context context) {
        this(context, null);
    }

    public RadioGroupBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        if (attrs != null) {
            int arrayId = attrs.getAttributeResourceValue(NAME_SPACE, ATTR_ENTRIES, 0);
            if (arrayId != 0) {
                setItem(Arrays.asList(getResources().getStringArray(arrayId)));
            }
        }
        setOnCheckedChangeListener(this);
        setOrientation(HORIZONTAL);
    }

    public void setItemBackground(int normalColor, int CheckedColor) {
        this.mNormalItemBgc = normalColor;
        this.mCheckedItemBgc = CheckedColor;
        if (getChildCount() > 0) {
            setItem(mItems);
        }
    }

    public void setItem(List<String> items) {
        if (items == null) return;
        if (items != mItems) {
            this.mItems.clear();
            this.mItems.addAll(items);
        }
        removeAllViews();
        int size = mItems.size();
        for (int i = 0; i < size; i++) {
            RadioButton button = createChild(mItems.get(i));
            button.setId(i);
            button.setBackground(createBackground(i == 0, i == (size - 1)));
            if (i==0){
                button.setChecked(true);
            }
            addView(button);
        }
    }


    private RadioButton createChild(String text) {
        RadioButton button = new RadioButton(mContext);
        button.setButtonDrawable(null);
        button.setText(text);
        button.setGravity(Gravity.CENTER);
        button.setPadding(5, 5, 5, 5);

        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        params.leftMargin = 1;
        button.setLayoutParams(params);
        return button;
    }


    private Drawable createBackground(boolean leftCorner, boolean rightCorner) {
        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setColor(mNormalItemBgc);


        GradientDrawable checkedDrawable = new GradientDrawable();
        checkedDrawable.setColor(mCheckedItemBgc);

        float[] radii = new float[]{0, 0, 0, 0, 0, 0, 0, 0};

        if (leftCorner) {
            radii[0] = RADIUS;
            radii[1] = RADIUS;
            radii[6] = RADIUS;
            radii[7] = RADIUS;
        }

        if (rightCorner) {
            radii[2] = RADIUS;
            radii[3] = RADIUS;
            radii[4] = RADIUS;
            radii[5] = RADIUS;
        }

        normalDrawable.setCornerRadii(radii);
        checkedDrawable.setCornerRadii(radii);

        StateListDrawable listDrawable = new StateListDrawable();
        listDrawable.addState(new int[]{android.R.attr.state_checked}, checkedDrawable);
        listDrawable.addState(new int[]{}, normalDrawable);

        return listDrawable;
    }

    public void setOnItemSwitchListener(OnItemSwitchListener listener) {
        this.onItemCheckedListener = listener;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (onItemCheckedListener == null) return;
        onItemCheckedListener.onItemSwitch(mItems.get(checkedId), checkedId);
    }

    public interface OnItemSwitchListener {
        void onItemSwitch(String text, int position);
    }

}
