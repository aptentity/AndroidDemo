package com.borg.mvp.view.widget;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.borg.mvp.utils.LogHelper;

/**
 * Created by Gulliver(feilong) on 16/3/8.
 */
public class VDHLayout extends LinearLayout{
    private final String TAG = VDHLayout.class.getSimpleName();
    private ViewDragHelper mDragger;

    private View mDragView;
    private View mAutoBackView;
    private View mEdgeTrackerView;
    private Point mAutoBackOriginPos = new Point();

    public VDHLayout(Context context,AttributeSet attrs){
        super(context,attrs);
        mDragger = ViewDragHelper.create(this,1.0f,new ViewDragHelper.Callback(){
            //tryCaptureView如何返回ture则表示可以捕获该view，你可以根据传入的第一个view参数决定哪些可以捕获
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                LogHelper.d(TAG,"tryCaptureView");
                return child==mDragView||child== mAutoBackView;
            }

            //clampViewPositionHorizontal,clampViewPositionVertical可以在该方法中对child移动的边界进行控制，left , top 分别为即将移动到的位置
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                LogHelper.d(TAG,"clampViewPositionHorizontal left="+left+" dx="+dx);
                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                LogHelper.d(TAG,"clampViewPositionVertical top="+top+" dy="+dy);
                return top;
            }
            //手指释放的时候回调
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                LogHelper.d(TAG,"onViewReleased");
                //mAutoBackView手指释放时可以自动回去
                if (releasedChild==mAutoBackView){
                    LogHelper.d(TAG,"onViewReleased mAutoBackView");
                    mDragger.settleCapturedViewAt(mAutoBackOriginPos.x, mAutoBackOriginPos.y);
                    invalidate();
                }
            }
            //在边界拖动时回调
            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                mDragger.captureChildView(mEdgeTrackerView, pointerId);
            }
        });

        mDragger.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragger.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragger.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if(mDragger.continueSettling(true))
        {
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mAutoBackOriginPos.x = mAutoBackView.getLeft();
        mAutoBackOriginPos.y = mAutoBackView.getTop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDragView = getChildAt(0);
        mAutoBackView = getChildAt(1);
        mEdgeTrackerView = getChildAt(2);
    }
}
