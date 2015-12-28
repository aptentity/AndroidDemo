package com.borg.androidemo.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.borg.androidemo.R;
import com.borg.androidemo.service.MessengerService;
import com.borg.mvp.settings.Constants;
import com.borg.mvp.utils.LogUtil;

public class MessengerActivity extends AppCompatActivity {
    private static final String TAG = MessengerActivity.class.getSimpleName();
    private Messenger mService;
    private Messenger mPeplyMessenger = new Messenger(new MessengerHandler());
    private static class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.MSG_FROM_SERVICE:
                    LogUtil.d(TAG,"receiver msg from Service:"+msg.getData().getString("reply"));
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            Message msg = Message.obtain(null, Constants.MSG_FROM_CLIENT);
            Bundle data = new Bundle();
            data.putString("msg", "hello, this is client");
            msg.setData(data);
            msg.replyTo = mPeplyMessenger;
            try{
                mService.send(msg);
            }catch (Exception e){

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        Intent intent = new Intent(this, MessengerService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }
}
