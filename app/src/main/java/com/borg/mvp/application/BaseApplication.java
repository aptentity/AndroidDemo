package com.borg.mvp.application;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by Gulliver(feilong) on 15/12/11.
 */
public class BaseApplication extends Application{
    private static Context mContext;
    private static Handler mMainThreadHandler = null;
    private static Looper mMainThreadLooper = null;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mMainThreadLooper = getMainLooper();
        mMainThreadHandler = new Handler();
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }
}
