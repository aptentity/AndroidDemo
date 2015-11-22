package com.borg.androidemo.devices.api;

/**
 * 设备和手机的通信类型
 * Created by yiping.cyp on 2015/9/16.
 */
public enum AccessType {
    /**
     * 手机通过蓝牙BT方式和设备通信, 设备以bT为主要通信模式
     */
    BT_DEVICE_Direct,

    /**
     * 手机通过蓝牙BLE方式和设备通信, 设备以ble为主要通信模式
     */
    BLE_DEVICE_Direct,

    /**
     * 手机通过蓝牙BT方式和设备通信, bT只是辅助的通信模式。蓝牙通道不会进行认证，数据获取等
     */
    BT_DEVICE_Secondary,

    /**
     * 手机通过蓝牙BLE方式和设备通信, ble只是辅助的通信模式。蓝牙通道不会进行认证，数据获取等
     */
    BLE_DEVICE_Secondary,

    /**
     * 设备通过2G/3G/4G上网，inwatch手表和儿童手表都是这种类型
     */
    CARRIER_DEVICE
}
