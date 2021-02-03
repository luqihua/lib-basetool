package com.lu.tool.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Author: luqihua
 * Time: 2018/1/3
 * Description: MessageView
 */

public class MessageView extends AppCompatImageView {

    private final float DOT_SIZE = 1 / 8f;

    private Paint mPaint;
    private int dot_center_x, dot_center_y;
    private int mDotSize;
    private boolean hasNewMessage = false;

    public MessageView(Context context) {
        this(context, null);
    }

    public MessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(w, h);

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Drawable drawable = getDrawable();
        int centerX = w / 2;
        int centerY = h / 2;
        if (drawable != null) {
            w = drawable.getIntrinsicWidth();
            h = drawable.getIntrinsicHeight();
        }
        this.mDotSize = (int) (w * DOT_SIZE);

        dot_center_x = centerX + w / 2 - mDotSize;
        dot_center_y = centerY - h / 2 + mDotSize;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (hasNewMessage) {
            canvas.drawCircle(dot_center_x, dot_center_y, mDotSize, mPaint);
        }
    }

    public boolean isHasNewMessage() {
        return hasNewMessage;
    }

    public void setHasNewMessage(boolean hasNewMessage) {
        this.hasNewMessage = hasNewMessage;
        invalidate();
    }
}
