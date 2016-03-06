package com.borg.mvp.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.borg.androidemo.R;
import com.borg.mvp.application.BaseApplication;
import com.borg.mvp.view.widget.SwipeCloseLayout;



public class BaseActivity extends AppCompatActivity {
    private BaseApplication mBaseApp = null;
    private WindowManager mWindowManager = null;
    private View mNightView = null;
    private View mTitleBar;
    private boolean mIsAddedView;
    private BroadcastReceiver mNetWorkChangeReceiver;
    private float currentTitleAlpha;
    private SwipeCloseLayout mSwipeCloseLayout;

    protected boolean isSwipeToClose() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBaseApp = (BaseApplication) getApplication();
        setTheme(isSwipeToClose() ? R.style.AppTheme_day_transparent : R.style.AppTheme_day);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http_test);
        findViewById(R.id.tv_hello).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseActivity.this,BaseActivity.class);
                startActivity(intent);
            }
        });
        if (isSwipeToClose()) {
            mSwipeCloseLayout = new SwipeCloseLayout(this);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (isSwipeToClose()) {
            mSwipeCloseLayout.injectWindow();
        }
    }

    @Override
    public void finish() {
        if (isSwipeToClose()) {
            mSwipeCloseLayout.finish();
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if (mIsAddedView) {
            mBaseApp = null;
            mWindowManager.removeViewImmediate(mNightView);
            mWindowManager = null;
            mNightView = null;
        }
        super.onDestroy();
    }

    public void regScrollTitleBar(View view) {
        mTitleBar = view;
        currentTitleAlpha = 1;
    }

    public void switchActionBar(int deltaY) {
        //向上滑  +
        //向下    -

        if (mTitleBar == null)
            return;

        //已经透明，不用继续再减透明度了
        if (currentTitleAlpha <= 0 && deltaY > 0)
            return;

        //已经完全不透明，不用继续加透明度了
        if (currentTitleAlpha >= 1 && deltaY < 0)
            return;

        currentTitleAlpha = currentTitleAlpha - deltaY * 0.002f;

        if (currentTitleAlpha < 0)
            currentTitleAlpha = 0;

        if (currentTitleAlpha > 1)
            currentTitleAlpha = 1;

        mTitleBar.setClickable(currentTitleAlpha >= 0.5f);
        mTitleBar.setAlpha(currentTitleAlpha);
    }

    public BaseApplication getApp() {
        return mBaseApp;
    }

    protected void ChangeToDay() {
        mNightView.setBackgroundResource(android.R.color.transparent);
    }

    protected void ChangeToNight() {
        initNightView();
    }

    /**
     * wait a time until the onresume finish
     */
    protected void recreateOnResume() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                recreate();
            }
        }, 100);
    }

    private void initNightView() {
        if (mIsAddedView)
            return;
        LayoutParams mNightViewParam = new LayoutParams(
                LayoutParams.TYPE_APPLICATION,
                LayoutParams.FLAG_NOT_TOUCHABLE | LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mNightView = new View(this);
        mWindowManager.addView(mNightView, mNightViewParam);
        mIsAddedView = true;
    }

}
