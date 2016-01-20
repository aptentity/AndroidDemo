package com.borg.mvp.view.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.borg.androidemo.R;
import com.borg.mvp.utils.LogHelper;

/**
 * Created by Gulliver(feilong) on 16/1/20.
 */
public class TestFragment extends Fragment{
    private final String TAG = TestFragment.class.getSimpleName();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.act_list_btn,container,false);
        LogHelper.d(TAG,"onCreateView");
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogHelper.d(TAG, "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogHelper.d(TAG, "onCreate");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogHelper.d(TAG, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        LogHelper.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogHelper.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogHelper.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogHelper.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogHelper.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogHelper.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogHelper.d(TAG, "onDetach");
    }
}
