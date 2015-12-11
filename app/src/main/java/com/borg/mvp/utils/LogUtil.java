package com.borg.mvp.utils;

import android.util.Log;

import com.borg.mvp.settings.DevSettings;

/**
 * Created by Gulliver(feilong) on 15/12/11.
 */
public class LogUtil {
    private static final String TAG = "[borg]";

    public static void d(String tag,String log){
        if (DevSettings.isDebugOn()){
            Log.d(TAG+tag,log);
        }
    }

    public static void d(String log){
        d("",log);
    }

    public static void e(String tag,String log){
        Log.e(TAG + tag, log);
    }

    public static void e(String log){
        e("",log);
    }
}
