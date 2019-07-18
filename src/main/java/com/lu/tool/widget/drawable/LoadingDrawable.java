package com.lu.tool.widget.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.LinkedList;

/**
 * Author: luqihua
 * Time: 2017/10/13
 * Description: LoadingDrawable
 */

public class LoadingDrawable extends Drawable implements Animatable {
    private static final int DEFAULT_SIZE = 100;//drawable的默认长宽
    private static final int DEFAULT_COLOR = 0xffffffff;//指针初始颜色
    private static final int COUNT = 12;//小指针个数
    private static final int DEGREE = 360 / COUNT;//每个指针相差角度
    private static final int DEFAULT_POINT_WIDTH = 4;//指针宽度
    private static final int DEFAULT_POINT_LENGTH = DEFAULT_SIZE / 5;//指针长度
    private static final int PERIOD = 100;//多少ms转动一次
    private Paint mPaint;
    private RectF mPoint;

    private int mSize = DEFAULT_SIZE;
    private LinkedList<Integer> sAlpha = new LinkedList<>();//用于存储每个小指针对应的透明度
    private boolean isStop;

    public LoadingDrawable() {
        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < COUNT; i++) {
            sAlpha.add(15 + 240 / COUNT * i);
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mPoint == null) {
            int centerX = mSize / 2;
            mPoint = new RectF(
                    centerX - DEFAULT_POINT_WIDTH / 2,
                    DEFAULT_POINT_LENGTH / 2 + 2,
                    centerX + DEFAULT_POINT_WIDTH / 2,
                    DEFAULT_POINT_LENGTH / 2 * 3 + 2);
        }
        mPaint.setColor(DEFAULT_COLOR);
        canvas.save();
        for (int i = 0; i < COUNT; i++) {
            mPaint.setAlpha(sAlpha.get(i));
            if (i > 0)
                canvas.rotate(DEGREE, mSize / 2, mSize / 2);
            canvas.drawRoundRect(mPoint, DEFAULT_POINT_WIDTH / 2, DEFAULT_POINT_WIDTH / 2, mPaint);
        }

        canvas.restore();
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            sAlpha.addFirst(sAlpha.removeLast());
            invalidateSelf();
            if (!isStop)
                mHandler.sendEmptyMessageDelayed(1, PERIOD);
        }
    };

    @Override
    public void start() {
        isStop = false;
        mHandler.sendEmptyMessage(1);
    }

    @Override
    public void stop() {
        isStop = true;
    }

    @Override
    public boolean isRunning() {
        return !isStop;
    }

    @Override
    public void setBounds(@NonNull Rect bounds) {
        mSize = Math.min(bounds.width(), bounds.height());
        setBounds(0, 0, mSize, mSize);
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
