package com.lu.tool.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created  on 2017/3/20.
 * by luqihua
 */

public class MaxListView extends ListView{


    public MaxListView(Context context) {
        super(context);
    }

    public MaxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST));
    }

}
