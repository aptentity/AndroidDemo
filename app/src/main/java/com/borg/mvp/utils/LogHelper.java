package com.borg.mvp.utils;

import android.util.Log;

import com.borg.mvp.settings.DevSettings;

/**
 * Created by Gulliver(feilong) on 15/11/15.
 */
public class LogHelper {

    private LogHelper(){
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    private static final String TAG = "[borg]";

    public static void e(String msg){
        if (DevSettings.isDebugOn()){
            Log.e(TAG,msg);
        }
    }

    public static void d(String msg){
        if (DevSettings.isDebugOn()){
            Log.d(TAG, msg);
        }
    }

    public static void i(String msg){
        if (DevSettings.isDebugOn()){
            Log.i(TAG, msg);
        }
    }

    public static void v(String msg){
        if (DevSettings.isDebugOn()){
            Log.v(TAG, msg);
        }
    }

    public static void d(String tag,String msg){
        if (DevSettings.isDebugOn()){
            Log.d(TAG + tag, msg);
        }
    }

    public static void e(String tag,String msg){
        if (DevSettings.isDebugOn()){
            Log.e(TAG + tag, msg);
        }
    }

    public static void i(String tag,String msg){
        if (DevSettings.isDebugOn()){
            Log.i(TAG + tag, msg);
        }
    }

    public static void v(String tag,String msg){
        if (DevSettings.isDebugOn()){
            Log.v(TAG + tag, msg);
        }
    }
}
