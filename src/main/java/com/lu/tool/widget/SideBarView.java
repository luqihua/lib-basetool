package com.lu.tool.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by lqh on 2016/4/11.
 */
public class SideBarView extends View {

    /**
     * 26个首字母
     **/
    private static final String[] b = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z"};

    private int mWidth, mHeight, mSingleH, mXPos;

    private TextView mTextDialog;

    private Paint mPaint;// 画笔
    private int choose = -1;

    /**
     * 触摸事件
     **/
    private OnLetterChangedListener mListener;

    public SideBarView(Context context) {
        this(context, null);
    }

    public SideBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initP();
    }

    private void initP() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(30);
        mPaint.setColor(Color.BLACK);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void reSetP() {
        if (mPaint != null) {
            mPaint.setTextSize(30);
            mPaint.setColor(Color.BLACK);
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        /** 获取每一个字母的高度 **/
        mSingleH = h / b.length;
        mXPos = mWidth / 2;
    }

    /**
     * 为SideBar设置显示字母的TextView
     *
     * @param mTextDialog
     */
    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < b.length; i++) {
            float yPos = mSingleH * i + mSingleH;
            /** 选中状态 **/
            if (i == choose) {
                mPaint.setColor(Color.parseColor("#3399ff"));
                mPaint.setFakeBoldText(true);
                canvas.drawText(b[i], mXPos, yPos, mPaint);
                reSetP();
                continue;
            }
            // x坐标等于中间字符串宽度的一半
            canvas.drawText(b[i], mXPos, yPos, mPaint);
        }
    }

    /**
     * 点击监听
     *
     * @param event
     * @return
     */

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final float y = event.getY();// 点击点的y坐标
        /** 点击y坐标所占的总高度的比例*b的个数就是点中b的position **/
        int c = (int) (y / mHeight * b.length);
        c = c < 0 ? 0 : c >= b.length ? b.length-1 : c;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (c != choose) {
                    if (mListener != null) {
                        mListener.onLetterChanged(b[c]);
                    }

                    if (mTextDialog != null) {
                        mTextDialog.setText(b[c]);
                        mTextDialog.setVisibility(View.VISIBLE);
                    }
                    choose = c;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                choose = -1;
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.GONE);
                }
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 向外公开的方法
     *
     * @param onLetterChangedListener
     */
    public void setLetterChangedListener(
            OnLetterChangedListener onLetterChangedListener) {
        this.mListener = onLetterChangedListener;
    }

    /**
     * 接口
     *
     * @author coder
     */
    public interface OnLetterChangedListener {
        void onLetterChanged(String s);
    }
}
