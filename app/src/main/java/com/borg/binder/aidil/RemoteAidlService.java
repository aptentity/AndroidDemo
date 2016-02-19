package com.borg.binder.aidil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.borg.mvp.utils.LogHelper;

/**
 * Created by Gulliver(feilong) on 16/2/19.
 */
public class RemoteAidlService extends Service{
    private final String TAG = RemoteAidlService.class.getSimpleName();
    @Override
    public IBinder onBind(Intent intent) {
        LogHelper.d("onBind");
        LogHelper.d(TAG, Thread.currentThread().getName() + ";" + Thread.currentThread().getId() + ";" + android.os.Process.myPid());
        return mBinder;
    }

    private final IMyAidlInterface.Stub mBinder = new IMyAidlInterface.Stub(){

        @Override
        public int add(int a, int b) throws RemoteException {
            LogHelper.d("add "+a+"+"+b);
            LogHelper.d(TAG, Thread.currentThread().getName() + ";" + Thread.currentThread().getId() + ";" + android.os.Process.myPid());
            return a+b;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        LogHelper.d("onCreate");
        LogHelper.d(TAG, Thread.currentThread().getName() + ";" + Thread.currentThread().getId() + ";" + android.os.Process.myPid());
    }
}
