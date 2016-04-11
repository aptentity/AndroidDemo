package com.borg.mvp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.borg.androidemo.R;
import com.borg.mvp.utils.LogHelper;
import com.borg.mvp.view.widget.BlurDialogTest;
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
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showAlertDialog();
                Intent intent = new Intent();
                intent.setClassName("com.aliyun.fota", "com.aliyun.fota.FotaUpdateInfo");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlurActivityTest.mHoldingActivity = BlurActivity.this;
                startActivity(new Intent(BlurActivity.this,BlurActivityTest.class));
            }
        });
    }

    private void showAlertDialog(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_D1NoTitleDim);
//        builder.setTitle("test");
//        builder.setMessage("my test");
//        builder.setCancelable(false);
//        builder.setPositiveButton("setting", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//            }
//        });
//        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
        BlurDialogTest dialogTest = new BlurDialogTest(this);
        dialogTest.show();
    }


    @Override
    protected void onStart() {
        //mBlurEngine.onResume(false);
        super.onStart();
    }

    @Override
    protected void onResume() {

        super.onResume();
//        mBlurEngine.onResume(false);
        //test();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mBlurEngine.onDestroy();
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
