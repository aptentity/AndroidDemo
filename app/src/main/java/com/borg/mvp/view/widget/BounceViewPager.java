package com.borg.mvp.view.widget;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.borg.androidemo.R;

public class BounceViewPager extends ViewPager {

    final static int DEFAULT_OVERSCROLL_TRANSLATION = 150;
    final static int DEFAULT_SWIPE_TRANSLATION = 100;
    final static float DEFAULT_SWIPE_ROTATION = 3;
    final static float DEFAULT_OVERSCROLL_ROTATION = 2f;

    final private static int DEFAULT_OVERSCROLL_ANIMATION_DURATION = 400;
    final private static boolean DEFAULT_ANIMATE_ALPHA = false;
    final private static int INVALID_POINTER_ID = -1;
//  final private static double RADIANS = 180f / Math.PI;

    private OnPageChangeListener mScrollListener;
    private float mLastMotionX;
    private int mActivePointerId;
    private int mScrollPosition;
    private float mScrollPositionOffset;
//    private int mScrollPositionOffsetPixels;
    final private int mTouchSlop;

    private float mOverscrollRotation;
    private float mSwipeRotation;
    private int mOverscrollTranslation;
    private int mSwipeTranslation;
    private int mOverscrollAnimationDuration;
    private boolean mAnimateAlpha;

    private Rect mTempTect = new Rect();
    private Rect gRect = new Rect();
    final private OverscrollEffect mOverscrollEffect = new OverscrollEffect();
    final private Camera mCamera = new Camera();


    private class OverscrollEffect {
        private float mOverscroll;
        private Animator mAnimator;

        /**
         * @param deltaDistance [0..1] 0->no overscroll, 1>full overscroll
         */
        public void setPull(final float deltaDistance) {
            mOverscroll = deltaDistance;
            invalidateVisibleChilds();
        }

        /**
         * called when finger is released. starts to animate back to default
         * position
         */
        private void onRelease() {
            if (mAnimator != null && mAnimator.isRunning()) {
                mAnimator.addListener(new AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startAnimation(0);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });
                mAnimator.cancel();
            } else {
                startAnimation(0);
            }
        }

        private void startAnimation(final float target) {
            mAnimator = ObjectAnimator.ofFloat(this, "pull", mOverscroll, target);
            mAnimator.setInterpolator(new DecelerateInterpolator());
            final float scale = Math.abs(target - mOverscroll);
            mAnimator.setDuration((long) (mOverscrollAnimationDuration * scale));
            mAnimator.start();
        }

