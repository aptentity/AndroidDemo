package com.borg.androidemo.devices.api;


import com.borg.androidemo.devices.api.callback.SendDataCallback;

import org.json.JSONObject;


/**手机和设备的连接接口，本接口提供连接和断开功能及相应的状态通知，数据的发送和接收。
 * Created by junxu on 15/9/10.
 */
abstract public class DeviceConnection{

    public static final int DEVICE_UNCONNECT = 0;
    public static final int DEVICE_DISCONNECTED = 1;
    public static final int DEVICE_CONNECTING = 2;
    public static final int DEVICE_WAITFORAUTH = 4;
    public static final int DEVICE_CONNECTED = 5;
    public static final int DEVICE_BINDED = 6;
    public static final int DEVICE_BINDBYOTHER = 7;

    public interface DeviceDataObserver {

        /** 接收到设备主动发送的数据
         * @param data
         * @param category
         */
        public void onReceiveData (final String data, final int category);
    }

    public interface ServerDataObserver {

        /** 接收到设备主动发送的数据
         * @param data
         * @param cmd
         */
        public void onReceiveData (final String data, final String cmd);
    }

    /**返回设备地址
     * @return 设备地址，蓝牙设备返回蓝牙mac地址，wifi或者2G/3G设备返回设备cuuid
     */
    abstract public String getAddress();

    /**返回设备和手机的通信类型
     * @return 设备和手机的通信类型
     */
    abstract public AccessType getAccessType();

    /**返回设备的UUID
     * @return 设备的UUID
     */
    abstract public String getCuuid();

    /**返回手机是否正在连接设备
     * @return 返回设备是否连接中
     */
    abstract public boolean isConnecting ();

    /**判断手机和设备是否已连接
     * @return 返回设备是否已经连接
     */
    abstract public boolean isConnected ();

    /**
     * 异步方式连接设备
     * @param listener  设备连接结果回调接口
     */
    abstract public void connectToDevice(DeviceConnectListener listener);

    /**
     * 断开手机和设备的连接
     */
    abstract public void disconnectFromDevice();

    /**注册设备数据接收器
     * @param observer 接收接口
     * @param category 数据类型
     */
    abstract public void registerDeviceDataObserver (final DeviceDataObserver observer, final int category);

    /**取消注册设备数据接收器
     * @param observer 接收接口
     * @param category 数据类型
     */
    abstract public void unRegisterDeviceDataObserver (final DeviceDataObserver observer, final int category);

    /**注册服务器数据接收器
     * @param observer 接收接口
     * @param cmd   命令
     */
    abstract public void registerServerDataObserver (final ServerDataObserver observer, final String cmd);

    /**取消注册服务器数据接收器
     * @param observer 接收接口
     * @param cmd 命令
     */
    abstract public void unRegisterServerDataObserver (final ServerDataObserver observer, final String cmd);

    /**
     * 发送数据给设备，如果是蓝牙设备，则通过蓝牙发送，否则通过cmns上行转下行
     *
     * @param data            发送到设备的json数据
     * @param category    数据类型
     * @param callback        发送结果通知回调接口
     */
    abstract public void sendData(final JSONObject data, final int category, final SendDataCallback callback);
    abstract public void sendData(final String data, final int category, final SendDataCallback callback);
}


