package com.lu.tool.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.text.DecimalFormat;

import lu.basetool.R;


/**
 * @author luzeyan
 * @time 2017/12/7 上午9:56
 * @description 倒计时
 */


public class CountDownView extends View {
    private static final String DEFAULT_FINISH_TEXT = "已逾期";//结束时显示的文字
    private static final int DEFAULT_FINISH_TEXT_COLOR = Color.WHITE;
    private static final int DEFAULT_FINISH_TEXT_BGC_COLOR = 0xfff40009;
    /**
     * 分隔符类型
     * COLON  :
     */
    private static final int COLON = 0x011;
    /**
     * 分隔符类型
     * CHAR   天、时、分、秒
     */
    private static final int CHAR = 0x012;

    /**
     * 默认文字颜色
     */
    private static final int DEFAULT_TIME_TEXT_COLOR = 0xffffffff;
    /**
     * 默认分隔符颜色
     */
    private static final int DEFAULT_SUFFIX_TEXT_COLOR = 0xff2e2e2e;
    /**
     * 默认背景颜色
     */
    private static final int DEFAULT_TIME_BG_COLOR = 0xfffc4f09;
    /**
     * 默认文本字体大小   sp
     */
    private static final int DEFAULT_TEXT_SIZE = 14;
    /**
     * 默认文字背景圆角   dp
     */
    private static final int DEFAULT_TIME_BG_CORNER = 2;
    /**
     * 有背景时文本离边框距离  dp
     */
    private static final int DEFAULT_TIME_PADDING = 2;
    /**
     * 默认分割符与文本间距  dp
     */
    private static final int DEFAULT_SUFFIX_MARGIN = 2;

    private static DecimalFormat sDecimalFormat;


    static {
        sDecimalFormat = new DecimalFormat("00");
    }

    private String mDay, mHour, mMinute, mSecond;

    private String mSuffixDay, mSuffixHour, mSuffixMinute, mSuffixSecond;

    private int showLevel;

    private int mSuffixTextColor, mTimeTextColor, mTimeBgColor;

    private float mTextSize;

    private float mTimePadding, mSuffixMargin;

    private int mSuffixType;

    private float mTimeBgCorner;

    private boolean isDrawBg;
    private Context mContext;

    private int mWidth, mHeight;

    private CountDownTimer mDownTimer;
    private Paint mTimePaint;
    private Paint mBgPaint;
    private Paint mSuffixPaint;


    private RectF mTimeBgRectF;

    private float mTimeWidth;
    private float mSuffixWidth;
    private float mBaseLine;//绘制文字基准线


    private String mFinishText = DEFAULT_FINISH_TEXT;
    private boolean isFinish = false;
    private Paint mFinishPaint;
    private int mFinishTextWidth;


    public CountDownView(Context context) {
        this(context, null);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        initAttrs(context, attrs);

        initPaint();

        initTimeInfo();

        updateTime(0);
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CountdownView);
            mTextSize = array.getDimension(R.styleable.CountdownView_Text_Size, sp2px(DEFAULT_TEXT_SIZE));
            mSuffixMargin = array.getDimension(R.styleable.CountdownView_suffixMargin, sp2px(DEFAULT_SUFFIX_MARGIN));
            mTimeBgCorner = array.getDimension(R.styleable.CountdownView_timeBgCorner, dp2px(DEFAULT_TIME_BG_CORNER));
            mTimeTextColor = array.getColor(R.styleable.CountdownView_timeTextColor, DEFAULT_TIME_TEXT_COLOR);
            mSuffixTextColor = array.getColor(R.styleable.CountdownView_suffixTextColor, DEFAULT_SUFFIX_TEXT_COLOR);
            mTimeBgColor = array.getColor(R.styleable.CountdownView_timeBgColor, DEFAULT_TIME_BG_COLOR);
            showLevel = array.getInt(R.styleable.CountdownView_showLevel, 0);
            mSuffixType = array.getInt(R.styleable.CountdownView_suffixType, CHAR);

            isDrawBg = array.hasValue(R.styleable.CountdownView_timeBgColor);

