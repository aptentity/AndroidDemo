package com.borg.androidemo.devices.connection.bluetooth.ble.listener;

import android.os.Handler;

public class Listener {

    protected Handler mListenerHandler;

    /**
     * 构�?�方法�?��?�过构�?�方法传入Handler参数，可以配置执行回调的线程�?
     * 譬如，如果想在UI线程执行回调，传入UI线程的Handler。如果传入null，则在工作线程中执行�?
     * 
     * @param listenerHandler
     *            执行回调的线程的Handler�?
     */
    public Listener(Handler listenerHandler) {

        mListenerHandler = listenerHandler;
    }
}
