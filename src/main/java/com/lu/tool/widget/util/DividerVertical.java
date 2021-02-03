package com.lu.tool.widget.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.lu.tool.util.ResourceUtil;

import lu.basetool.R;

/**
 * recyclerView 简单分割线
 * create by lqh
 */
public class DividerVertical extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private int dividerHeight;

    public DividerVertical(int dividerHeight) {
        this(dividerHeight, ResourceUtil.getColor(R.color.line_color));
    }

    public DividerVertical(int dividerHeight, int color) {
        this.dividerHeight = dividerHeight;
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildLayoutPosition(view) == 0) return;
        outRect.set(0, dividerHeight, 0, 0);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int count = parent.getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int bottom = child.getTop() - params.topMargin - Math.round(ViewCompat.getTranslationY(child));
            final int top = bottom - dividerHeight;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }
}
