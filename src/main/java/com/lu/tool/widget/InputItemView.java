package com.lu.tool.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lu.tool.util.DimensionTools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import lu.basetool.R;

/**
 * Author: luqihua
 * Time: 2017/9/25
 * Description: InputItemView
 */

public class InputItemView extends LinearLayout {
    private final String NAME_SPACE = "http://schemas.android.com/apk/res/android";
    private final String ATTR_INPUTTYPE = "inputType";
    private final String ATTR_LENGTH = "maxLength";
    private final String CLICKABLE = "clickable";
    private final String DIGITS = "digits";

    //当右侧显示这些文字时变更字体颜色
    private static List<String> sPromptText = new ArrayList<>();

    static {
        sPromptText.add("未完成");
        sPromptText.add("已冻结");
    }

    private final static int DEFAULT_LEFT_COLOR = 0xff2e2e2e;
    private final static int DEFAULT_RIGHT_COLOR = 0xff515151;
    private final static int DEFAULT_HINT_COLOR = 0xff8a8a8a;
    private final static int DEFAULT_PROMPT_HINT_COLOR = 0xffee0000;

    private Context mContext;
    private TextView mTitleView;
    private TextView mInputView;

    private String mTitle;
    private String mHint;

    private int leftColor, rightColor;

    private boolean isRequired;
    private boolean editable;
    private boolean clickable;
    private int inputType;
    private String digits;//用来指定输入的字符
    private int maxLength = 128;//输入长度

    public InputItemView(Context context) {
        this(context, null);
    }

    public InputItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        if (attrs != null) {
            inputType = attrs.getAttributeIntValue(NAME_SPACE, ATTR_INPUTTYPE, InputType.TYPE_CLASS_TEXT);
            maxLength = attrs.getAttributeIntValue(NAME_SPACE, ATTR_LENGTH, 128);
            clickable = attrs.getAttributeBooleanValue(NAME_SPACE, CLICKABLE, true);
            digits = attrs.getAttributeValue(NAME_SPACE, DIGITS);

            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.InputView);

            isRequired = array.getBoolean(R.styleable.InputView_required, false);
            editable = array.getBoolean(R.styleable.InputView_editable, false);

            mTitle = array.getString(R.styleable.InputView_input_title);

            if (TextUtils.isEmpty(mTitle)) {
                mTitle = "标题";
            }
            mHint = array.getString(R.styleable.InputView_input_hint);
            mHint = mHint == null ? "" : mHint;
            leftColor = array.getColor(R.styleable.InputView_left_color, DEFAULT_LEFT_COLOR);
            rightColor = array.getColor(R.styleable.InputView_right_color, DEFAULT_RIGHT_COLOR);

            array.recycle();
        }
        initView();
    }

    private void initView() {
        setBackgroundColor(Color.WHITE);
        int padding = DimensionTools.dp2px(10);
        setPadding(padding, padding, padding, padding);
        mTitleView = createTitleView(mTitle);
        addView(mTitleView);

        mInputView = createInputView(mHint);
        mInputView.setInputType(inputType);
        addView(mInputView);
    }


    private TextView createTitleView(String title) {
        TextView textView = new TextView(mContext);
        textView.setTextSize(16);
        textView.setTextColor(leftColor);
        textView.setCompoundDrawablePadding(DimensionTools.dp2px(5));
        textView.setText(title);

        if (isRequired) {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.asterisk, 0);
        }
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        return textView;
    }

    private TextView createInputView(String hint) {
        TextView textView;
        if (editable) {
            textView = new EditText(mContext);
            textView.setBackground(null);
            textView.setPadding(0, 0, 0, 0);
            textView.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(maxLength)
                    , new CustomFilter(digits)});
        } else {
            textView = new TextView(mContext);
            if (clickable) {
                textView.setCompoundDrawablePadding(DimensionTools.dp2px(5));
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_right, 0);
            }
        }

        textView.setTextSize(14);
        textView.setMaxLines(2);
        textView.setTextColor(rightColor);

        textView.setSingleLine(false);
        textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        if (sPromptText.contains(hint)) {
            textView.setHintTextColor(DEFAULT_PROMPT_HINT_COLOR);
        } else {
            textView.setHintTextColor(DEFAULT_HINT_COLOR);
        }
        textView.setHint(hint);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = DimensionTools.dp2px(5);
        params.weight = 1;

        textView.setLayoutParams(params);
        return textView;

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mInputView.setEnabled(false);
    }

    public TextView getInputView() {
        return mInputView;
    }

    public String getText() {
        return mInputView.getText().toString().trim();
    }

    public void setText(CharSequence charSequence) {
        mInputView.setSingleLine(false);
        mInputView.setText(charSequence);
    }

    public void setHint(CharSequence hint) {
        if (sPromptText.contains(hint.toString())) {
            mInputView.setHintTextColor(DEFAULT_PROMPT_HINT_COLOR);
        } else {
            mInputView.setHintTextColor(DEFAULT_HINT_COLOR);
        }
        mInputView.setHint(hint);
    }


    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        this.clickable = clickable;

        if (clickable && mInputView instanceof EditText) {
            this.editable = false;
            removeView(mInputView);
            mInputView = createInputView(getText());
            addView(mInputView);
        }
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        if (!editable && mInputView instanceof EditText) {
            EditText edit = (EditText) mInputView;
            edit.setInputType(0);
        }
    }


    private static class CustomFilter implements InputFilter {

        private Pattern pattern;
        private String digits;

        public CustomFilter(String digits) {
            if (TextUtils.isEmpty(digits)) {
                pattern = null;
            } else if (digits.startsWith("{") && digits.endsWith("}") && digits.length() > 3) {
                pattern = Pattern.compile(digits.substring(1, digits.length() - 1));
            } else {
                pattern = null;
                this.digits = digits;
            }
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            //过滤空格和换行符
            if (source.equals("\n") || source.equals(" ")) return "";
            if (pattern != null &&
                    !pattern.matcher(new StringBuilder(dest).insert(dstart, source)).matches())
                return "";
            int len = source.length();
            //过滤emoji表情
            if (len == 2 && sEmojiPattern.matcher(source).matches()) return "";
            if (digits != null) {
                if (len == 1 && !digits.contains(source)) return "";
                if (len > 1) {
                    for (int i = 0; i < len; i++) {
                        char ch = source.charAt(i);
                        if (digits.indexOf(ch) == -1) return "";
                    }
                }
            }
            return source;
        }

        /**
         * 判断是否是Emoji
         *
         * @param codePoint 比较的单个字符
         * @return
         */
        private static Pattern sEmojiPattern = Pattern.compile("[\\ud83c\\udc00-\\ud83c\\udfff]" +
                        "|[\\ud83d\\udc00-\\ud83d\\udfff]" +
                        "|[\\ud83e\\udd00-\\ud83e\\udfff]" +
                        "|[\\u2600-\\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
    }

}
