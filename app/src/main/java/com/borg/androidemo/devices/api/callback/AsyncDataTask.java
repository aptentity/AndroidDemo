package com.borg.androidemo.devices.api.callback;

import com.borg.androidemo.common.utils.CKLOG;
import com.borg.androidemo.devices.protocol.ConnectionResponseCode;
import com.borg.androidemo.devices.protocol.ResponseCode;
import com.litesuits.android.async.AsyncTask;
import com.litesuits.android.async.TaskExecutor;

import java.util.concurrent.TimeUnit;

/**
 * Created by yiping.cyp on 2015/9/15.
 */
public class AsyncDataTask {


    private static final String TAG = "AsyncDataTask";

    private static AsyncDataTask mAsyncDataTask = new AsyncDataTask();
    private SendDataCallbackMap map;

    private AsyncDataTask() {
        map = SendDataCallbackMap.instance();
    }

    public static AsyncDataTask instance() {
        return mAsyncDataTask;
    }

    public boolean isRunning(String category) {
        return map.isCallbackValid(category);
    }

    public void add(SendDataCallback b) {
        // public void add(SendDataCallbackMap b, long nextSeqId) {
        map.put(b.getSeqId(), b);
        CKLOG.Info(TAG, "The SendDataCallbackMap " + b.getCatigory() + " is added,nextSeqId:" + b.getSeqId());
        setTimer(b);
    }

    public SendDataCallback remove(long seqId) {
        CKLOG.Debug(TAG, "in AsyncDataTask begin doing the map remove operation...");
        SendDataCallback callback = map.remove(seqId);
        if (callback != null) {
            CKLOG.Verbose(TAG, "in AsyncDataTask The SendDataCallbackMap " + seqId + " is removed");
        } else {
            CKLOG.Verbose(TAG, "in AsyncDataTask The SendDataCallbackMap " + seqId + " is not existed , retrun null!");
        }
        CKLOG.Verbose(TAG, "in AsyncDataTask the map remove operation complete...");
        return callback;
    }

    private void setTimer(final SendDataCallback b) {
        AsyncTask<Object, Void, SendDataCallback> task = new AsyncTask<Object, Void, SendDataCallback>() {

            @Override
            protected void onPostExecute(SendDataCallback result) {
                if (result == null) {
                    CKLOG.Debug(TAG, "The SendDataCallbackMap removed is null. may be receive the callback success before the latency time limit...");
                } else {
                    result.onFail(ResponseCode.FAIL_TIME_OUT);
                    CKLOG.Debug(TAG, "The SendDataCallbackMap " + result.getSeqId() + " is removed in PostExecute...timeout...");
                }
            }

            @Override
            protected SendDataCallback doInBackground(Object... params) {
                SendDataCallback result = mAsyncDataTask.remove(b.getSeqId());
                 if(result != null){
                    result.onFail(ConnectionResponseCode.FAIL_TIME_OUT);
                 }
                return result;
            }
        };
        TaskExecutor.startDelayedTask(task, b.getLatencyTime(), TimeUnit.SECONDS);
    }


}
