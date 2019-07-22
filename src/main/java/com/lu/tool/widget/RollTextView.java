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

import com.lu.tool.util.DeviceUtil;

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
    private static final float DIMEN_DEFAULT_ROLL_SPEED = 0.2f;
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
//    private String mText = "hello worldjasdjaljskjlajdjalkjalksjdkjalsjlajsljdalsjkajdad";
    private String mText = "hello world how are you";

    //需要绘制的次数   如果文本长度大于控件宽度  则为1  若小于 则需要计算
    private List<String> mTextArray = new ArrayList<>();
    private int mDrawCount = 1;

    private int mWidth, mHeight;
    //单个文本总高度  以及控件当前显示的(显示一小部分的也当成完整文本计算)总的文本高度
    private int mTextHeight, mTotalTextHeight;
    //单个文本宽度   总宽度
    private int mTextWidth, mTotalTextWidth;
    //绘制文本的基线 x y  值
    private int mBaseLineX = 0, mBaseLineY;
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
        setBackgroundColor(Color.BLACK);
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
        mPaint.setTextSize(DeviceUtil.sp2px(mTextSize));
        mPaint.setColor(mTextColor);
    }

    /**
     * 初始化水平滚动的一些尺寸值
     */
    private void initHorizontalValue() {
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        mBaseLineX = 0;
        mBaseLineY = (int) (mHeight / 2 - (metrics.ascent + metrics.descent) / 2);
        mTextWidth = (int) mPaint.measureText(mText);
        mDrawCount = mWidth / mTextWidth + 1;
        mTotalTextWidth = (mTextWidth + mContentSpace) * mDrawCount;
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

        //文本总的高度
        mTextHeight = (mTextLineSpace) * mTextArray.size();
        mDrawCount = mHeight / mTextHeight + 1;
        mTotalTextHeight = mDrawCount * (mTextHeight + mContentSpace);
        //第一行文本的baseLine
        mBaseLineX = 0;
        mBaseLineY = (int) -metrics.top;
    }

    /**
     * 初始化跑马灯滚动属性
     */
    private void initAnimator() {
        stopRoll();
        int endValue = 0;
        if (mOrientation == ORIENTATION_HORIZONTAL) {
            endValue = mTotalTextWidth;
        } else if (mOrientation == ORIENTATION_VERTICAL) {
            endValue = mTotalTextHeight;
        }

        if (TextUtils.isEmpty(mText)) return;
        mRollAnimator = ValueAnimator.ofInt(0, endValue);
        mRollAnimator.setDuration((long) (endValue / mSpeed));
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
            mBaseLineX = -value;
        } else if (mOrientation == ORIENTATION_VERTICAL) {
            mBaseLineY = -value;
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mOrientation == ORIENTATION_HORIZONTAL) {
            drawTextHorizontal(canvas);
        } else if (mOrientation == ORIENTATION_VERTICAL) {
            drawTextVertical(canvas);
        }

        canvas.save();
        if (mOrientation == ORIENTATION_HORIZONTAL) {
            canvas.translate(-mTotalTextWidth, 0);
            drawTextHorizontal(canvas);
        } else if (mOrientation == ORIENTATION_VERTICAL) {
            canvas.translate(0, -mTotalTextHeight);
            drawTextVertical(canvas);
        }
        canvas.restore();

        canvas.save();
        if (mOrientation == ORIENTATION_HORIZONTAL) {
            canvas.translate(mTotalTextWidth, 0);
            drawTextHorizontal(canvas);
        } else if (mOrientation == ORIENTATION_VERTICAL) {
            canvas.translate(0, mTotalTextHeight);
            drawTextVertical(canvas);
        }
        canvas.restore();
    }

    private void drawTextHorizontal(Canvas canvas) {
        for (int i = 0; i < mDrawCount; i++) {
            int offset = i * (mTextWidth + mContentSpace);
            canvas.drawText(mText, mBaseLineX + offset, mBaseLineY, mPaint);
        }
    }


    private void drawTextVertical(Canvas canvas) {
        //第一层循环是文本绘制的次数
        for (int i = 0; i < mDrawCount; i++) {
            int offset = i * (mTextHeight + mContentSpace);
            //第二层循环是逐行把文本绘制出来
            for (int j = 0; j < mTextArray.size(); j++) {
                canvas.drawText(mTextArray.get(j), mBaseLineX, offset + mBaseLineY + j * mTextLineSpace, mPaint);
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
