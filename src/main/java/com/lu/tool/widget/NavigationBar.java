package com.lu.tool.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LevelListDrawable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import lu.basetool.R;


/**
 * 首页底部导航栏
 * Created by lqh on 2016/9/27.
 */
public class NavigationBar extends LinearLayout implements OnClickListener {

    final int LABEL_TEXT_SIZE = 10;

    private boolean animatorAble = true;
    private Context mContext;
    private int mCurrentIndex;
    private int mTargetIndex = -1;
    private int mWidth;

    private String[] mTitleList;

    private int[] mNormalIcon;

    private int[] mSelectedIcon;


    private SparseArray<TabView> mItems = new SparseArray<>();

    private OnItemSelectedListener mListener;

    public void setCheckedListener(OnItemSelectedListener listener) {
        this.mListener = listener;
    }

    public NavigationBar(Context context) {
        this(context, null);
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(10, 5, 10, 5);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
    }

    /**
     * 添加tab
     *
     * @param normalIcon
     * @param selectIcon
     * @param titles
     */
    public void setTabs(int[] normalIcon, int[] selectIcon, String[] titles) {
        if (normalIcon == null || selectIcon == null || titles == null) {
            throw new RuntimeException("tab must set normalIcon selectIcon  title");
        }

        int count = normalIcon.length;

        if (selectIcon.length != count || titles.length != count) {
            throw new RuntimeException("normalIcon selectIcon title.size() must be the same");
        }

        this.mNormalIcon = normalIcon;
        this.mSelectedIcon = selectIcon;
        this.mTitleList = titles;


        removeAllViews();
        mItems.clear();

        for (int i = 0; i < count; i++) {
            TabView item = createTab(mNormalIcon[i], mSelectedIcon[i], mTitleList[i]);
            if (i == 0) item.checked();
            mItems.put(i, item);
            addView(item);
        }

        if (mWidth > 0) {
            invalidate();
        }

    }

    /**
     * @param icon
     * @param selectIcon
     * @param text
     * @return
     */
    private TabView createTab(int icon, int selectIcon, String text) {
        TabView item = new TabView(icon, selectIcon, text);
        //点击事件
        item.setOnClickListener(this);
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        params.rightMargin = 10;
        item.setLayoutParams(params);
        return item;
    }


    //tab的点击事件
    @Override
    public void onClick(View v) {
        TabView view = (TabView) v;
        int index = mItems.indexOfValue(view);
        selectedTab(index);
    }


    public void setItemNum(int position, String num) {
        TabView tabView = mItems.get(position);
        if (tabView != null) {
            tabView.setNum(num);
        }
    }

    /**
     * 点击的时候是否支持动画
     *
     * @param animatorAble
     */
    public void setAnimatorAble(boolean animatorAble) {
        this.animatorAble = animatorAble;
    }


    /**
     * 设置选中的tab
     *
     * @param position
     */
    public void selectedTab(int position) {
        if (position == mCurrentIndex || position < 0 || position >= mItems.size())
            return;
        if (mListener == null) return;

        if (mListener.onSelectedBefore(position)) {
            mItems.get(mCurrentIndex).unChecked();
            mCurrentIndex = position;
            mItems.get(mCurrentIndex).checked();
            if (mListener != null) {
                mListener.onItemSelected(mCurrentIndex);
            }
        } else {
            mTargetIndex = position;
        }
    }

