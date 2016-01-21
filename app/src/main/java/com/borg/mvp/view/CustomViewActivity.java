package com.borg.mvp.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.borg.androidemo.R;
import com.borg.mvp.model.Network.INetworkCallback;
import com.borg.mvp.model.Network.QrNetworkHelper;
import com.borg.mvp.model.Thread.TestThread;
import com.borg.mvp.model.entities.QrLoginResult;
import com.borg.mvp.model.entities.QrResult;
import com.borg.mvp.utils.LogHelper;
import com.borg.mvp.utils.QRCodeUtils;
import com.borg.mvp.utils.ToastUtil;

import java.io.File;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class CustomViewActivity extends AppCompatActivity {
    private final String TAG = CustomViewActivity.class.getSimpleName();
    private View mCircleView;
    private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_view);
        mCircleView = findViewById(R.id.cv_test);
        mImageView = (ImageView)findViewById(R.id.imageView2);
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
                Thread thread = new ThreadTest();
                thread.start();
                LogHelper.d(TAG, "thread start");
                //并不是马上停止
                //thread.stop();
                mThread.start();
                break;
            case R.id.btn_thread_run://在调用线程中执行
                LogHelper.d(TAG, "thread run");
                //暂停或继续线程
                if (mThread.isRunning()){
                    mThread.onPause();
                }else {
                    mThread.onResume();
                }

                Thread thread1 = new ThreadTest();
                thread1.run();

                break;
            case R.id.btn_get_qr://网络获取url，生成二维码
                getQrCode();
                break;
        }
    }

    /**
     * 网络获取url，生成二维码
     */
    private void getQrCode(){
        QrNetworkHelper.getQrUrl(new INetworkCallback() {
            @Override
            public void onSuccess(String result) {
                LogHelper.d(TAG,"getQrUrl onSuccess:"+result);
                mQrResult = new QrResult(result);
                if (mQrResult.isSuccess()){
                    ToastUtil.showShort("getQrUrl success");
                    //生成二维码并显示
                    File file = new File(getBaseContext().getFilesDir(), "qr.jpg");
                    QRCodeUtils.createQRImage(mQrResult.getUrl(), 300, 300, null, file.getPath());
                    final Bitmap bMap = BitmapFactory.decodeFile(file.getPath());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mImageView.setImageBitmap(bMap);
                        }
                    });
                    check();
                }else {
                    LogHelper.d(TAG,"getQrUrl onFail");
                    ToastUtil.showShort("getQrUrl onFail");
                }
            }

            @Override
            public void onFail(int code, String result) {
                LogHelper.d(TAG,"getQrUrl onFail:"+code+":"+result);
                ToastUtil.showShort("getQrUrl onFail:" + code + ":"+result);
            }
        });
    }

    QrResult mQrResult;
    Timer timer;

    /**
     * 检查登录结果
     */
    private void check(){
        if (timer==null){
            timer = new Timer(true);
            timer.schedule(task,1000,1000);
        }
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            LogHelper.d(TAG,"qrresult...");
            QrNetworkHelper.getToken(mQrResult.getTime(), mQrResult.getAt(), new INetworkCallback() {
                @Override
                public void onSuccess(String result) {
                    LogHelper.d(TAG,"onSuccess:"+result);
                    QrLoginResult qrLoginResult = new QrLoginResult(result);
                    if (qrLoginResult.isSuccess()){
                        if (qrLoginResult.getCode().equals(QrLoginResult.LOGIN_SUCCESS)){
                            ToastUtil.showShort("login success");
                        }else if (qrLoginResult.getCode().equals(QrLoginResult.LOGIN_EXPIRED)){

                        }
                    }
                }

                @Override
                public void onFail(int code, String result) {
                    LogHelper.d(TAG,"onFail:"+result);
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null){
            timer.cancel();
        }
    }

    private TestThread mThread = new TestThread();
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
        mCircleView.scrollTo(100, 0);
    }

    private void getQrUrl(){
        try{
            URL url = new URL("http://www.baidu.com");
        }catch (Exception e){

        }
    }
}
