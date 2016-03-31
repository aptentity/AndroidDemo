package com.borg.mvp.view;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.borg.androidemo.R;
import com.borg.mvp.utils.LogHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

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
        test();
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

    private static final ExecutorService executor =Executors.newCachedThreadPool();

    private void test(){
        LogHelper.d("zfl","test begin");


        Runnable command = new Runnable() {
            @Override
            public void run() {
                LogHelper.d("zfl","runing...");
            }
        };
        executor.execute(command);


        FutureTask<File> futureTask = new FutureTask<File>(new Callable<File>() {
            @Override
            public File call() throws Exception {
                FutureTarget<File> fileFutureTarget = Glide.with(getApplicationContext()).load("http://pic.pp3.cn/uploads//20120713j/460.jpg").
                        downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                try{
                    File file =  fileFutureTarget.get();
                    LogHelper.d("zfl","fileFutureTarget.get end");
                    LogHelper.d("zfl","path="+file.getAbsolutePath());
                    return file;
                }catch (Exception e){
                    LogHelper.d("zfl","Exception="+e.getMessage());
                    return null;
                }
            }
        });
        executor.submit(futureTask);
        try{
            LogHelper.d("zfl","futureTask begin");
            //futureTask.get();
            LogHelper.d("zfl","futureTask end");
        }catch (Exception e){

        }

    }
}
