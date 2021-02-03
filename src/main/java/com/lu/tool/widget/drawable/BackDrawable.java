package com.lu.tool.widget.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 返回键的drawable
 */
public class BackDrawable extends Drawable {

    private static final int DEFAULT_SIZE = 60;
    private static final int DEFAULT_LINE_WIDTH = 4;
    private static final int DEFAULT_LINE_COLOR = 0xff2c2c2c;

    private Paint mPaint;
    private Path mPath;
    private int mSize;

    public BackDrawable() {
        this(DEFAULT_LINE_COLOR, DEFAULT_SIZE);
    }

    public BackDrawable(int lineColor, int size) {
        this.mSize = size == 0 ? DEFAULT_SIZE : size;
        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(lineColor);
        mPaint.setStrokeWidth(DEFAULT_LINE_WIDTH);
        mPaint.setStyle(Paint.Style.STROKE);
        mPath = new Path();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect rect = getBounds();

        int centerX = rect.centerX();
        int centerY = rect.centerY();

        int offset = rect.width() / 3;

        mPath.reset();
        mPath.moveTo(centerX + offset, centerY - offset);
        mPath.lineTo(centerX, centerY);
        mPath.lineTo(centerX + offset, centerY + offset);

        canvas.save();
        canvas.translate(-offset / 2, 0);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        mSize = Math.min(right - left, bottom - top);
        super.setBounds(0, 0, mSize, mSize);
    }

    @Override
    public int getIntrinsicHeight() {
        return mSize;
    }

    @Override
    public int getIntrinsicWidth() {
        return mSize;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}

