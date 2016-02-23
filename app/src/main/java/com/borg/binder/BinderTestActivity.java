package com.borg.binder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.borg.androidemo.R;
import com.borg.binder.aidil.IMyAidlInterface;
import com.borg.mvp.utils.LogHelper;

public class BinderTestActivity extends AppCompatActivity{
    private final String TAG = BinderTestActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidltest);
    }

    public void onBinderClick(View v) {
        switch (v.getId()){
            case R.id.btn_aidl:
                bindAidlService();
                break;
            case R.id.btn_aidl_add:
                if (mRemoteAIDLService!=null){
                    try{
                        mRemoteAIDLService.add(2,3);
                        LogHelper.d(TAG,"add in ui");
                        LogHelper.d(TAG, Thread.currentThread().getName() + ";" + Thread.currentThread().getId() + ";" + android.os.Process.myPid());
                    }catch (RemoteException e){

                    }
                }
                break;
            case R.id.btn_binder:
                break;
            case R.id.btn_messenger:
                break;
        }
    }

    /********************aidl test begin*******************/
    private void bindAidlService(){
        LogHelper.d(TAG,"bindAidlService");
        Intent intent = new Intent("com.borg.myaidl");
        intent.setPackage("com.borg.androidemo");
        bindService(intent, mRemoteAIDLConnection, Context.BIND_AUTO_CREATE);
    }

    private IMyAidlInterface mRemoteAIDLService;
    private ServiceConnection mRemoteAIDLConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogHelper.d(TAG,"onServiceConnected");
            LogHelper.d(TAG, Thread.currentThread().getName() + ";" + Thread.currentThread().getId() + ";" + android.os.Process.myPid());
            mRemoteAIDLService = IMyAidlInterface.Stub.asInterface(service);
            try{
                mRemoteAIDLService.add(1,2);
            }catch (RemoteException e){

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogHelper.d(TAG,"onServiceDisconnected");
        }
    };
    /********************aidl test end*******************/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRemoteAIDLService!=null){
            unbindService(mRemoteAIDLConnection);
        }
    }
}