            if (!isDrawBg) {
                mSuffixMargin = 0;
                mTimePadding = 0;
            }

            array.recycle();
        }
    }

    private void initPaint() {
        mTimePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimePaint.setTextSize(mTextSize);
        mTimePaint.setColor(mTimeTextColor);
        mTimePaint.setStrokeWidth(dp2px(1));

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(mTimeBgColor);

        mSuffixPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSuffixPaint.setTextSize(mTextSize);
        mSuffixPaint.setColor(mSuffixTextColor);
        mSuffixPaint.setStrokeWidth(dp2px(1));


        mFinishPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFinishPaint.setColor(DEFAULT_FINISH_TEXT_BGC_COLOR);
        mFinishPaint.setStyle(Paint.Style.FILL);
        mFinishPaint.setTextSize(mTextSize);
    }


    private void initTimeInfo() {

        if (mSuffixType == COLON) {
            mSuffixDay = ":";
            mSuffixHour = ":";
            mSuffixMinute = ":";
            mSuffixSecond = "";
        } else {
            mSuffixDay = "天";
            mSuffixHour = "时";
            mSuffixMinute = "分";
            mSuffixSecond = "秒";
        }

        mTimePadding = dp2px(DEFAULT_TIME_PADDING);
        mSuffixMargin = dp2px(DEFAULT_SUFFIX_MARGIN);

        mTimeWidth = mTimePaint.measureText("00") + mTimePadding * 2;
        mSuffixWidth = mSuffixPaint.measureText(mSuffixMinute) + mSuffixMargin * 2;

        mBaseLine = (mTimeWidth - mTimePaint.ascent() - mTimePaint.descent()) / 2;

        mTimeBgRectF = new RectF();


        mWidth = measureWidth();
        mHeight = (int) mTimeWidth;

        mFinishTextWidth = (int) (mSuffixPaint.measureText(mFinishText) + mTimePadding * 4);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }

    /**
     * 测控件宽度
     */
    private int measureWidth() {
        int width = 0;
        if (showLevel >= 0) {
            width += mTimeWidth;
            if (!TextUtils.isEmpty(mSuffixSecond)) {
                width += mSuffixWidth;
            }
        }

        if (showLevel >= 1) {
            width += mTimeWidth + mSuffixWidth;
        }

        if (showLevel >= 2) {
            width += mTimeWidth + mSuffixWidth;
        }

        if (showLevel >= 3) {
            width += mTimeWidth + mSuffixWidth;
        }
        return width;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (isFinish) {
            drawFinishText(canvas);
        } else {
            drawTime(canvas);
        }
    }

    private void drawFinishText(Canvas canvas) {
        mTimeBgRectF.set(0, 0, mWidth, mHeight);
        mFinishPaint.setColor(DEFAULT_FINISH_TEXT_BGC_COLOR);
        canvas.drawRoundRect(mTimeBgRectF, mTimeBgCorner, mTimeBgCorner, mFinishPaint);
        mFinishPaint.setColor(mTimeTextColor);
        canvas.drawText(mFinishText, mTimePadding * 2, mBaseLine, mFinishPaint);
    }

    private void drawTime(Canvas canvas) {
        float left = 0;
        float right = mTimeWidth;
        float bottom = getHeight();

        if (showLevel >= 3) {
            if (isDrawBg) {
                mTimeBgRectF.set(left, 0, right, bottom);
                canvas.drawRoundRect(mTimeBgRectF, mTimeBgCorner, mTimeBgCorner, mBgPaint);
            }
            canvas.drawText(mDay, left + mTimePadding, mBaseLine, mTimePaint);
            canvas.drawText(mSuffixDay, right + mSuffixMargin, mBaseLine, mSuffixPaint);

            left = right + mSuffixWidth;
            right = left + mTimeWidth;
        }

        if (showLevel >= 2) {

            if (isDrawBg) {
                mTimeBgRectF.set(left, 0, right, bottom);
                canvas.drawRoundRect(mTimeBgRectF, mTimeBgCorner, mTimeBgCorner, mBgPaint);
            }

            canvas.drawText(mHour, left + mTimePadding, mBaseLine, mTimePaint);
            canvas.drawText(mSuffixHour, right + mSuffixMargin, mBaseLine, mSuffixPaint);

            left = right + mSuffixWidth;
            right = left + mTimeWidth;
        }

        if (showLevel >= 1) {

            if (isDrawBg) {
                mTimeBgRectF.set(left, 0, right, bottom);
                canvas.drawRoundRect(mTimeBgRectF, mTimeBgCorner, mTimeBgCorner, mBgPaint);
            }
            canvas.drawText(mMinute, left + mTimePadding, mBaseLine, mTimePaint);
            canvas.drawText(mSuffixMinute, right + mSuffixMargin, mBaseLine, mSuffixPaint);

            left = right + mSuffixWidth;
            right = left + mTimeWidth;
        }

        if (showLevel >= 0) {
            if (isDrawBg) {
                mTimeBgRectF.set(left, 0, right, mTimeWidth);
                canvas.drawRoundRect(mTimeBgRectF, mTimeBgCorner, mTimeBgCorner, mBgPaint);
            }
            canvas.drawText(mSecond, left + mTimePadding, mBaseLine, mTimePaint);
            if (!TextUtils.isEmpty(mSuffixSecond)) {
                canvas.drawText(mSuffixSecond, right + mSuffixMargin, mBaseLine, mSuffixPaint);
            }
        }
    }


    public void setFinishText(String finishText) {
        this.mFinishText = finishText;
    }

    /**
     * 以结束时间开始倒计时间
     *
     * @param endTimeStamp 结束时间  millisecond
     */
    public void startEndTime(long endTimeStamp) {
        start(endTimeStamp - System.currentTimeMillis());
    }

    /**
     * 开始倒计时
     *
     * @param timeStamp millisecond
     */
    public void start(long timeStamp) {

        cancelTimer();

//        timeStamp += BaseApp.sTimeDiff;

        //当剩余时间<1000ms时  视为结束
        if (timeStamp <= 1000) {
            finish();
            return;
        } else {
            isFinish = false;
            mWidth = measureWidth();
            requestLayout();
        }
        mDownTimer = new CountDownTimer(timeStamp, 1000 - 10) {

            @Override
            public void onTick(long millisUntilFinished) {
                updateTime(millisUntilFinished + 15);
            }

            @Override
            public void onFinish() {
                finish();
                if (mOnCountDownListener != null) {
                    mOnCountDownListener.onFinish(CountDownView.this);
                }
            }
        };
        mDownTimer.start();
    }


    private void finish() {
        isFinish = true;
        mWidth = mFinishTextWidth;
        requestLayout();
    }

    /**
     * 取消倒计时
     */
    public void cancelTimer() {
        if (mDownTimer != null) {
            mDownTimer.cancel();
            mDownTimer = null;
        }
    }

    public void updateTime(long ms) {
        int day = (int) (ms / (1000 * 60 * 60 * 24));
        int hour = (int) ((ms % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        int minute = (int) ((ms % (1000 * 60 * 60)) / (1000 * 60));
        int second = (int) ((ms % (1000 * 60)) / 1000);

        mDay = sDecimalFormat.format(day);
        mHour = sDecimalFormat.format(hour);
        mMinute = sDecimalFormat.format(minute);
        mSecond = sDecimalFormat.format(second);

        invalidate();
    }


    private OnCountDownListener mOnCountDownListener;

    public void setOnCountDownListener(OnCountDownListener onCountDownListener) {
        mOnCountDownListener = onCountDownListener;
    }

    /**
     * 倒计时结束回调监听
     */
    public interface OnCountDownListener {
        void onFinish(CountDownView cv);
    }

    public int dp2px(float dpValue) {
        float density = mContext.getResources().getDisplayMetrics().density;
        return (int) (density * dpValue + 0.5f);
    }

    public float sp2px(int spValue) {
        float density = mContext.getResources().getDisplayMetrics().scaledDensity;
        return density * spValue;
    }
}
