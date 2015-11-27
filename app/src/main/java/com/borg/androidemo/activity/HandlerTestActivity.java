package com.borg.androidemo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.borg.androidemo.R;
import com.borg.androidemo.common.log.LogHelper;

public class HandlerTestActivity extends AppCompatActivity {

    private Handler mhandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_test);
        LogHelper.d("handler test main thread:"+Thread.currentThread().getId());
        //test handler post method
        mhandler.post(new Runnable() {
            @Override
            public void run() {
                LogHelper.d("handler post runnable:"+Thread.currentThread().getId());
            }
        });
    }
}
