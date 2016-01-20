package com.borg.mvp.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.borg.androidemo.R;
import com.borg.mvp.utils.LogHelper;
import com.borg.mvp.utils.ToastUtil;

public class CustomViewActivity extends AppCompatActivity {
    private final String TAG = CustomViewActivity.class.getSimpleName();
    private View mCircleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_view);
        mCircleView = findViewById(R.id.cv_test);
    }

    /**
     * 用于演示
     * @param view
     */
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_scrollby:
                scrollBy();
                break;
            case R.id.btn_scrollto:
                LogHelper.d(TAG,"scroll to");
                ToastUtil.showShort("scroll to");
                scrollTo();
                break;
            case R.id.cv_test:
                LogHelper.d(TAG,"click circle view");
                break;
            //线程测试
            case R.id.btn_thread_start://在子线程中
                new ThreadTest().start();
                LogHelper.d(TAG, "thread start");
                System.exit(0);
                break;
            case R.id.btn_thread_run://在调用线程中执行
                new ThreadTest().run();
                LogHelper.d(TAG,"thread run");
                break;
        }
    }

    private class ThreadTest extends Thread{
        @Override
        public void run() {
            for (int i=0;i<100;i++){
                LogHelper.d(TAG,""+i);
            }
        }
    }

    /**
     * 演示scrollBy、scrollTo
     * 操作简单，适合对View内容的滑动
     * 计算方法：View的边缘减去View内容边缘，由左向右是负数，由上到下是负数
     */
    private void scrollBy(){
        mCircleView.scrollBy(100,0);
    }

    private void scrollTo(){
        mCircleView.scrollTo(100,0);
    }
}
