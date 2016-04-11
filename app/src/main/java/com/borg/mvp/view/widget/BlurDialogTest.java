package com.borg.mvp.view.widget;

import android.app.Activity;

import com.borg.androidemo.R;

/**
 * Created by Gulliver(feilong) on 16/4/8.
 */
public class BlurDialogTest extends BlurDialog {
    public BlurDialogTest(Activity activity){
        super(activity);
    }
    @Override
    protected void onCreateDialog() {
        setDialogView(R.layout.test_layout);
    }
}
