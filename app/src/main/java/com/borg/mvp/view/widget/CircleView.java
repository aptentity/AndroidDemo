package com.borg.mvp.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.StyleableRes;
import android.util.AttributeSet;
import android.view.View;

import com.borg.androidemo.R;
import com.borg.mvp.utils.LogHelper;

/**
 * Created by Gulliver(feilong) on 16/1/18.
 * 直接继承于View的自定义控件
 * 这种方法主要用于实现一些不规则效果，一般需要重写onDraw方法。
 * 采用这种方式需要自己支持wrap_content，并且padding也需要自己处理
 * 如果不对wrap_content做特殊处理，那么使用wrap_content就相当于使用match_parent
 */
public class CircleView extends View{
    private final String TAG = CircleView.class.getSimpleName();
    /**
     * 这些构造函数都是什么时候会被调用
     * 在Code中实例化View会调用第一个构造函数，在XML中定义会调用第二个构造函数
     * @param context
     */
    public CircleView(Context context) {
        super(context);
        LogHelper.d(TAG,"CircleView(Context context)");
        init();
    }

    //在xml中使用时调用此方法
    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        LogHelper.d(TAG, "CircleView(Context context, AttributeSet attrs)");
        //init();
    }

    //不会被系统调用，需要由View显式调用
    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LogHelper.d(TAG, "CircleView(Context context, AttributeSet attrs, int defStyleAttr)");
        //自定义属性第二步：解析
        //首先加载自定义属性集合CircleView
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        //解析CircleView属性集合中的circle_color属性，如果没有属性值就使用红色
        mColor = a.getColor(R.styleable.CircleView_cicle_color,Color.RED);
        //实现资源
        a.recycle();
        init();
    }

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mColor = Color.RED;

    private void init(){
        mPaint.setColor(mColor);
    }

    /**
     * 需要在这里支持wrap_content
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode==MeasureSpec.AT_MOST&&heightSpecMode==MeasureSpec.AT_MOST){
            LogHelper.d(TAG,"all at_most");
            setMeasuredDimension(200, 200);
        }else if(widthSpecMode ==MeasureSpec.AT_MOST){
            LogHelper.d(TAG,"widthSpecMode at_most");
            setMeasuredDimension(200,heightSpecSize);
        }else if (heightSpecMode==MeasureSpec.AT_MOST){
            LogHelper.d(TAG,"heightSpecMode at_most");
            setMeasuredDimension(widthSpecSize,200);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 针对padding进行处理
         */
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingButton = getPaddingBottom();
        int width = getWidth()-paddingLeft-paddingRight;
        int height = getHeight()-paddingTop-paddingButton;
        int radius = Math.min(width,height)/2;
        canvas.drawCircle(paddingLeft+width/2,paddingTop+height/2,radius,mPaint);
    }
}
