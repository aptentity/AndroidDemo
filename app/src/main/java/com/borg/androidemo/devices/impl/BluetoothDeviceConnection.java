package com.borg.androidemo.devices.impl;

import android.os.Handler;
import android.os.Looper;

import com.borg.androidemo.common.utils.CKLOG;
import com.borg.androidemo.devices.api.AccessType;
import com.borg.androidemo.devices.api.DeviceConnectListener;
import com.borg.androidemo.devices.api.callback.SendDataCallback;
import com.borg.androidemo.devices.device.bluetoothdevice.BLEDevice;
import com.borg.androidemo.devices.device.bluetoothdevice.BTDevice;
import com.borg.androidemo.devices.device.bluetoothdevice.BluetoothDev;
import com.borg.androidemo.devices.device.bluetoothdevice.BluetoothDeviceListener;
import com.borg.androidemo.devices.init.CloudKitProfile;
import com.borg.androidemo.devices.utils.SharedPreferencesUtil;

import org.json.JSONObject;

/**
 * Created by   on 15/10/10.
 */
public class BluetoothDeviceConnection extends  DeviceConnectionImplBase implements BluetoothDeviceListener {
    private static final String TAG = BluetoothDeviceConnection.class.getSimpleName();
    private String mCuuid;
    private String mAddress;
    private AccessType accessType;
    private int mDeviceState;
    private DeviceConnectListener mDeviceConnectListener;
    private Handler mDeviceConnectHandler;
    BluetoothDev bledevice;

    /**
     * @param deviceAddr   蓝牙设备mac地址
     * @param cuuid        设备的cuuid
     * @param type         蓝牙连接类型
     */
    public BluetoothDeviceConnection(String deviceAddr, String cuuid, AccessType type) {
        mCuuid = cuuid;
        mAddress = deviceAddr;
        accessType = type;

        if (type == AccessType.BLE_DEVICE_Direct || type == AccessType.BLE_DEVICE_Secondary) {
            bledevice = new BLEDevice(deviceAddr, type);
            bledevice.registerListener(this);

        } else if (type == AccessType.BT_DEVICE_Direct || type == AccessType.BT_DEVICE_Secondary) {
            bledevice = new BTDevice(deviceAddr, type);
            bledevice.registerListener(this);
        } else {
            // TODO: 15/10/21
        }
    }

    /**
     * @return 返回设备地址
     */
    public String getAddress() {
        return mAddress;
    }

    /**返回设备和手机的通信类型
     * @return 设备和手机的通信类型
     */
    public AccessType getAccessType() {
        return accessType;
    }
    /**
     * @return 返回设备绑定的UUID
     */
    public String getCuuid() {
        return mCuuid;
    }

    public void setCuuid(String cuuid) {
        mCuuid = cuuid;
        SharedPreferencesUtil.setCuuid(CloudKitProfile.instance().getKp(), getAddress(), mCuuid);
    }

    /**
     * 异步方式连接设备
     * @param listener  设备连接结果回调接口
     */
    public void connectToDevice(DeviceConnectListener listener) {
        CKLOG.Debug(TAG, "connectToDevice:" + getAddress());

        if (mDeviceConnectHandler == null) {
            mDeviceConnectListener = listener;
            mDeviceConnectHandler = new Handler(Looper.myLooper());

            if (bledevice != null) {
                bledevice.connectToDevice();
            } else {
                CKLOG.Error(TAG, "device is null...");
            }
        } else {
            CKLOG.Error("Device is connecting!");
        }
    }

    /**返回手机是否正在连接设备
     * @return 返回设备是否连接中
     */
    public boolean isConnecting () {return mDeviceState == DEVICE_CONNECTING
            || mDeviceState == DEVICE_WAITFORAUTH;}

    /**判断手机和设备是否已连接
     * @return 返回设备是否已经连接
     */
    public boolean isConnected () {
        return mDeviceState == DEVICE_BINDED
                || mDeviceState == DEVICE_CONNECTED
                || mDeviceState == DEVICE_BINDBYOTHER;}

    /**返回手机是否和设备已经绑定
     * @return 返回设备是否和手机绑定
     */
    public boolean isBinded () { return mDeviceState == DEVICE_BINDED; }

    /**返回设备是否被其它手机绑定，若已经被其它设备绑定，需要先重置设备才可以绑定设备
     * @return 设备是否被其它手机绑定
     */
    public boolean deviceBindedByOther () {
        return mDeviceState == DEVICE_BINDBYOTHER;
    }

    /**
     * 断开手机和设备的连接
     */
    public void disconnectFromDevice() {
        CKLOG.Debug(TAG, "disconnectFromDevice...");
        bledevice.disconnectFromDevice();
    }

    /**
     * 发送数据给设备（手机和设备直接连接时才可以使用此接口）
     *
     * @param data            发送到设备的json数据
     * @param categoryCode    数据类型
     * @param callback        发送结果通知回调接口
     */
    public void sendData(final JSONObject data, final int categoryCode, final SendDataCallback callback) {
        bledevice.sendData(data.toString(), categoryCode, callback);
    }

    public void sendData(final String data, final int categoryCode, final SendDataCallback callback) {
        bledevice.sendData(data, categoryCode, callback);
    }

    /**
     * 设备连接状态变化通知。
     *
     * @param state  新的状态
     */
    public void onDeviceStateChange(final int state)
    {
        if (mDeviceState != state) {
            mDeviceState = state;

            mDeviceConnectHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mDeviceConnectListener != null) {
                        if (state == DEVICE_CONNECTING) {
                            CKLOG.Debug(TAG, "onConnecting:" + mAddress);
                            mDeviceConnectListener.onConnecting(BluetoothDeviceConnection.this);
                        } else if (state == DEVICE_BINDED || state == DEVICE_CONNECTED || state == DEVICE_BINDBYOTHER) {
                            CKLOG.Debug(TAG, "onConnected:" + mAddress + ", state=" + state);
                            mDeviceConnectListener.onConnected(BluetoothDeviceConnection.this, state);
                            mDeviceConnectHandler = null;
                        } else if (state == DEVICE_DISCONNECTED) {
                            CKLOG.Debug(TAG, "onDisconnected:" + mAddress);
                            mDeviceConnectListener.onDisconnected(BluetoothDeviceConnection.this);
                            mDeviceConnectHandler = null;
                        }
                    }
                }
            });
        }
    }


    /** 接收到设备主动发送的数据
     * @param data
     */
    public void onReceiveData (final String data, final int category)
    {
        mDeviceConnectHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mDataDispatch != null) {
                    mDataDispatch.dispatch(data, category);
                }
            }
        });
    }
}
