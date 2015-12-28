package com.borg.androidemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import com.borg.mvp.settings.Constants;
import com.borg.mvp.utils.LogUtil;

/**
 * Created by Gulliver(feilong) on 15/12/18.
 */
public class MessengerService extends Service{
    private static final String TAG = MessengerService.class.getSimpleName();

    private static class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.MSG_FROM_CLIENT:
                    LogUtil.d(TAG,"receiver client message:"+msg.getData().getString("msg"));
                    Messenger client = msg.replyTo;
                    Message replyMsg = Message.obtain(null,Constants.MSG_FROM_SERVICE);
                    Bundle bundle = new Bundle();
                    bundle.putString("reply","reply later");
                    replyMsg.setData(bundle);
                    try {
                        client.send(replyMsg);
                    }catch (Exception e){}
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }
    private final Messenger mMessenger = new Messenger(new MessengerHandler());
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }


}
