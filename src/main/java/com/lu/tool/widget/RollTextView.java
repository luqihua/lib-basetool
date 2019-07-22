package com.lu.tool.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.lu.tool.util.DimensionTools;

import java.util.ArrayList;
import java.util.List;

import lu.basetool.R;

/**
 * author: luqihua
 * date:2019-07-18
 * description: 跑马灯效果
 **/
public class RollTextView extends View implements ValueAnimator.AnimatorUpdateListener {
    private static final String TAG = "TestView";
    private static final int DIMEN_DEFAULT_CONTENT_SPACE = 100;
    private static final int DIMEN_DEFAULT_TEXT_SIZE = 16;
    private static final int DIMEN_DEFAULT_TEXT_COLOR = Color.WHITE;
    private static final float DIMEN_DEFAULT_ROLL_SPEED = 0.15f;
    //滚动方向
    public static final int ORIENTATION_HORIZONTAL = 1;
    public static final int ORIENTATION_VERTICAL = 2;

    @IntDef({ORIENTATION_HORIZONTAL, ORIENTATION_VERTICAL})
    public @interface orientation {
    }

    private Paint mPaint;
    //水平滚动还是竖直滚动
    @orientation
    private int mOrientation = ORIENTATION_HORIZONTAL;

    private int mTextSize = 14;
    private int mTextColor = Color.WHITE;
        private String mText = "hello worldjasdjaljskjlajdjalkjalksjdkjalsjlajsljdalsjkajdad";
//    private String mText = "hello world how are you";

    //需要绘制的次数   如果文本长度大于控件宽度  则为1  若小于 则需要计算
    private List<String> mTextArray = new ArrayList<>();

    private int mWidth, mHeight;
    //单个文本总高度
    private int mTextHeight;
    //单个文本宽度   总宽度
    private int mTextWidth;
    //绘制文本的基线 x y  值 滚动过程中改变的值
    private int mBaseLineX = 0, mBaseLineY;
    //初始的绘制x，y值  不改变
    private int mStartX, mStartY;
    //文本行间隔
    private int mTextLineSpace;

    //文本间隔
    private int mContentSpace;

    //========================跑马灯动画相关===================
    //是否在滚动播放
    private boolean isRolling;
    //滚动速度  px
    private float mSpeed = 0.2f;
    private ValueAnimator mRollAnimator;
    private Interpolator mRollInterpolator = new LinearInterpolator();

    public RollTextView(Context context) {
        this(context, null);
    }

