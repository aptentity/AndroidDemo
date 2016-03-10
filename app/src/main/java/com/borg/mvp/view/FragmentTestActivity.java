package com.borg.mvp.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.borg.androidemo.R;
import com.borg.mvp.utils.LogHelper;
import com.borg.mvp.view.fragment.TestFragment;

public class FragmentTestActivity extends FragmentActivity {
    private final String TAG = FragmentTestActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogHelper.d(TAG, "onCreate");
        setContentView(R.layout.activity_fragment_test2);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.tv_fragment,new TestFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogHelper.d(TAG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogHelper.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogHelper.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogHelper.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogHelper.d(TAG, "onDestroy");
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        LogHelper.d(TAG, "onAttachFragment");
    }
}
