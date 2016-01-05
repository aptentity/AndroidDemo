package com.borg.mvp.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Gulliver(feilong) on 16/1/5.
 */
public class CommonActivitiesUtils {
    /**
     * 打开网络设置
     * @param context
     */
    public static void showNetWorkSettingActivity(Context context){
        Intent intent=null;
        if(android.os.Build.VERSION.SDK_INT>10){
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        }else{
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        context.startActivity(intent);
    }
}