    public RollTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RollTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.RollTextView);
        if (arr != null) {
            mTextSize = arr.getDimensionPixelSize(R.styleable.RollTextView_text_size, DIMEN_DEFAULT_TEXT_SIZE);
            mTextColor = arr.getColor(R.styleable.RollTextView_text_color, DIMEN_DEFAULT_TEXT_COLOR);
            mContentSpace = arr.getDimensionPixelSize(R.styleable.RollTextView_roll_content_space, DIMEN_DEFAULT_CONTENT_SPACE);
            mSpeed = arr.getFloat(R.styleable.RollTextView_roll_speed, DIMEN_DEFAULT_ROLL_SPEED);
            mOrientation = arr.getInt(R.styleable.RollTextView_roll_orientation, ORIENTATION_HORIZONTAL);
            arr.recycle();
        }
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        init();
    }

    private void init() {
        initPaint();
        if (mOrientation == ORIENTATION_HORIZONTAL) {
            initHorizontalValue();
        } else if (mOrientation == ORIENTATION_VERTICAL) {
            initVerticalValue();
        }
        initAnimator();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint.setTextSize(DimensionTools.sp2px(mTextSize));
        mPaint.setColor(mTextColor);
    }

    /**
     * 初始化水平滚动的一些尺寸值
     */
    private void initHorizontalValue() {
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        mBaseLineX = mStartX = mWidth;
        mBaseLineY = mStartY = (int) (mHeight / 2 - (metrics.ascent + metrics.descent) / 2);
        mTextWidth = (int) mPaint.measureText(mText);
    }

    /**
     * 初始化竖直滚动的一些尺寸值
     */
    private void initVerticalValue() {
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        mTextLineSpace = (int) mPaint.getFontSpacing();
        //将长文本按照最大长度为控件宽度分割成若干行
        char[] chars = mText.toCharArray();
        int start = 0;
        do {
            int count = mPaint.breakText(chars, start, chars.length - start, mWidth, null);
            mTextArray.add(mText.substring(start, start + count));
            start += count;
        } while (start < chars.length);

        mBaseLineX = mStartX = 0;
        //文本总的高度
        mTextHeight = (mTextLineSpace) * mTextArray.size();
        //第一行文本的baseLine
        mBaseLineY = mStartY = (int) (mHeight -metrics.top);
    }

    /**
     * 初始化跑马灯滚动属性
     */
    private void initAnimator() {
        stopRoll();
        int startValue = 0;
        int endValue = 0;
        if (mOrientation == ORIENTATION_HORIZONTAL) {
            endValue = mBaseLineX + mTextWidth;
        } else if (mOrientation == ORIENTATION_VERTICAL) {
            endValue = mBaseLineY + mTextHeight;
        }

        if (TextUtils.isEmpty(mText)) return;
        mRollAnimator = ValueAnimator.ofInt(startValue, endValue);

        mRollAnimator.setDuration((long) (Math.abs(endValue - startValue) / mSpeed));
        mRollAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mRollAnimator.setRepeatMode(ValueAnimator.RESTART);
        //线性滚动
        mRollAnimator.setInterpolator(mRollInterpolator);
        mRollAnimator.addUpdateListener(this);
        isRolling = true;
        mRollAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (!isRolling) {
            animation.cancel();
            return;
        }
        int value = (int) animation.getAnimatedValue();
        if (mOrientation == ORIENTATION_HORIZONTAL) {
            mBaseLineX = mStartX - value;
        } else if (mOrientation == ORIENTATION_VERTICAL) {
            mBaseLineY = mStartY - value;
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mOrientation == ORIENTATION_HORIZONTAL) {
            canvas.drawText(mText, mBaseLineX, mBaseLineY, mPaint);
        } else if (mOrientation == ORIENTATION_VERTICAL) {
            for (int j = 0; j < mTextArray.size(); j++) {
                canvas.drawText(mTextArray.get(j), mBaseLineX, mBaseLineY + j * mTextLineSpace, mPaint);
            }
        }
    }

    //===============================================================

    public void setText(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (this.mText.equals(text)) return;
            stopRoll();
            this.mText = text;
            initAnimator();
        } else {
            stopRoll();
        }
    }

    public void startRoll() {
        if (!isRolling) {
            init();
        }
    }

    public void stopRoll() {
        if (isRolling && mRollAnimator != null) {
            isRolling = false;
            mRollAnimator.cancel();
            mRollAnimator = null;
        }
    }

    /**
     * 设置滚动速度
     *
     * @param speed 0.1px~1px/ms
     */
    public void setSpeed(float speed) {
        if (this.mSpeed == speed) return;
        stopRoll();
        this.mSpeed = speed < 0 ? 0.1f : (speed > 1 ? 1 : speed);
    }

    public void setRollInterpolator(Interpolator interpolator) {
        stopRoll();
        this.mRollInterpolator = interpolator;
    }

    public void setTextSize(int spSize) {
        if (this.mTextSize == spSize) return;
        stopRoll();
        this.mTextSize = spSize;
    }


    public void setTextColor(int color) {
        if (this.mTextColor == color) return;
        stopRoll();
        this.mTextColor = color;
    }

    public void setContentSpace(int pxSpace) {
        if (this.mContentSpace == pxSpace) return;
        stopRoll();
        this.mContentSpace = pxSpace;
    }

    public void setOrientation(@orientation int orientation) {
        if (this.mOrientation == orientation) return;
        stopRoll();
        this.mOrientation = orientation;
    }
}
