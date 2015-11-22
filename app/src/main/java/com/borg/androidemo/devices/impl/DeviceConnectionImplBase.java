package com.borg.androidemo.devices.impl;

import com.borg.androidemo.devices.api.DeviceConnection;

/**
 * Created by wuzonglu on 15/10/29.
 */
abstract public class DeviceConnectionImplBase extends DeviceConnection {

    protected DeviceDataDispatch mDataDispatch = new DeviceDataDispatch();

    /**注册数据接收器
     * @param observer 接收接口
     * @param category 数据类型
     */
    public void registerDeviceDataObserver (final DeviceDataObserver observer, final int category)
    {
        mDataDispatch.insertObserver(observer, category);
    }

    /**取消注册数据接收器
     * @param observer 接收接口
     * @param category 数据类型
     */
    public void unRegisterDeviceDataObserver (final DeviceDataObserver observer, final int category)
    {
        mDataDispatch.removeObserver(observer, category);
    }

    /**注册服务器数据接收器
     * @param observer 接收接口
     * @param cmd   命令
     */
    public void registerServerDataObserver (final ServerDataObserver observer, final String cmd)
    {
        mDataDispatch.insertServerObserver(observer, cmd);
    }

    /**取消注册服务器数据接收器
     * @param observer 接收接口
     * @param cmd 命令
     */
    public void unRegisterServerDataObserver (final ServerDataObserver observer, final String cmd)
    {
        mDataDispatch.removeServerObserver (observer, cmd);
    }

    /** 接收到设备主动发送的数据
     * @param data
     */
    abstract public void onReceiveData (final String data, final int category);

    /** 接收到服务端主动发送的数据
     * @param data
     */
    public void onReceiveSvrData (final String data)
    {}
}
