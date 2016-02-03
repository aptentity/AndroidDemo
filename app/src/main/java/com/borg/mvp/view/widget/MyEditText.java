package com.borg.mvp.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import com.borg.androidemo.R;
import com.borg.mvp.utils.LogHelper;

/**
 * Created by Gulliver(feilong) on 16/2/3.
 */
public class MyEditText extends EditText{
    private final String TAG = MyEditText.class.getSimpleName();
    public MyEditText(Context context) {
        this(context, null);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        // 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
    }
    int hintTextSize = 15;
    public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyEditText);
        hintTextSize = a.getDimensionPixelSize(R.styleable.MyEditText_hint_textSize, hintTextSize);
        LogHelper.d(TAG,"hint text size="+hintTextSize);
        hintTextSize = (int)a.getDimension(R.styleable.MyEditText_hint_textSize, hintTextSize);
        LogHelper.d(TAG,"hint text size="+hintTextSize);
        init();
    }

    private void init(){
        String hint = getHint().toString();
        // 新建一个可以添加属性的文本对象
        SpannableString ss = new SpannableString(hint);
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(hintTextSize);
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint
        setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失
    }
}
