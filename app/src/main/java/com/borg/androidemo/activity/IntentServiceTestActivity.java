package com.borg.androidemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.borg.androidemo.R;
import com.borg.mvp.utils.LogHelper;
import com.borg.androidemo.service.TestIntentService;

public class IntentServiceTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_service_test);

        LogHelper.d("IntentServiceTestActivity start");
        Intent i = new Intent(this, TestIntentService.class);
        i.putExtra("name", "task1");
        startService(i);
        i.putExtra("name", "task2");
        startService(i);
        i.putExtra("name", "task3");
        startService(i);
        i.putExtra("name", "task4");
        startService(i);
        LogHelper.d("IntentServiceTestActivity end");
    }
}
