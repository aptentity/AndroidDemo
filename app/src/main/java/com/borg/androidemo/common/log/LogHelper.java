package com.borg.androidemo.common.log;

import android.util.Log;

/**
 * Created by Gulliver(feilong) on 15/11/15.
 */
public class LogHelper {
    private static final String tag = "android_demo";
    public static void d(String text){
        Log.d(tag,text);
    }
    public static void d(String tag,String className,String text){
        Log.d(tag,className+":"+text);
    }
}
