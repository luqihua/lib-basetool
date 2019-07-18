package com.lu.tool.widget.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * recyclerView 简单分割线
 * create by lqh
 */
public class DividerHorizontal extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private boolean isLeft;
    private boolean isRight;
    private int dividerHeight;

    public DividerHorizontal(boolean isLeft, boolean isRight, int dividerHeight) {
        this(isLeft, isRight, dividerHeight, Color.TRANSPARENT);
    }

    public DividerHorizontal(boolean isLeft, boolean isRight, int dividerHeight, int color) {
        this.isLeft = isLeft;
        this.isRight = isRight;
        this.dividerHeight = dividerHeight;
        if (color <= 0) {
            color = 0;
        }

        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view);
        if (isLeft && isRight) {
            if (position == 0)
                outRect.set(dividerHeight, 0, dividerHeight, 0);
            else
                outRect.set(0, 0, dividerHeight, 0);

            return;
        }
        if (isLeft) {
            outRect.set(dividerHeight, 0, 0, 0);
            return;
        }
        if (isRight) {
            outRect.set(0, 0, dividerHeight, 0);
            return;
        }
        if (position == 0)
            outRect.set(0, 0, 0, 0);
        else
            outRect.set(dividerHeight, 0, 0, 0);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int count = parent.getChildCount();
        for (int i = 0; i < count - 1; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            final int left = child.getRight() + params.rightMargin
                    + Math.round(ViewCompat.getTranslationX(child));

            final int right = left + dividerHeight;
            c.drawRect(left, top, right, bottom, mPaint);
        }

        //第一条分割线
        if (isLeft)
            c.drawRect(parent.getPaddingLeft()
                    , top
                    , parent.getPaddingLeft() + dividerHeight
                    , bottom
                    , mPaint);
        if (isRight)
            c.drawRect(parent.getRight() - parent.getPaddingRight() - dividerHeight
                    , top
                    , parent.getRight() - parent.getPaddingRight()
                    , bottom
                    , mPaint);
    }
}
