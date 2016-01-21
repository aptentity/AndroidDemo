package com.borg.mvp.model.Thread;

import com.borg.mvp.utils.LogHelper;

/**
 * Created by Gulliver(feilong) on 16/1/21.
 * 可以暂停继续的线程
 */
public class TestThread extends Thread{
    private final String TAG = TestThread.class.getSimpleName();
    private Object mPauseLock;
    private boolean mPauseFlag;

    public boolean isRunning(){
        return !mPauseFlag;
    }
    public TestThread(){
        mPauseLock = new Object();
        mPauseFlag = false;
    }

    public void onPause(){
        synchronized (mPauseLock){
            mPauseFlag = true;
        }
    }

    public void onResume(){
        synchronized (mPauseLock){
            mPauseFlag = false;
            mPauseLock.notifyAll();
        }
    }

    private void pauseThread(){
        synchronized (mPauseLock){
            if (mPauseFlag){
                try{
                    mPauseLock.wait();
                }catch (Exception e){

                }
            }
        }
    }

    @Override
    public void run() {
        for (int i=0;i<100;){
            pauseThread();
            LogHelper.d(TAG,"running");
        }
    }
}
