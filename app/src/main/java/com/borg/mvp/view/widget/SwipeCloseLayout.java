package com.borg.mvp.view.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.borg.androidemo.R;

/**
 * 侧滑关闭的布局，使用方式
 * 在目标容器的onCreate里面创建本布局 {@link #SwipeCloseLayout(Context)}
 * 在目标容器的onPostCreate里面将本布局挂载到decorView下{@link #injectWindow()}
 * Created by 陈俊杰 on 2016/2/16.
 */
public class SwipeCloseLayout extends FrameLayout {
    private static final int ANIMATION_DURATION = 200;

    /**
     * 是否可以滑动关闭页面
     */
    private boolean mSwipeEnabled = true;
    private boolean mIsAnimationFinished = true;
    private boolean mCanSwipe = false;
    private boolean mIgnoreSwipe = false;
    private boolean mHasIgnoreFirstMove;

    private Activity mActivity;
    private VelocityTracker tracker;
    private ObjectAnimator mAnimator;
    private Drawable mLeftShadow;
    private View mContent;
    private int mScreenWidth;
    private int touchSlopLength;
    private float mDownX;
    private float mDownY;
    private float mLastX;
    private float mCurrentX;
    private int mPullMaxLength;
    private boolean mIsInjected;


    public SwipeCloseLayout(Context context) {
        this(context, null, 0);
    }

    public SwipeCloseLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeCloseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mActivity = (Activity) context;
        mLeftShadow = context.getResources().getDrawable(R.drawable.left_shadow);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        touchSlopLength = (int) (20 * displayMetrics.density);
        touchSlopLength *= touchSlopLength;
        mScreenWidth = displayMetrics.widthPixels;
        mPullMaxLength = (int) (mScreenWidth * 0.33f);
        setClickable(true);
    }

    /**
     * 将本view注入到decorView的子view上
     * 在{@link Activity#onPostCreate(Bundle)}里使用本方法注入
     */
    public void injectWindow() {
        if (mIsInjected)
            return;

        final ViewGroup root = (ViewGroup) mActivity.getWindow().getDecorView();
        mContent = root.getChildAt(0);
        root.removeView(mContent);
        this.addView(mContent, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        root.addView(this);
        mIsInjected = true;
    }

    public boolean isSwipeEnabled() {
        return mSwipeEnabled;
    }

    public void setSwipeEnabled(boolean swipeEnabled) {
        this.mSwipeEnabled = swipeEnabled;
    }

    @Override
    protected boolean drawChild(@NonNull Canvas canvas, @NonNull View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        final int shadowWidth = mLeftShadow.getIntrinsicWidth();
        int left = (int) (getContentX()) - shadowWidth;
        mLeftShadow.setBounds(left, child.getTop(), left + shadowWidth, child.getBottom());
        mLeftShadow.draw(canvas);
        return result;
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        if (mSwipeEnabled && !mCanSwipe && !mIgnoreSwipe) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = ev.getX();
                    mDownY = ev.getY();
                    mCurrentX = mDownX;
                    mLastX = mDownX;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = ev.getX() - mDownX;
                    float dy = ev.getY() - mDownY;
                    if (dx * dx + dy * dy > touchSlopLength) {
                        if (dy == 0f || Math.abs(dx / dy) > 1) {
                            mDownX = ev.getX();
                            mDownY = ev.getY();
                            mCurrentX = mDownX;
                            mLastX = mDownX;
                            mCanSwipe = true;
                            tracker = VelocityTracker.obtain();
                            return true;
                        } else {
                            mIgnoreSwipe = true;
                        }
                    }
                    break;
            }
        }
        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            mIgnoreSwipe = false;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mCanSwipe || super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (mCanSwipe) {
            tracker.addMovement(event);
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = event.getX();
                    mCurrentX = mDownX;
                    mLastX = mDownX;
                    break;
                case MotionEvent.ACTION_MOVE:
                    mCurrentX = event.getX();
                    float dx = mCurrentX - mLastX;
                    if (dx != 0f && !mHasIgnoreFirstMove) {
                        mHasIgnoreFirstMove = true;
                        dx = dx / dx;
                    }
                    if (getContentX() + dx < 0) {
                        setContentX(0);
                    } else {
                        setContentX(getContentX() + dx);
                    }
                    mLastX = mCurrentX;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    tracker.computeCurrentVelocity(10000);
                    tracker.computeCurrentVelocity(1000, 20000);
                    mCanSwipe = false;
                    mHasIgnoreFirstMove = false;
                    int mv = mScreenWidth * 3;
                    if (Math.abs(tracker.getXVelocity()) > mv) {
                        animateFromVelocity(tracker.getXVelocity());
                    } else {
                        if (getContentX() > mPullMaxLength) {
                            animateFinish(false);
                        } else {
                            animateBack(false);
                        }
                    }
                    tracker.recycle();
                    break;
                default:
                    break;
            }
        }
        return super.onTouchEvent(event);
    }


    public void cancelPotentialAnimation() {
        if (mAnimator != null) {
            mAnimator.removeAllListeners();
            mAnimator.cancel();
        }
    }

    public float getContentX() {
        return mContent.getX();
    }

    private void setContentX(float x) {
        mContent.setX(x);
        invalidate();
    }

    public boolean isAnimationFinished() {
        return mIsAnimationFinished;
    }

    /**
     * 弹回，不关闭，因为left是0，所以setX和setTranslationX效果是一样的
     *
     * @param withVel 使用计算出来的时间
     */
    private void animateBack(boolean withVel) {
        cancelPotentialAnimation();
        mAnimator = ObjectAnimator.ofFloat(this, "contentX", getContentX(), 0);
        int tmpDuration = withVel ? ((int) (ANIMATION_DURATION * getContentX() / mScreenWidth)) : ANIMATION_DURATION;
        if (tmpDuration < 100) {
            tmpDuration = 100;
        }
        mAnimator.setDuration(tmpDuration);
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.start();
    }

    private void animateFinish(boolean withVel) {
        cancelPotentialAnimation();
        mAnimator = ObjectAnimator.ofFloat(this, "contentX", getContentX(), mScreenWidth);
        int tmpDuration = withVel ? ((int) (ANIMATION_DURATION * (mScreenWidth - getContentX()) / mScreenWidth)) : ANIMATION_DURATION;
        if (tmpDuration < 100) {
            tmpDuration = 100;
        }
        mAnimator.setDuration(tmpDuration);
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimationFinished = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimationFinished = true;
                if (!mActivity.isFinishing()) {
                    mActivity.finish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mIsAnimationFinished = true;
            }
        });
        mAnimator.start();
    }


    private void animateFromVelocity(float v) {
        int currentX = (int) getContentX();
        if (v > 0) {
            if (currentX < mPullMaxLength && v * ANIMATION_DURATION / 1000 + currentX < mPullMaxLength) {
                animateBack(false);
            } else {
                animateFinish(true);
            }
        } else {
            if (currentX > mPullMaxLength / 3 && v * ANIMATION_DURATION / 1000 + currentX > mPullMaxLength) {
                animateFinish(false);
            } else {
                animateBack(true);
            }
        }
    }

    public void finish() {
        if (!isAnimationFinished()) {
            cancelPotentialAnimation();
        }
    }
}