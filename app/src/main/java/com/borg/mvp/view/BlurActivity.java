package com.borg.mvp.view;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.borg.androidemo.R;

import fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine;

/**
 * 模糊化背景
 */
public class BlurActivity extends Activity {
    static final float DEFAULT_BLUR_DOWN_SCALE_FACTOR = 4.0f;
    static final int DEFAULT_BLUR_RADIUS = 8;

    private BlurDialogEngine mBlurEngine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBlurEngine = new BlurDialogEngine(this);

        int radius = getBlurRadius();
        mBlurEngine.setBlurRadius(radius);

        float factor = getDownScaleFactor();
        mBlurEngine.setDownScaleFactor(factor);

        mBlurEngine.setUseRenderScript(false);

        mBlurEngine.debug(true);

        mBlurEngine.setBlurActionBar(false);


        setContentView(R.layout.activity_blur);

    }

    @Override
    protected void onStart() {
        mBlurEngine.onResume(false);
        super.onStart();
    }

    @Override
    protected void onResume() {

        super.onResume();
        //mBlurEngine.onResume(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBlurEngine.onDestroy();
    }

    protected int getBlurRadius() {
        return DEFAULT_BLUR_RADIUS;
    }

    protected float getDownScaleFactor() {
        return DEFAULT_BLUR_DOWN_SCALE_FACTOR;
    }
}
