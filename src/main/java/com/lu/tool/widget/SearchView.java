package com.lu.tool.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;

import java.lang.ref.WeakReference;


/**
 * Created by lqh on 2016/4/11.
 */
public class SearchView extends AppCompatEditText implements
        OnFocusChangeListener, TextWatcher {

    public final static int TYPE_DELETE = 0x111;
    public final static int TYPE_SEARCH = 0x112;

    @IntDef({TYPE_DELETE, TYPE_SEARCH})
    public @interface IconType {
    }

    private final int DEFAULT_PADDING = 10;//dp
    private final int DEFAULT_DRAWABLE_PADDING = 10;//dp
    private final int DEFAULT_RECT_COLOR = Color.WHITE;
    private Context mContext;

    private int mWidth, mHeight;
    private Drawable mClearDrawable, mSearchDrawable;
    private RectF mBGCRect;
    private Paint mPaint;
    private boolean hasFocus;

    private OnTextChange onTextChange;
    private TextChangeTask textChangeTask;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.editTextStyle);
        this.mContext = context;
        init();
    }

    private void init() {
        setCompoundDrawablePadding(DEFAULT_DRAWABLE_PADDING);
        setBackground(null);
        setSingleLine(true);
        setMaxLines(1);

        //初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(DEFAULT_RECT_COLOR);

        // 获取editText的DrawableRight,如果没有设置我们就使用默认的图片
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
            mClearDrawable = new IconDrawable();
        }
        mSearchDrawable = getCompoundDrawables()[0];
        if (mSearchDrawable == null) {
            mSearchDrawable = new IconDrawable(TYPE_SEARCH, 0);
        }

        setClearIconVisible(false);
        // 设置焦点改变的监听
        setOnFocusChangeListener(this);
        // 设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mBGCRect = new RectF(0, getPaddingTop(), mWidth, mHeight - getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mBGCRect.offsetTo(getScrollX(), getPaddingTop());
        canvas.drawRoundRect(mBGCRect, mHeight / 4, mHeight / 4, mPaint);
        super.onDraw(canvas);
    }

    /**
     * 设置清楚图标显示与隐藏，调用setCompoundDrawable为EditText绘制上去
     *
     * @param visible
     */

    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawablesWithIntrinsicBounds(mSearchDrawable,
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);

    }

    /**
     * 通过判断editText被点击的位置是否是右侧的删除图标来设置时间
     *
     * @param event
     * @return
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Drawable deleteDw = getCompoundDrawables()[2];
                if (deleteDw != null) {
                    int width = deleteDw.getBounds().width();
                    int x = (int) event.getX();
                    int rightOffset = mWidth - getPaddingRight();
                    boolean touchAble = x > (rightOffset - width) && x < rightOffset;
                    if (touchAble) {
                        this.setText("");
                    }
                }
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);

    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        setClearIconVisible(getText().length() > 0);
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!hasFocus) return;
        setClearIconVisible(s.length() > 0);
        if (onTextChange == null) return;
        removeCallbacks(textChangeTask);
        textChangeTask.setText(s);
        postDelayed(textChangeTask, 500);
    }

    private static class TextChangeTask implements Runnable {
        private CharSequence text;

        private WeakReference<OnTextChange> weakReference;


        public TextChangeTask(OnTextChange onTextChange) {
            this.weakReference = new WeakReference<>(onTextChange);
        }

        public void setText(CharSequence text) {
            this.text = text;
        }
        @Override
        public void run() {
            OnTextChange onTextChange = weakReference.get();
            if (onTextChange != null)
                onTextChange.onTextChanged(text);
        }

    }


    public void setOnTextChange(OnTextChange onTextChange) {
        this.onTextChange = onTextChange;
        this.textChangeTask = new TextChangeTask(onTextChange);
    }

    //输入发生变化的监听
    public interface OnTextChange {
        void onTextChanged(CharSequence s);
    }


    /*=========================*/
    public static class IconDrawable extends Drawable {

        private static final int DEFAULT_SIZE = 60;
        private static final int DEFAULT_LINE_WIDTH = 4;
        private static final int DEFAULT_ICON_COLOR = 0xff707070;
        private float mLineRate = 2.0f;

        private Paint mPaint;
        private Path mPath;
        private int mSize = DEFAULT_SIZE;
        private int mIconColor;

        @IconType
        private int mIconType;

        public IconDrawable() {
            this(TYPE_DELETE, DEFAULT_ICON_COLOR);
        }

        public IconDrawable(@IconType int iconType, int iconColor) {
            this.mIconType = iconType;
            this.mIconColor = iconColor == 0 ? DEFAULT_ICON_COLOR : iconColor;
            this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStrokeWidth(DEFAULT_LINE_WIDTH);
            this.mPath = new Path();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            switch (mIconType) {
                case TYPE_DELETE:
                    drawDeleteIcon(canvas);
                    break;
                case TYPE_SEARCH:
                    drawSearchIcon(canvas);
                    break;
            }
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


        private void drawDeleteIcon(Canvas canvas) {
            int centerX = getBounds().centerX();

            //画圆形背景
            mPaint.setColor(DEFAULT_ICON_COLOR);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(centerX, centerX, centerX, mPaint);

            //画第一条斜线
            int start = (int) (centerX - centerX / mLineRate);//左侧起点坐标值(x和y值相等)
            int end = (int) (centerX + centerX / mLineRate);//右下角坐标值(x和y值相等)

            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.STROKE);
            mPath.reset();
            mPath.moveTo(start, start);
            mPath.lineTo(end, end);
            canvas.drawPath(mPath, mPaint);

            //画第二条斜线
            mPath.reset();
            mPath.moveTo(end, start);
            mPath.lineTo(start, end);
            canvas.drawPath(mPath, mPaint);
        }


        private void drawSearchIcon(Canvas canvas) {
            int centerX = getBounds().centerX();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mIconColor);
            mPath.reset();
            mPath.moveTo(centerX + centerX * 3 / 4, centerX + centerX * 3 / 4);
            mPath.lineTo(centerX + centerX / 2 - 4, centerX + centerX / 2 - 4);
            mPath.addCircle(centerX, centerX, centerX / 2, Path.Direction.CW);
            canvas.drawPath(mPath, mPaint);
        }

    }

}
