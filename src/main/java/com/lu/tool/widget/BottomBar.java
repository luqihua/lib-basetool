package com.lu.tool.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lqh on 2016/5/10.
 */
public class BottomBar extends LinearLayout {

    private String[] mTabNames;

    private int[] mUnCheckTabIcons;
    private int[] mCheckTabIcons;

    private final int mTxtColor[] = {Color.parseColor("#50ba26"), Color.parseColor("#555555")};

    private Context mContext;
    private ViewPager mViewPager;
    private List<BottomTab> mChildTabs = new ArrayList<>();
    private int mCurrentIndex;

    public BottomBar(Context context) {
        this(context, null);
    }

    public BottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initSelf();
    }

    private void initSelf() {
        setOrientation(HORIZONTAL);
    }

    private void createTab() {
        removeAllViews();
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        int len = mTabNames.length;
        for (int i = 0; i < len; i++) {
            BottomTab tab = new BottomTab(mContext);
            if (i == 0) tab.setAlpha(255);
            tab.setText(mTabNames[i]);
            tab.setTextColor(mTxtColor);
            tab.setDrawable(mCheckTabIcons[i], mUnCheckTabIcons[i]);
            tab.setGravity(Gravity.CENTER);
            tab.setLayoutParams(params);
            tab.setOnClickListener(mItemClickListener);
            addView(tab);
            mChildTabs.add(tab);
        }
    }


    public void setTabs(String[] tabNames, int[] checkTabIcons, int[] unCheckTabIcons) {
        if (tabNames.length != checkTabIcons.length || checkTabIcons.length != unCheckTabIcons.length) {
            throw new RuntimeException("tab name size must = icons size");
        }
        this.mTabNames = tabNames;
        this.mCheckTabIcons = checkTabIcons;
        this.mUnCheckTabIcons = unCheckTabIcons;
        createTab();
    }

    public void setViewPager(ViewPager viewPager) {
        if (viewPager == null) return;
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(mPageChangeListener);
    }


    private OnClickListener mItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = mChildTabs.indexOf(v);
            if (index == mCurrentIndex) return;
            setClickedViewChecked(mCurrentIndex, index);
            if (mViewPager != null) {
                mViewPager.setCurrentItem(index, false);
            }
            mCurrentIndex = index;
        }
    };


    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            updateGradient(position, positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            mCurrentIndex = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void updateGradient(int position, float offset) {
        if (offset > 0) {
            mChildTabs.get(position).updateAlpha(255 * (1 - offset));
            mChildTabs.get(position + 1).updateAlpha(255 * offset);
        }
    }

    private void setClickedViewChecked(int oldIndex, int newIndex) {
        mChildTabs.get(oldIndex).setChecked(false);
        mChildTabs.get(newIndex).setChecked(true);
    }

    /**
     * 设置选中tab
     *
     * @param index
     */
    public void setCurrentIndex(int index) {
        if (mCurrentIndex == index || index >= mChildTabs.size() || index < 0) return;
        setClickedViewChecked(mCurrentIndex, index);
        mCurrentIndex = index;
    }

    /**
     * 获取当前选中的下标
     *
     * @return
     */
    public int getCurrentIndex() {
        return mCurrentIndex;
    }


    /**
     * tab 内部类
     */

    protected class BottomTab extends AppCompatTextView {

        private static final float DEFAULT_ICON_RATIO = 0.6f;
        private static final float DEFAULT_TEXT_RATIO = 0.3f;
        private static final int DEFAULT_PADDING = 10;

        private Context mContext;
        /*尺寸*/
        private int mWidth, mHeight;
        private int mIconWidth, mIconHeight;
        /*文本*/
        private String mText;
        private int mCheckTxtColor;
        private int mUnCheckTxtColor;
        private float mTextBaseY;
        /*画笔*/
        private Paint mTextPaint;
        private Paint mCheckPaint;
        private Paint mUnCheckPaint;

        private Bitmap mCheckBitmap;
        private Bitmap mUnCheckBitmap;
        private Drawable mCheckDrawable;
        private Drawable mUnCheckDrawable;

        private int mAlpha;

        public BottomTab(Context context) {
            this(context, null);
        }

        public BottomTab(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public BottomTab(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            this.mContext = context;
            initSelf();
            initPaint();
        }

        private void initPaint() {
            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mCheckPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mUnCheckPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setTextSize(getTextSize());
        }

        private void initSelf() {
            this.mText = getText().toString();
            //由于图标四周一般会有一圈透明的四边，所以上部padding看上去要大一些，
            // 解决方法就是底部的padding添加一个5的偏移量(值可以根据视图自行定义)
            setPadding(0, DEFAULT_PADDING, 0, DEFAULT_PADDING + 5);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (mWidth == 0) {
                this.mWidth = w;
                this.mHeight = h - getPaddingTop() - getPaddingBottom();//内容区域高度
            /*--*/
                initDrawable();
            /*--*/
                mTextBaseY = h - getPaddingBottom();
                if (TextUtils.isEmpty(mText)) mText = getText().toString();
                mTextPaint.setTextSize(mHeight * DEFAULT_TEXT_RATIO);
            }
        }


        /**
         * 设置选中和未选中的文字颜色
         *
         * @param colors
         */
        public void setTextColor(int[] colors) {
            this.mCheckTxtColor = colors[0];
            this.mUnCheckTxtColor = colors[1];
        }

        /**
         * 设置选中和未选中的背景图片
         *
         * @param checkDrawableId
         * @param unCheckDrawableId
         */
        public void setDrawable(@NonNull int checkDrawableId, @NonNull int unCheckDrawableId) {
            mCheckDrawable = ContextCompat.getDrawable(mContext, checkDrawableId);
            mUnCheckDrawable = ContextCompat.getDrawable(mContext, unCheckDrawableId);
        }

        private void initDrawable() {
            if (mCheckDrawable == null || mUnCheckDrawable == null) {
                throw new RuntimeException("'focus_icon' and 'defocus_icon' attribute should be defined");
            }
            getIconSize(mCheckDrawable);
            mCheckDrawable.setBounds(0, 0, mIconWidth, mIconHeight);
            mUnCheckDrawable.setBounds(0, 0, mIconWidth, mIconHeight);
            mCheckBitmap = getBitmapFromDrawable(mCheckDrawable);
            mUnCheckBitmap = getBitmapFromDrawable(mUnCheckDrawable);
        }

        /**
         * 求出压缩后的图标的宽高，保持原图宽高比
         *
         * @param drawable
         */
        private void getIconSize(Drawable drawable) {
            int height = drawable.getIntrinsicHeight();
            int width = drawable.getIntrinsicWidth();
            float size = mHeight * DEFAULT_ICON_RATIO;

            float ratio = size * 1.0f / height;

            mIconWidth = (int) (width * ratio);
            mIconHeight = (int) (height * ratio);
        }

        private Bitmap getBitmapFromDrawable(Drawable drawable) {
            Bitmap bitmap = Bitmap.createBitmap(mIconWidth, mIconHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            if (drawable instanceof BitmapDrawable) {
                drawable.draw(canvas);
                return bitmap;
            }
            return null;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            drawUnfocusedIcon(canvas);
            drawFocusIcon(canvas);
            drawUnfocusedText(canvas);
            drawFocusText(canvas);
        }

        private void drawUnfocusedIcon(Canvas canvas) {
            mUnCheckPaint.setAlpha(255 - mAlpha);
            canvas.drawBitmap(mUnCheckBitmap, (mWidth - mIconWidth) / 2, getPaddingTop(), mUnCheckPaint);
        }

        private void drawFocusIcon(Canvas canvas) {
            mCheckPaint.setAlpha(mAlpha);
            canvas.drawBitmap(mCheckBitmap, (mWidth - mIconWidth) / 2, getPaddingTop(), mCheckPaint);
        }

        private void drawUnfocusedText(Canvas canvas) {
            mTextPaint.setColor(mUnCheckTxtColor);
            mTextPaint.setAlpha(255 - mAlpha);
            canvas.drawText(mText, mWidth / 2, mTextBaseY, mTextPaint);
        }

        private void drawFocusText(Canvas canvas) {
            mTextPaint.setColor(mCheckTxtColor);
            mTextPaint.setAlpha(mAlpha);
            canvas.drawText(mText, mWidth / 2, mTextBaseY, mTextPaint);
        }

        /**
         * @param mAlpha
         */
        public void setAlpha(int mAlpha) {
            this.mAlpha = mAlpha;
        }

        public void updateAlpha(float alpha) {
            mAlpha = (int) alpha;
            invalidate();
        }

        public void setChecked(boolean checked) {
            mAlpha = checked ? 255 : 0;
            invalidate();
        }

    }

}
