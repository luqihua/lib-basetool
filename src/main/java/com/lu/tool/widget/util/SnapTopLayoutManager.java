package com.lu.tool.widget.util;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Author: luqihua
 * Time: 2017/12/13
 * Description: 这个LayoutManager的scrollToPosition(int position)可以指定位置滚动到顶部
 */

public class SnapTopLayoutManager extends LinearLayoutManager {

    public SnapTopLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void scrollToPosition(int position) {
        scrollToPositionWithOffset(position, 0);
    }
}
