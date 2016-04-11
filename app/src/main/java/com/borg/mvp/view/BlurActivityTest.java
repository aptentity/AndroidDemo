package com.borg.mvp.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.borg.mvp.utils.ImageUtil;
import com.borg.mvp.utils.RenderScriptBlurHelper;
import com.borg.mvp.view.widget.BlurDialog;

/**
 * Created by Gulliver(feilong) on 16/4/11.
 */
public class BlurActivityTest extends Activity{
    private Bitmap mBackground;
    //private Activity mHoldingActivity;
    private ImageView mBlurredBackgroundView;
    private FrameLayout.LayoutParams mBlurredBackgroundLayoutParams;
    private View mBackgroundView;
    private Activity mContext;
    public static Activity mHoldingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getParent();
       // mHoldingActivity = getParent();
    }
    BlurAsyncTask mBluringTask;
    @Override
    protected void onResume() {
        super.onResume();
        mBluringTask = new BlurAsyncTask();
        mBluringTask.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissBlur();
    }

    private void dismissBlur(){
        if (mBluringTask != null) {
            mBluringTask.cancel(true);
        }
        if (mBlurredBackgroundView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mBlurredBackgroundView
                        .animate()
                        .alpha(0f)
                        .setDuration(10)
                        .setInterpolator(new AccelerateInterpolator())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                removeBlurredView();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                super.onAnimationCancel(animation);
                                removeBlurredView();
                            }
                        }).start();
            } else {
                removeBlurredView();
            }
        }
    }

    private void removeBlurredView() {
        if (mBlurredBackgroundView != null) {
            ViewGroup parent = (ViewGroup) mBlurredBackgroundView.getParent();
            if (parent != null) {
                parent.removeView(mBlurredBackgroundView);
            }
            mBlurredBackgroundView = null;
        }
    }

    private int offset=0;
    private class BlurAsyncTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBlurredBackgroundLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );

            mBackgroundView = mHoldingActivity.getWindow().getDecorView();
            Rect rect = new Rect();
            mBackgroundView.getWindowVisibleDisplayFrame(rect);
            mBackgroundView.destroyDrawingCache();
            mBackgroundView.setDrawingCacheEnabled(true);
            mBackgroundView.buildDrawingCache(true);
            mBackground = mBackgroundView.getDrawingCache(true);
            Log.i("zfl", "function 1 rect="+rect.toString());

            if (mBackground == null) {
                Log.i("zfl", "function 2");
                mBackgroundView.measure(
                        View.MeasureSpec.makeMeasureSpec(rect.width(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(rect.height(), View.MeasureSpec.EXACTLY)
                );
                mBackgroundView.layout(0, 0, mBackgroundView.getMeasuredWidth(),
                        mBackgroundView.getMeasuredHeight());
                Log.i("zfl","width="+mBackgroundView.getMeasuredWidth()+" height="+mBackgroundView.getMeasuredHeight());
                mBackgroundView.destroyDrawingCache();
                mBackgroundView.setDrawingCacheEnabled(true);
                mBackgroundView.buildDrawingCache(true);
                mBackground = mBackgroundView.getDrawingCache(true);

                ImageUtil.saveBitmap(mBackground);
            }
            if(mBackground!=null){
                offset = rect.top;
            }
        }

        @Override
        protected Object doInBackground(Object[] params) {
            // 进行高斯模糊
            if (mBackground != null) {
                int w = mBackground.getWidth();
                int h = mBackground.getHeight();
                mBackground = Bitmap.createBitmap(mBackground, 0, offset, w, h-offset, null, false);
                mBackground = RenderScriptBlurHelper.doBlur(mBackground, 5, false, mHoldingActivity);
                mBlurredBackgroundView = new ImageView(mHoldingActivity);
                mBlurredBackgroundView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mBlurredBackgroundView.setImageDrawable(new BitmapDrawable(mHoldingActivity.getResources(), mBackground));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (mBackground != null) {
                mHoldingActivity.getWindow().addContentView(
                        mBlurredBackgroundView,
                        mBlurredBackgroundLayoutParams
                );

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    mBlurredBackgroundView.setAlpha(0f);
                    mBlurredBackgroundView
                            .animate()
                            .alpha(1f)
                            .setDuration(10)
                            .setInterpolator(new LinearInterpolator())
                            .start();
                }
                mBackgroundView = null;
                mBackground = null;
            }
        }
    }
}
