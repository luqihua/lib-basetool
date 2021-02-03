package com.lu.tool.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import lu.basetool.R;

/**
 * 自定义按下颜色和边框的TextView
 * Created by lqh on 2016/9/25.
 */
public class ButtonTextView extends AppCompatTextView {

    private final String NAME_SPACE = "http://schemas.android.com/apk/res/android";
    private final String ATTR_BGC = "background";
    private final String ATTR_TXTC = "textColor";

    private final int DEFAULT_TEXT_SIZE = 16;
    private final int DEFAULT_TEXT_PADDING = 10;

    private final int DEFAULT_TEXT_COLOR = 0x8a000000;
    //文字演策
    private int txtC = DEFAULT_TEXT_COLOR;
    private int pressTxtC = DEFAULT_TEXT_COLOR;
    //背景色
    private int bgc;
    private int pressBgc;
    //圆角
    private float corner;
    private float cornerPercent;
    //边框
    private int stroke;

    /* 通过代码创建对象时,不检索自定义属性*/
    public ButtonTextView(Context context) {
        super(context);
        /*默认颜色*/
        setTextColor(DEFAULT_TEXT_COLOR);
    }

    public ButtonTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtonTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

        if (attrs != null) {
            String bc = attrs.getAttributeValue(NAME_SPACE, ATTR_BGC);
            if (TextUtils.isEmpty(bc)) {
                bgc = Color.WHITE;
            } else if (bc.startsWith("#")) {
                bgc = Color.parseColor(bc);
            } else if (bc.startsWith("@")) {
                bgc = ContextCompat.getColor(context, Integer.valueOf(bc.substring(1)));
            }

            String tc = attrs.getAttributeValue(NAME_SPACE, ATTR_TXTC);
            if (TextUtils.isEmpty(tc)) {
                txtC = DEFAULT_TEXT_COLOR;
            } else if (tc.startsWith("#")) {
                txtC = Color.parseColor(tc);
            } else if (tc.startsWith("@")) {
                txtC = ContextCompat.getColor(context, Integer.valueOf(tc.substring(1)));
            }

        }

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ButtonTextView);

        pressTxtC = ta.getColor(R.styleable.ButtonTextView_pressTxtColor, txtC);
        pressBgc = ta.getColor(R.styleable.ButtonTextView_pressBgc, bgc);

        //处理圆角度
        final String cornerValue = ta.getString(R.styleable.ButtonTextView_corner);
        if (!TextUtils.isEmpty(cornerValue)) {
            if (cornerValue.contains("%")) {
                corner = -1;
                cornerPercent = ta.getFraction(R.styleable.ButtonTextView_corner, 1, 1, 0f);
            } else {
                corner = ta.getDimensionPixelSize(R.styleable.ButtonTextView_corner, 0);
            }
        }

        //处理边框
        stroke = ta.getDimensionPixelSize(R.styleable.ButtonTextView_stroke, 0);

        ta.recycle();
    }

    private void init() {
        setClickable(true);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (corner < 0) {
            corner = cornerPercent * h;
        }
        setBackground(createBgcDrawable(bgc, pressBgc, corner, stroke));
        setTextColor(createTxtColor(txtC, pressTxtC));
    }

    private ColorStateList createTxtColor(@NonNull int txtC, @NonNull int pressTxtC) {
        int[] colors = new int[]{pressTxtC, txtC};

        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_pressed};
        states[1] = new int[]{};

        return new ColorStateList(states, colors);
    }

    private Drawable createBgcDrawable(@NonNull int bgc, @NonNull int pressBgc, float corner, int stroke) {

        GradientDrawable bgcDrawable = new GradientDrawable();
        GradientDrawable pBgcDrawable = new GradientDrawable();

        bgcDrawable.setCornerRadius(corner);
        bgcDrawable.setStroke(stroke, txtC == 0 ? DEFAULT_TEXT_COLOR : txtC);
        bgcDrawable.setColor(bgc);


        if (bgc == pressBgc) {
            return bgcDrawable;
        }

        pBgcDrawable.setCornerRadius(corner);
        pBgcDrawable.setStroke(stroke, pressBgc);
        pBgcDrawable.setColor(pressBgc);

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pBgcDrawable);
        stateListDrawable.addState(new int[]{}, bgcDrawable);

        return stateListDrawable;

    }

    /**
     * 设置背景色
     *
     * @param bgc
     * @param pressBgc
     */
    public void setBgcDrawable(@NonNull int bgc, @NonNull int pressBgc) {
        this.bgc = bgc;
        this.pressBgc = pressBgc;
    }

    /**
     * 设置文字颜色
     *
     * @param txtC
     * @param pressTxtC
     */
    public void setTextColor(@NonNull int txtC, @NonNull int pressTxtC) {
        this.txtC = txtC;
        this.pressTxtC = pressTxtC;
    }

    public void setCorner(float corner) {
        this.corner = corner;
    }

    public void setStroke(int stroke) {
        this.stroke = stroke;
    }

    public void setTxtC(int txtC, int pressTxtC) {
        this.txtC = txtC;
        this.pressTxtC = pressTxtC;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setAlpha(enabled ? 1.0f : 0.5f);
    }
}
