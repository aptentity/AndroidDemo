package com.borg.mvp.view.widget;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.borg.androidemo.R;
import com.borg.mvp.utils.RenderScriptBlurHelper;

/**
 * Created by Gulliver(feilong) on 16/4/8.
 */
public abstract class BlurDialog extends Dialog {

    private Activity mOwnerActivity; // 这个Dialog依附的Activity
    private ImageView blurImage; // 显示模糊的图片
    private ImageView blurAlpha; // 显示透明度
    private FrameLayout showView; // 要往上面添加布局的父控件
    private AlphaAnimation alphaAnimation; // 透明变化
    private Animation dialogInAnim; // Dialog进入动画
    private Bitmap bitmap;

    public BlurDialog(Activity activity) {
        super(activity, R.style.Transparent);
        mOwnerActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_blur_bg_layout);
        blurImage = (ImageView) findViewById(R.id.iv_blur_show);
        blurAlpha = (ImageView) findViewById(R.id.iv_blur_alpha);
        showView = (FrameLayout) findViewById(R.id.fl_add_views);

        // 默认设置透明颜色为半透明黑色
        blurAlpha.setBackgroundColor(0x77000000);

        // 背景透明动画
        alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);

        // 创建Dialog
        onCreateDialog();
    }

    protected void setShowInAnimation(Animation dialogInAnim) {
        this.dialogInAnim = dialogInAnim;
    }

    /**
     * 用这个方法来代替之前的onCreate()方法
     */
    protected abstract void onCreateDialog();

    /**
     * 设置高斯模糊的前景颜色
     *
     * @param color 前景的颜色（使用ARGB来设置）
     */
    protected void setFilterColor(int color) {
        blurAlpha.setBackgroundColor(color);
    }

    /**
     * 此方法用来添加要在透明背景上显示的布局
     *
     * @param layoutResId
     */
    protected void setDialogView(int layoutResId) {
        showView.addView(View.inflate(getContext(), layoutResId, null));
    }

    public Activity getDialogActivity() {
        return mOwnerActivity;
    }

    /**
     * 显示Dialog类，同时进行动画播放
     */
    @Override
    public void show() {
        super.show();
        // 开始截屏并进行高斯模糊
        new BlurAsyncTask().execute();
        // 背景开始渐变
        if (alphaAnimation != null) {
            blurAlpha.startAnimation(alphaAnimation);
        }
        // 框弹出的动画
        if (dialogInAnim != null) {
            showView.startAnimation(dialogInAnim);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        // 要设置下面这个，不然下次截图会返回上次的画面，不能实时更新
        mOwnerActivity.getWindow().getDecorView().setDrawingCacheEnabled(false);
        // 对bitmap进行回收
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    /**
     * 实现高斯模糊的任务
     */
    private class BlurAsyncTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // 截图
            mOwnerActivity.getWindow().getDecorView().setDrawingCacheEnabled(true);
            bitmap = mOwnerActivity.getWindow().getDecorView().getDrawingCache();
            //BitmapUtils.saveBitmapToFile(bitmap, "/sdcard/xinglian/my.jpg", Integer.MAX_VALUE);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            // 进行高斯模糊
            if (bitmap != null) {
                bitmap = RenderScriptBlurHelper.doBlur(bitmap, 5, false, mOwnerActivity);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (bitmap != null) {
                blurImage.setImageBitmap(bitmap);
            }
        }
    }

}