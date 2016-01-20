package com.borg.mvp.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by Gulliver(feilong) on 16/1/19.
 */
public class HorizontalScrollViewEx extends ViewGroup{
    private static final String TAG = HorizontalScrollViewEx.class.getSimpleName();
    private int mChildrenSize;
    private int mChildWidth;
    private int mChildIndex;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    // 记录上次滑动的坐标
    private int mLastX = 0;
    private int mLastY = 0;
    // 记录上次滑动的坐标
    private int mLastXIntercept = 0;
    private int mLastYIntercept = 0;

    public HorizontalScrollViewEx(Context context){
        super(context);
        init();
    }

    public HorizontalScrollViewEx(Context context,AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public HorizontalScrollViewEx(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
        init();
    }

    private void init(){
        if (mScroller==null){
            mScroller = new Scroller(getContext());
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        int x = (int)ev.getX();
        int y = (int)ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                intercepted = false;
                if (!mScroller.isFinished()){
                    mScroller.abortAnimation();
                    intercepted = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x-mLastXIntercept;
                int deltaY = y-mLastYIntercept;
                if (Math.abs(deltaX)>Math.abs(deltaY)){
                    intercepted = true;
                }else {
                    intercepted = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                intercepted = false;
                break;
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        mLastXIntercept = x;
        mLastYIntercept = y;
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                scrollBy(-deltaX,0);
                break;
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
                if (Math.abs(xVelocity)>=50){
                    mChildIndex = xVelocity>0?mChildIndex-1:mChildIndex+1;
                }else {
                    mChildIndex = (scrollX + mChildIndex/2)/mChildWidth;
                }
                mChildIndex = Math.max(0,Math.min(mChildIndex,mChildrenSize-1));
                int dx = mChildIndex*mChildWidth-scrollX;
                smoothScrollBy(dx,0);
                break;
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }


    private void smoothScrollBy(int dx,int dy){
        mScroller.startScroll(getScrollX(),0,dx,0,500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }

    /**
     * 首先判断是否有子元素，如果没有子元素就直接把自己的宽高设为0；
     * 然后判断宽高是不是采用了wrap_content，如果宽采用了wrap_content，那么HorizontalScrollViewEx的宽度就是所有子元素的宽度之和，
     * 如果高度采用了wrap_content，那么HorizontalScrollViewEx的高度就是第一个子元素的高度
     * 不规范的两点：
     * 1.没有子元素的时候不应该直接将宽高设为0，应该根据LayoutParams中宽高来做相应处理
     * 2.在测量HorizontalScrollViewEx的宽高时没有考虑到它的padding以及子元素的margin，因为它的padding以及子元素的margin会影响到HorizontalScrollViewEx的宽高
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = 0;
        int measuredHeight = 0;
        final int chiledCount = getChildCount();
        measureChildren(widthMeasureSpec,heightMeasureSpec);

        int widthSpaceSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpaceSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        //判断是否有子元素，如果没有子元素就直接将宽高设为0
        //需要考虑padding及子元素的margin
        if (chiledCount == 0){
            setMeasuredDimension(0,0);
        }else if (widthSpecMode==MeasureSpec.AT_MOST&&heightSpecMode==MeasureSpec.AT_MOST){//判断是否为wrap_content
            final View childView = getChildAt(0);
            measuredWidth = childView.getMeasuredWidth()*chiledCount;
            measuredHeight = childView.getMeasuredHeight();
            setMeasuredDimension(measuredWidth,measuredHeight);
        }else if (heightSpecMode == MeasureSpec.AT_MOST){
            final View childView = getChildAt(0);
            measuredHeight = childView.getMeasuredHeight();
            setMeasuredDimension(widthSpaceSize,measuredHeight);
        }else if(widthSpecMode == MeasureSpec.AT_MOST){
            final View childView = getChildAt(0);
            measuredWidth = childView.getMeasuredWidth()*chiledCount;
            setMeasuredDimension(measuredWidth,heightSpaceSize);
        }
    }

    /**
     * 完成子元素的定位。首先遍历所有的子元素，如果这个元素不是处于GONE状态，那么就通过layout方法将其放置在合适的位置上。
     * 这个放置的过程是从左向右的
     * 不完美的地方在于放置子元素的过程没有考虑到自身的padding以及子元素的margin，从一个规范的控件角度来看，这些都是应该考虑的。
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft=0;
        final int childCount = getChildCount();
        mChildrenSize = childCount;
        for(int i=0;i<childCount;i++){
            final View childView = getChildAt(i);
            // 如果是不是gone状态
            if (childView.getVisibility()!=View.GONE){
                final int childWidth = childView.getMeasuredWidth();
                mChildWidth = childWidth;
                childView.layout(childLeft,0,childLeft+childWidth,childView.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }
}
