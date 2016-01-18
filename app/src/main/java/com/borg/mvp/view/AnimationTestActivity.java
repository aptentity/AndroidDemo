package com.borg.mvp.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.borg.androidemo.R;

public class AnimationTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_test);
    }

    public void startActivityAnimationTest(View view){
        Intent intent1=new Intent(AnimationTestActivity.this,CustomViewActivity.class);
        startActivity(intent1);
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

    public void startActivityDefaultAnimationTest(View view){
        Intent intent1=new Intent(AnimationTestActivity.this,CustomViewActivity.class);
        startActivity(intent1);
    }
}
