package com.borg.androidemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.borg.androidemo.R;
import com.loopj.android.image.SmartImageView;

public class SmartImageTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_image_test);

        SmartImageView image = (SmartImageView)findViewById(R.id.iv_portrait);
        image.setImageUrl("http://q.qlogo.cn/qqapp/1104858413/31FF7C24C8567818647BEB2B8F468B3D/40");
    }
}