    public void selectedCacheIndex() {
        if (mTargetIndex == -1) return;
        mItems.get(mCurrentIndex).unChecked();
        mCurrentIndex = mTargetIndex;
        mItems.get(mCurrentIndex).checked();
        mTargetIndex = -1;
        if (mListener != null) {
            mListener.onItemSelected(mCurrentIndex);
        }
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    /*-----------------------------*/

    public interface OnItemSelectedListener {
        boolean onSelectedBefore(int targetIndex);

        void onItemSelected(int index);
    }

    public class TabView extends FrameLayout {
        private final int P_LEFT = 8, P_RIGHT = 8, P_TOP = 4, P_BOTTOM = 4;
        private TextView mContent;
        private TextView mNumView;
        private Paint mNumPaint;
        private boolean isChecked;
        private int mCount;
        private int DEFAULT_NUM_COLOR;

        public TabView(Context context, AttributeSet attrs) {
            super(context, attrs);
            setEnabled(true);
            setClickable(true);
            DEFAULT_NUM_COLOR = ContextCompat.getColor(context, R.color.main_color);
        }

        public TabView(int resId, int selectedRes, String title) {
            this(mContext, null);
            initContentView();
            setDrawable(resId, selectedRes);
            setTitle(title);
            unChecked();
        }

        private void initContentView() {
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = 10;
            mContent = new TextView(mContext);
            mContent.setTextSize(LABEL_TEXT_SIZE);
            mContent.setGravity(Gravity.CENTER);
            addView(mContent, params);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }

        /**
         * 数量小圆圈
         */
        private void createNumView(int color) {
            LayoutParams numParams = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);

            numParams.gravity = Gravity.RIGHT | Gravity.TOP;
            numParams.setMargins(0, 10, 20, 0);

            mNumView = new TextView(mContext);
            mNumView.setGravity(Gravity.CENTER);
            mNumView.setPadding(P_LEFT, P_TOP, P_RIGHT, P_BOTTOM);
            mNumView.setBackgroundDrawable(getGradientDrawable(color));
            mNumView.setTextSize(12);
            mNumView.setTextColor(Color.WHITE);
            mNumPaint = mNumView.getPaint();
            addView(mNumView, numParams);
        }

        private void setDrawable(int normalRes, int selectRes) {

            LevelListDrawable drawable = new LevelListDrawable();
            drawable.addLevel(0, 9, ContextCompat.getDrawable(mContext, normalRes));
            drawable.addLevel(10, 19, ContextCompat.getDrawable(mContext, selectRes));
            mContent.setCompoundDrawablesWithIntrinsicBounds(
                    null, drawable, null, null);
        }

        private Drawable getGradientDrawable(int color) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(color);
            gradientDrawable.setShape(GradientDrawable.OVAL);
            return gradientDrawable;
        }

        public void setTitle(String title) {
            mContent.setText(title);
        }

        public void setNum(int count, int color) {
            if (mNumView == null) createNumView(color);
            mCount = count;
            if (count < 10) {
                int offset = (int) (mNumPaint.measureText("0") / 2);
                mNumView.setPadding(P_LEFT + offset, P_TOP, P_RIGHT + offset, P_BOTTOM);
            } else {
                mNumView.setPadding(P_LEFT, P_TOP, P_RIGHT, P_BOTTOM);
            }
            if (count == 0 || isChecked)
                mNumView.setVisibility(GONE);
            else
                mNumView.setVisibility(VISIBLE);
            mNumView.setText(String.valueOf(count));
        }

        public void setNum(int count) {
            setNum(count, DEFAULT_NUM_COLOR);
        }

        private void showNum(boolean isShow) {
            if (mNumView == null) return;
            if (mCount <= 0)
                mNumView.setVisibility(GONE);
            else
                mNumView.setVisibility(isShow ? VISIBLE : GONE);
        }

        public void setNum(String count) {
            setNum(Integer.valueOf(count));
        }

        public void checked() {
            if (animatorAble)
                ViewCompat.animate(this)
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .start();
            isChecked = true;
            showNum(false);
            mContent.getCompoundDrawables()[1].setLevel(15);
            mContent.setTextColor(DEFAULT_NUM_COLOR);
        }

        public void unChecked() {
            if (animatorAble)
                ViewCompat.animate(this)
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .start();
            isChecked = false;
            showNum(true);
            mContent.getCompoundDrawables()[1].setLevel(5);
            mContent.setTextColor(Color.GRAY);
        }
    }

}
