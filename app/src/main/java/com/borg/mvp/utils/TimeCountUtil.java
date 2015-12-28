package com.borg.mvp.utils;

import android.os.CountDownTimer;

/**
 * Created by Gulliver(feilong) on 15/12/21.
 * 倒计时
 */
public class TimeCountUtil extends CountDownTimer{
    private ITimeCountListener listener;
    public TimeCountUtil(long millisInFuture, long countDownInterval, ITimeCountListener listener){
        super(millisInFuture,countDownInterval);
        this.listener = listener;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        listener.onTick(millisUntilFinished);
    }

    @Override
    public void onFinish() {
        listener.onFinish();
    }

    static public interface ITimeCountListener{
        public void onTick(long millisUntilFinished);
        public void onFinish();
    }
}
