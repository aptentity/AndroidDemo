package com.borg.mvp.utils;

import android.widget.Toast;

import com.borg.mvp.application.BaseApplication;
import com.borg.mvp.settings.DevSettings;

/**
 * Created by Gulliver(feilong) on 15/12/11.
 */
public class ToastUtil {
    private static Toast sToast;
    /**
     * long Toast
     *
     * @param text
     */
    public static void showLong(final String text) {
        showBase(text, Toast.LENGTH_LONG);
    }

    /**
     * short Toast
     *
     * @param text
     */
    public static void showShort(final String text) {
        showBase(text, Toast.LENGTH_SHORT);
    }

    public static void showDebug(final String text){
        if (DevSettings.isDebugOn()){
            showShort(text);
        }
    }

    private static void showBase(final String text, final int length) {
        BaseApplication.getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                if (sToast == null) {
                    sToast = Toast.makeText(BaseApplication.getAppContext(), text, length);
                }
                sToast.setText(text);
                sToast.show();
            }
        });
    }
}
