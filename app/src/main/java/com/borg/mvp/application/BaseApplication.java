package com.borg.mvp.application;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;


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


        //初始化日志
        Logger.init("BORG").setMethodCount(3).hideThreadInfo().setLogLevel(LogLevel.FULL);

        Logger.t("zfl").d("test");
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }
}