        private boolean isOverscrolling() {
            if (mScrollPosition == 0 && mOverscroll < 0) {
                return true;
            }
            if (getAdapter() != null) {
                final boolean isLast = (getAdapter().getCount() - 1) == mScrollPosition;
                if (isLast && mOverscroll > 0) {
                    return true;
                }
            }
            return false;
        }

    }

    public BounceViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setStaticTransformationsEnabled(true);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        super.setOnPageChangeListener(new MyOnPageChangeListener());
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BounceViewPager);
        mOverscrollRotation = a.getFloat(R.styleable.BounceViewPager_overscroll_rotation1, DEFAULT_OVERSCROLL_ROTATION);
        mSwipeRotation = a.getFloat(R.styleable.BounceViewPager_swipe_rotation1, DEFAULT_SWIPE_ROTATION);
        mSwipeTranslation = a.getInt(R.styleable.BounceViewPager_swipe_translation1, DEFAULT_SWIPE_TRANSLATION);
        mOverscrollTranslation = a.getInt(R.styleable.BounceViewPager_overscroll_translation1, DEFAULT_OVERSCROLL_TRANSLATION);
        mOverscrollAnimationDuration = a.getInt(R.styleable.BounceViewPager_overscroll_animation_duration1, DEFAULT_OVERSCROLL_ANIMATION_DURATION);
        mAnimateAlpha = a.getBoolean(R.styleable.BounceViewPager_animate_alpha1, DEFAULT_ANIMATE_ALPHA);
        a.recycle();
    }

    public boolean isAnimateAlpha() {
        return mAnimateAlpha;
    }

    public void setAnimateAlpha(boolean mAnimateAlpha) {
        this.mAnimateAlpha = mAnimateAlpha;
    }

    public int getOverscrollAnimationDuration() {
        return mOverscrollAnimationDuration;
    }

    public void setOverscrollAnimationDuration(int mOverscrollAnimationDuration) {
        this.mOverscrollAnimationDuration = mOverscrollAnimationDuration;
    }

    public int getSwipeTranslation() {
        return mSwipeTranslation;
    }

    public void setSwipeTranslation(int mSwipeTranslation) {
        this.mSwipeTranslation = mSwipeTranslation;
    }

    public int getOverscrollTranslation() {
        return mOverscrollTranslation;
    }

    public void setOverscrollTranslation(int mOverscrollTranslation) {
        this.mOverscrollTranslation = mOverscrollTranslation;
    }

    public float getSwipeRotation() {
        return mSwipeRotation;
    }

    public void setSwipeRotation(float mSwipeRotation) {
        this.mSwipeRotation = mSwipeRotation;
    }

    public float getOverscrollRotation() {
        return mOverscrollRotation;
    }

    public void setOverscrollRotation(float mOverscrollRotation) {
        this.mOverscrollRotation = mOverscrollRotation;
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mScrollListener = listener;
    }

    private void invalidateVisibleChilds() {
        for (int i = 0; i < getChildCount(); i++) {
            final View childAt = getChildAt(i);
            childAt.getLocalVisibleRect(mTempTect);
            childAt.getGlobalVisibleRect(gRect);
            final int area = mTempTect.width() * mTempTect.height();
            if (area > 0) {
                childAt.invalidate();
            }
        }

        invalidate();
    }

    private class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mScrollListener != null) {
                mScrollListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
            mScrollPosition = position;
            mScrollPositionOffset = positionOffset;
//          mScrollPositionOffsetPixels = positionOffsetPixels;
            //Log.i(DEBUG_TAG, "mScrollPosition = " + position + " offset = " + String.format("%f.2", positionOffset));
            //Log.i(DEBUG_TAG, "onPageScrolled");

            invalidateVisibleChilds();
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollListener != null) {
                mScrollListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
            if (mScrollListener != null) {
                mScrollListener.onPageScrollStateChanged(state);
            }
            if (state == SCROLL_STATE_IDLE) {
                mScrollPositionOffset = 0;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mLastMotionX = ev.getX();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, index);
                mLastMotionX = x;
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            }
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean callSuper = false;

        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                callSuper = true;
                mLastMotionX = ev.getX();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                callSuper = true;
                final int index = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, index);
                mLastMotionX = x;
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId != INVALID_POINTER_ID) {
                    // Scroll to follow the motion event
                    final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                    final float x = MotionEventCompat.getX(ev, activePointerIndex);
                    final float deltaX = mLastMotionX - x;
                    final int width = getWidth();
                    final int widthWithMargin = width + getPageMargin();
                    final int lastItemIndex = getAdapter().getCount() - 1;
                    final int currentItemIndex = getCurrentItem();
                    final float leftBound = Math.max(0, (currentItemIndex - 1) * widthWithMargin);
                    final float rightBound = Math.min(currentItemIndex + 1, lastItemIndex) * widthWithMargin;
                    if (mScrollPositionOffset == 0) {
                        if (currentItemIndex == 0) {
                            if (leftBound == 0) {
                                final float over = deltaX + mTouchSlop;
                                mOverscrollEffect.setPull(over / width);
                            }
                        } else if (lastItemIndex == currentItemIndex) {
                            if (rightBound == lastItemIndex * widthWithMargin) {
                                final float over = deltaX - mTouchSlop;
                                mOverscrollEffect.setPull(over / width);
                            }
                        }
                    } else {
                        mLastMotionX = x;
                    }
                } else {
                    mOverscrollEffect.onRelease();
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                callSuper = true;
                mActivePointerId = INVALID_POINTER_ID;
                mOverscrollEffect.onRelease();
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastMotionX = ev.getX(newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                    callSuper = true;
                }
                break;
            }
            default:
                break;
        }

        if (mOverscrollEffect.isOverscrolling() && !callSuper) {
            return true;
        } else {
            try {
                return super.onTouchEvent(ev);
            } catch (IllegalArgumentException ignore) {
                Log.e("BounceViewPage", ignore.toString());
            } catch (ArrayIndexOutOfBoundsException ignore) {
                Log.e("BounceViewPage", ignore.toString());
            }
            return false;
        }
    }


    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        if (child.getWidth() == 0) {
            return false;
        }

        final boolean isFirstOrLast = mScrollPosition == 0 || (mScrollPosition == (getAdapter().getCount() - 1));
        if (mOverscrollEffect.isOverscrolling() && isFirstOrLast) {
            final float dx = getWidth() / 2;
            final int dy = getHeight() / 2;
            t.getMatrix().reset();
            final float translateX = (float) mOverscrollTranslation * (mOverscrollEffect.mOverscroll > 0 ? Math.min(mOverscrollEffect.mOverscroll, 1) : Math.max(mOverscrollEffect.mOverscroll, -1));

            mCamera.save();
            mCamera.translate(-translateX, 0, 0);
            mCamera.getMatrix(t.getMatrix());
            mCamera.restore();

            t.getMatrix().preTranslate(-dx, -dy);
            t.getMatrix().postTranslate(dx, dy);

            return true;
        }

        return false;
    }
}
