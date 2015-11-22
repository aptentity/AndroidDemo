package com.borg.androidemo.devices.impl;


import com.borg.androidemo.devices.api.AccessType;
import com.borg.androidemo.devices.api.BindType;
import com.borg.androidemo.devices.api.Device;
import com.borg.androidemo.devices.api.DeviceBinder;
import com.borg.androidemo.devices.api.DeviceConnection;
import com.borg.androidemo.devices.api.DeviceProperty;
import com.borg.androidemo.devices.device.DeviceInfo;
import com.borg.androidemo.devices.utils.SharedPreferencesUtil;

import java.util.ArrayList;


/**
 * Created by wuzonglu on 15/10/20.
 */
public class DeviceImpl extends Device {
    private static final String TAG = DeviceImpl.class.getSimpleName();
    private String mCuuid;
    private BindType mType;
    private String mAddress;
    private String mBindInfo;
    private DeviceProperty mDeviceProperty = new DevicePropertyImpl();
    private DeviceBinder mDeviceBinder = null;
    private ArrayList<DeviceConnectionImplBase> connections = new ArrayList<>();

    @Override
    public String toString() {
        return "DeviceImpl{" +
                "mCuuid='" + mCuuid + '\'' +
                ", mType=" + mType +
                ", mAddress='" + mAddress + '\'' +
                ", mDeviceProperty=" + mDeviceProperty +
                ", mDeviceBinder=" + mDeviceBinder +
                ", connections=" + connections +
                '}';
    }

    /**
     * @param deviceAddr   设备地址，对于蓝牙设备是mac地址，wifi设备是ip地址
     * @param type   设备类型
     */
    public DeviceImpl(String deviceAddr, BindType type) {

        mType = type;

        if (type == BindType.BINDER_INWATCH) {
            mAddress = deviceAddr;
            mCuuid = deviceAddr;
        } else if (type == BindType.BINDER_JSONINFO) {
//            QrcodeInterpreter.QrCodeJsonInfo info = QrcodeInterpreter.getQrJsonCode(deviceAddr);
//            mAddress = info.id;
//            mBindInfo = deviceAddr;
//            mCuuid = SharedPreferencesUtil.getCuuid(SharedPreferencesUtil.getActiveKp(), mAddress);
        } else {
            mAddress = deviceAddr;
            mCuuid = SharedPreferencesUtil.getCuuid(SharedPreferencesUtil.getActiveKp(), mAddress);
        }
    }

    public void setCuuid(String cuuid) {
        this.mCuuid = cuuid;
    }

    /**
     * @return 返回设备地址
     */
    public String getAddress() {
        return mAddress;
    }

    /**
     * @return 返回设备绑定的UUID
     */
    public String getCuuid() {
        return mCuuid;
    }

    public String getDeviceToken() {
        return SharedPreferencesUtil.getDeviceToken(SharedPreferencesUtil.getActiveKp(), mCuuid);
    }

    /**获取设备的详细信息
     * @return 返回详细的设备信息
     */
    public DeviceInfo getDeviceInfo() {
        return SharedPreferencesUtil.getDeviceInfo(SharedPreferencesUtil.getActiveKp(), mAddress);
    }

    /**判断设备是否在线
     * @return 是否在线
     */
    public boolean isOnline ()
    {
        return false;
    }

    /**获取默认的命令连接，所有和设备的直接通信（非服务器中转）都是通过这个接口来发送和接收
     * @return    DeviceConnection接口
     */
    public DeviceConnectionImplBase getDefaultDeviceConnection ()
    {
        if (mType == BindType.BINDER_BLE || mType == BindType.BINDER_BT) {
            return CreateBlueDeviceConnection(mAddress, mType == BindType.BINDER_BLE? AccessType.BLE_DEVICE_Direct : AccessType.BT_DEVICE_Direct);
        } else if (mType == BindType.BINDER_INWATCH || mType == BindType.BINDER_JSONINFO) {
            //return CreateCarrierDeviceConnection(mCuuid);
        }

        return null;
    }

    /**创建蓝牙连接
     * @param mac     蓝牙mac地址
     * @param type    蓝牙类型
     * @return    DeviceConnection接口
     */
    public DeviceConnectionImplBase CreateBlueDeviceConnection (String mac, AccessType type)
    {
        for (DeviceConnectionImplBase conn : connections) {
            if (conn.getAccessType() == type && conn.getAddress() == mac) {
                return conn;
            }
        }

        DeviceConnectionImplBase conn = new BluetoothDeviceConnection(mac, mCuuid, type);
        connections.add(conn);
        return conn;
    }

    /**创建蓝牙连接
     * @param cuuid    设备cuuid
     * @return    DeviceConnection接口
     */
//    public DeviceConnectionImplBase CreateCarrierDeviceConnection (String cuuid)
//    {
//        for (DeviceConnectionImplBase conn : connections) {
//            if (conn.getAccessType() == AccessType.CARRIER_DEVICE) {
//                return conn;
//            }
//        }
//
//        DeviceConnectionImplBase conn = new CarrierDeviceConnection(this);
//        connections.add(conn);
//        return conn;
//    }

    /**销毁无用的连接
     * @param conn 带销毁连接
     */
    public void RemoveDeviceConnection (DeviceConnection conn)
    {
        connections.remove(conn);
    }

    /**
     * 获取设备属性对象
     *
     */
    public DeviceProperty getDeviceProperty()
    {
        return mDeviceProperty;
    }

    /**
     * 获取手机和设备的绑定接口类
     *
     */
    public DeviceBinder getDeviceBinder()
    {
        if (mDeviceBinder == null) {
            if (mType == BindType.BINDER_BLE || mType == BindType.BINDER_BT) {
                mDeviceBinder = new DeviceBinderBle(this);
            } else if (mType == BindType.BINDER_INWATCH) {
                //mDeviceBinder = new DeviceBinderInwatch(this);
            } else if (mType == BindType.BINDER_JSONINFO) {
                //mDeviceBinder = new DeviceBinderJsonInfo(this, mBindInfo);
            }
        }

        return mDeviceBinder;
    }


    /** 接收到设备主动发送的数据
     * @param data
     */
    public void onReceiveData (final String data, final int category)
    {
        getDefaultDeviceConnection().onReceiveData(data, category);
    }

    /** 接收到服务端主动发送的数据
     * @param data
     */
    public void onReceiveSvrData (final String data)
    {
        getDefaultDeviceConnection().onReceiveSvrData(data);
    }
}
