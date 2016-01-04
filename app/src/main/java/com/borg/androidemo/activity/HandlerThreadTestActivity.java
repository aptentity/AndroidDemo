package com.borg.androidemo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.borg.androidemo.R;
import com.borg.mvp.utils.LogHelper;

public class HandlerThreadTestActivity extends AppCompatActivity {
    private HandlerThread mHandlerThread;
    private MyHandler mMyHandler;
    private NormalThread mNormalThread;
    private Handler normalHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_thread_test);

        LogHelper.d("The mainthread id = " + Thread.currentThread().getId());

        mHandlerThread = new HandlerThread("leochin.com");
        mHandlerThread.start();

        mMyHandler = new MyHandler(mHandlerThread.getLooper());
        mMyHandler.sendEmptyMessage(1);

        //记得不用的时候
        //mHandlerThread.quit();//quitSafely

        mNormalThread = new NormalThread();
        mNormalThread.start();
    }

    private class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            LogHelper.d("MyHandler-->handleMessage-->threadid = " + Thread.currentThread().getId());
            super.handleMessage(msg);

            try{
                Thread.sleep(1000);
            }catch (Exception e){}

            normalHandler.sendEmptyMessage(1);
        }
    }

    /**
     *
     * 普通线程
     *
     */
    class NormalThread extends Thread{
        @Override
        public void run() {
            Looper.prepare();
            normalHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    int what = msg.what;
                    if(what == 1){
                        LogHelper.d(Thread.currentThread().getName() + " NormalThread is OK");
                    }
                }
            };
            Looper.loop();
        }

    }
}
