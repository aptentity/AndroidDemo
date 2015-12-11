package com.borg.mvp.settings;

/** 用于保存开发变量
 * Created by Gulliver(feilong) on 15/12/11.
 */
public class DevSettings {
    /**
     * 调试开关
     */
    private static boolean debug = true;
    public static void turnonDebug(){
        debug = true;
    }
    public static void turnoffDebug(){
        debug = false;
    }

    public static boolean isDebugOn(){
        return debug;
    }
}
