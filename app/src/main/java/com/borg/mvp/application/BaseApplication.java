package com.borg.mvp.application;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * Created by Gulliver(feilong) on 15/12/11.
 */
public class BaseApplication extends Application{
    private static Context mContext;
    private static Handler mMainThreadHandler = null;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mMainThreadHandler = new Handler();
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }
}
