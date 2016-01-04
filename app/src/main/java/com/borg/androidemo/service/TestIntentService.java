package com.borg.androidemo.service;

import android.app.IntentService;
import android.content.Intent;

import com.borg.mvp.utils.LogHelper;

/**
 * Created by Gulliver(feilong) on 15/11/17.
 */
public class TestIntentService extends IntentService{
    public TestIntentService(){
        super("TestIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogHelper.d("TestIntentService onCreate"+ ";threadid=" + Thread.currentThread().getId());
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        LogHelper.d("TestIntentService onStart startId=" + startId + ";threadid=" + Thread.currentThread().getId());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogHelper.d("TestIntentService onStartCommand startId="+startId+ ";threadid=" + Thread.currentThread().getId());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getStringExtra("name");
        try {
            Thread.sleep(3000);
        }catch (Exception e){}

        LogHelper.d("TestIntentService onHandleIntent action="+action+ ";threadid=" + Thread.currentThread().getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogHelper.d("TestIntentService onDestroy"+ ";threadid=" + Thread.currentThread().getId());
    }
}
