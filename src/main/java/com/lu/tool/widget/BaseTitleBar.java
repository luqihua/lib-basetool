package com.lu.tool.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lu.tool.util.DimensionTools;
import com.lu.tool.widget.drawable.BackDrawable;

import lu.basetool.R;

/**
 * Author: luqihua
 * Time: 2017/10/11
 * Description: BaseToolbar
 */

public class BaseTitleBar extends FrameLayout {
    private final static int DEFAULT_HEIGHT = 40;//默认高度 40dp
    private final static int DEFAULT_RIGHT_ICON = 0;//默认右侧图标
    private final static int DEFAULT_PADDING = 14;//左右侧内容的内边距
    private final static int DEFAULT_TEXT_SIZE = 14;//默认文字大小sp
    private final static int DEFAULT_TITLE_SIZE = 16;//默认文字大小sp
    private final static int DEFAULT_TEXT_COLOR = 0xff2c2c2c;//默认文字颜色
    private final static String DEFAULT_TITLE = "";//默认标题，例如“个人中心”
    private final static String DEFAULT_RIGHT_TEXT = "";//默认右侧文字，例如“设置”

    private final static int DEFAULT_BOTTOM_LINE_COLOR = 0xffe4e4e4;//默认底部横线颜色
    private final static int DEFAULT_BOTTOM_LINE_HEIGHT = 1;//默认底部横线高度

    private Context mContext;
    private int mHeight = DEFAULT_HEIGHT;
    //中间模块
    protected TextView mTitleView;
    protected String mTitle = DEFAULT_TITLE;
    protected int mTitleColor = DEFAULT_TEXT_COLOR;
    protected int mTitleSize = DEFAULT_TITLE_SIZE;

    //左侧模块
    protected ImageView mLeftView;

    //右侧模块
    protected TextView mRightView;
    protected int mRightIcon = DEFAULT_RIGHT_ICON;
    protected String mRightText = DEFAULT_RIGHT_TEXT;
    protected int mRightTextColor = DEFAULT_TEXT_COLOR;
    protected int mRightTextSize = DEFAULT_TEXT_SIZE;
    private boolean bDrawBottomLine = true;

    private OnTitleBarClickListener mLeftClickListener;
    private OnTitleBarClickListener mRightClickListener;

    public BaseTitleBar(Context context) {
        this(context, null);
    }

    public BaseTitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseTitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BaseTitleBar);
            //中间
            mTitle = ta.getString(R.styleable.BaseTitleBar_centerText);
            mTitle = mTitle == null ? DEFAULT_TITLE : mTitle;
            mTitleSize = ta.getInt(R.styleable.BaseTitleBar_titleSize, DEFAULT_TITLE_SIZE);
            mTitleColor = ta.getColor(R.styleable.BaseTitleBar_titleColor, DEFAULT_TEXT_COLOR);
            //右侧文字
            mRightIcon = ta.getResourceId(R.styleable.BaseTitleBar_rightIcon, DEFAULT_RIGHT_ICON);
            mRightText = ta.getString(R.styleable.BaseTitleBar_rightText);
            mRightText = mRightText == null ? DEFAULT_RIGHT_TEXT : mRightText;
            mRightTextColor = ta.getColor(R.styleable.BaseTitleBar_rightTextColor, mTitleColor);
            mRightTextSize = ta.getInt(R.styleable.BaseTitleBar_rightTextSize, mTitleSize);
            //是否画底部的分割线
            bDrawBottomLine = ta.getBoolean(R.styleable.BaseTitleBar_bottom_line, true);
            ta.recycle();
        }
        init();
        initView();
        createChildView();
    }

    private void init() {
        //初始化默认的高度
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mHeight = (int) (DEFAULT_HEIGHT * metrics.density + 0.5);
        /*设置id*/
        int id = getId();
        if (id < 0) {
            setId(R.id.base_title_bar);
        }
    }

    protected void initView() {
        if (getBackground() == null) {
            setBackgroundColor(ContextCompat.getColor(mContext, R.color.base_title_bar_color));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void createChildView() {
        mTitleView = createTitle();
        addView(mTitleView);

        mLeftView = createLeftModule();
        addView(mLeftView);

        mRightView = createRightModule();
        addView(mRightView);
        if (bDrawBottomLine)
            addView(createBottomLine());
    }

    //左侧模块
    private ImageView createLeftModule() {
        ImageView imageView = new ImageView(mContext);
        Drawable drawable = new BackDrawable(mTitleColor, mHeight * 2 / 3);
        imageView.setImageDrawable(drawable);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLeftClickListener != null) {
                    mLeftClickListener.onClick();
                } else {
                    defaultLeftClick();
                }
            }
        });
        LayoutParams params = new LayoutParams(mHeight, mHeight);
        params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        imageView.setLayoutParams(params);
        return imageView;
    }

    //中间模块
    private TextView createTitle() {
        TextView textView = new TextView(mContext);
        textView.setText(mTitle);
        textView.setTextColor(mTitleColor);
        textView.setTextSize(mTitleSize);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        textView.setLayoutParams(params);
        return textView;
    }

    //右边模块
    private TextView createRightModule() {
        TextView textView = new TextView(mContext);
        textView.setText(mRightText);
        textView.setTextColor(mRightTextColor);
        textView.setTextSize(mRightTextSize);
        textView.setGravity(Gravity.CENTER);
        //设置右侧小图
        if (mRightIcon != 0) {
            Drawable drawable = ContextCompat.getDrawable(mContext, mRightIcon);
            if (drawable != null) {
                drawable.setBounds(0, 0, mHeight / 2, mHeight / 2);
                textView.setCompoundDrawables(drawable, null, null, null);
            }
        }
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRightClickListener != null) {
                    mRightClickListener.onClick();
                } else {
                    defaultRightClick();
                }
            }
        });
        LayoutParams params;
        if (TextUtils.isEmpty(mRightText)) {
            params = new LayoutParams(mHeight, mHeight);
            int padding = DimensionTools.dp2px(5);
            textView.setPadding(padding, 0, padding, 0);
        } else {
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mHeight);
        }
        params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        params.rightMargin = DEFAULT_PADDING;
        textView.setLayoutParams(params);
        return textView;
    }

    //底部横线
    private View createBottomLine() {
        View v = new View(mContext);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DEFAULT_BOTTOM_LINE_HEIGHT);
        params.gravity = Gravity.BOTTOM;
        v.setLayoutParams(params);
        v.setBackgroundColor(DEFAULT_BOTTOM_LINE_COLOR);
        return v;
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        mTitle = title;
        if (mTitleView != null) {
            mTitleView.setText(mTitle);
        }
    }

    /**
     * 默认的左侧按钮点击事件
     */
    protected void defaultLeftClick() {
        if (mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
    }


    /**
     * 默认的右侧点击事件，用于在实现的子类中复写
     */
    protected void defaultRightClick() {
    }

    public View getLeftView() {
        return mLeftView;
    }

    public TextView getRightView() {
        return mRightView;
    }

    //设置左侧按钮的监听回调
    public void setLeftClickListener(OnTitleBarClickListener listener) {
        this.mLeftClickListener = listener;
    }

    //设置右侧按钮监听回调
    public void setRightClickListener(OnTitleBarClickListener listener) {
        this.mRightClickListener = listener;
    }

    /**
     * 按钮的监听
     */
    public interface OnTitleBarClickListener {
        void onClick();
    }

}
