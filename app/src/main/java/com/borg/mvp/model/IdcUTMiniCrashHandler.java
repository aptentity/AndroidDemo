package com.borg.mvp.model;
import com.borg.androidemo.common.utils.CKLOG;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Gulliver(feilong) on 16/1/7.
 * 处理crash信息，通过crash内容判断是否为sdk的crash，如果为sdk的crash则上传，否则不上传
 * 向crash中添加包名信息
 */
public class IdcUTMiniCrashHandler implements Thread.UncaughtExceptionHandler{
    private final String TAG = IdcUTMiniCrashHandler.class.getSimpleName();
    private Thread.UncaughtExceptionHandler mDefaultHandler = null;
    @Override
    public void uncaughtException(Thread pThread, Throwable pException) {
        try{
            CKLOG.Debug(TAG, "crash:" + pException.getMessage());
            if (isSDKCrash(pException)){
                //Throwable throwable = new Throwable("app package name:"+YunOSCloudKit.mContext.getPackageName()+"\n"+pException.getMessage(),pException);
                CKLOG.Debug(TAG,"sdk crash");
            }else {
                CKLOG.Debug(TAG,"not sdk crash");
            }
        }catch (Exception e){
            CKLOG.Debug(TAG,"uncaughtException");
        }
    }

    public void turnOn(){
        Thread.setDefaultUncaughtExceptionHandler(this);
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    /**
     * 根据crash信息判断是否为sdk的crash
     * @param pException
     * @return
     */
    private boolean isSDKCrash(Throwable pException) {

        if (pException != null) {
            Throwable lCause = pException.getCause();

            if (lCause == null) {
                lCause = pException;
            }
            if (lCause != null) {
                StackTraceElement[] stes = lCause.getStackTrace();
                if (stes.length > 0) {
                    StackTraceElement ste = stes[0];
                    if (ste != null) {
                        int kPos = 0;
                        String sException = lCause.toString();
                        String message = "";
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        try {
                            pException.printStackTrace(pw);

                            message = sw.toString();
                        } catch (Exception e) {
                        } finally {
                            try {
                                pw.close();
                                sw.close();
                            } catch (Exception e) {

                            }
                        }
                        CKLOG.Debug(TAG,"crash message:"+message);
                        return message.contains("com.yunos.cloudkit");
                    }
                }
            }
        }
        return false;
    }
}
