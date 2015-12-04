package com.borg.androidemo.devices.device.bluetoothdevice;

/**
 * Created by   on 15/10/21.
 */
public interface BluetoothDeviceListener {

    /**
     * 设备连接状态变化通知。
     *
     * @param state  新的状态
     */
    public void onDeviceStateChange(final int state);


    /** 接收到设备主动发送的数据
     * @param data        数据
     * @param category    数据类型
     */
    public void onReceiveData (final String data, final int category);

}
