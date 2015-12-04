package com.borg.androidemo.devices.impl;

import android.os.Handler;
import android.os.Looper;

import com.borg.androidemo.common.utils.CKLOG;
import com.borg.androidemo.devices.api.DeviceConnection;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by   on 15/10/22.
 */
public class DeviceDataDispatch {
    private static final java.lang.String TAG = DeviceDataDispatch.class.getSimpleName();

    class DataObserverModel {
        public DeviceConnection.DeviceDataObserver mObserver;
        public Handler mHandler;

        public DataObserverModel(DeviceConnection.DeviceDataObserver observer) {
            mObserver = observer;
            mHandler = new Handler(Looper.myLooper());
        }
    }

    ;

    protected HashMap<Integer, ArrayList<DataObserverModel>> mObservers = new HashMap<>();

    class ServerDataObserverModel {
        public DeviceConnection.ServerDataObserver mObserver;
        public Handler mHandler;

        public ServerDataObserverModel(DeviceConnection.ServerDataObserver observer) {
            mObserver = observer;
            mHandler = new Handler(Looper.myLooper());
        }
    }

    ;
    protected HashMap<String, ArrayList<ServerDataObserverModel>> mCmdObservers = new HashMap<>();


    public void insertObserver(final DeviceConnection.DeviceDataObserver observer, final int category) {
        if (observer == null) {
            return;
        }

        ArrayList<DataObserverModel> values = mObservers.containsKey(category) ? mObservers.get(category) : new ArrayList<DataObserverModel>();
        for (DataObserverModel v : values) {
            if (v.mObserver == observer) {
                return;
            }
        }

        values.add(new DataObserverModel(observer));
    }

    public void removeObserver(final DeviceConnection.DeviceDataObserver observer, final int category) {
        if (observer == null || !mObservers.containsKey(category)) {
            return;
        }

        ArrayList<DataObserverModel> values = mObservers.get(category);
        for (DataObserverModel v : values) {
            if (v.mObserver == observer) {
                values.remove(v);
                return;
            }
        }
    }

    public void dispatch(final String data, final int category) {
        if (!mObservers.containsKey(category)) {
            return;
        }

        ArrayList<DataObserverModel> values = mObservers.get(category);
        for (final DataObserverModel v : values) {
            v.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    v.mObserver.onReceiveData(data, category);
                }
            });
        }
    }

    public void insertServerObserver(final DeviceConnection.ServerDataObserver observer, final String cmd) {
        if (observer == null) {
            return;
        }
        CKLOG.Debug(TAG, "befoSre insert mCmdObservers=" + mCmdObservers);

        ArrayList<ServerDataObserverModel> values = mCmdObservers.containsKey(cmd) ? mCmdObservers.get(cmd) : new ArrayList<ServerDataObserverModel>();
        for (ServerDataObserverModel v : values) {
            if (v.mObserver == observer) {
                return;
            }
        }

        values.add(new ServerDataObserverModel(observer));
        mCmdObservers.put(cmd, values);
    }

    public void removeServerObserver(final DeviceConnection.ServerDataObserver observer, final String cmd) {
        if (observer == null || !mCmdObservers.containsKey(cmd)) {
            return;
        }

        ArrayList<ServerDataObserverModel> values = mCmdObservers.get(cmd);
        for (ServerDataObserverModel v : values) {
            if (v.mObserver == observer) {
                values.remove(v);
                return;
            }
        }
    }

    public void dispatch(final String data, final String cmd) {

        if (!mCmdObservers.containsKey(cmd)) {
            CKLOG.Error(TAG, "in DeviceDataDispatch dispatch found mCmdObservers do not contains key:" + cmd);
            CKLOG.Error(TAG, "in DeviceDataDispatch dispatch found mCmdObservers=" + mCmdObservers.toString());
            return;
        }

        ArrayList<ServerDataObserverModel> values = mCmdObservers.get(cmd);
        CKLOG.Debug(TAG, "in DeviceDataDispatch dispatch found mCmdObservers contains key :" + cmd + ",values=" + values.toString());
        for (final ServerDataObserverModel v : values) {
            v.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    v.mObserver.onReceiveData(data, cmd);
                }
            });
        }
    }
}
