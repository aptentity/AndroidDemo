package com.borg.androidemo.devices.api;

import android.bluetooth.BluetoothDevice;

import com.borg.androidemo.devices.connection.bluetooth.ble.listener.ScanBluetoothDevicesListener;
import com.borg.androidemo.devices.impl.DeviceManagerImpl;

import java.util.List;

/**
 * Created by junxu on 15/9/10.
 */
abstract public class DeviceManager {
    /**
     * @return 返回DeviceManager单例对象
     */
    public static DeviceManager instance() {
        return DeviceManagerImpl.instance();
    }

    /** 添加一台设备
     * @param deviceAddr   设备地址信息
     * @param type   BindType 设备绑定类型
     *           BINDER_BT, BINDER_BLE： deviceAddr传入mac地址
     *           BINDER_INWATCH:  deviceAddr传入cuuid
     *           BINDER_JSONINFO  deviceAddr传入是包含完整JSON信息的字符串
     * @return 设备实例对象
     */
    abstract public Device addDevice(String deviceAddr, BindType type);

    /** 删除一台设备
     * @param device 设备实例对象
     */
    abstract public void removeDevice(Device device);

    /**返回被管理的设备数量
     * @return 被管理的设备数量
     */
    abstract public int getDeviceCount();

    /**返回所有设备实例列表
     * @return 所有被管理的设备对象列表
     */
    abstract public List<Device> getAllDevices();

    /**根据索引返回设备对象
     * @param index 索引
     * @return 设备对象
     */
    abstract  public Device getDevice(int index);

    /**通过cuuid返回设备对象
     * @param cuuid 设备的cuuid
     * @return 设备对象
     */
    abstract public Device getDeviceByCuuid(String cuuid);

    /**根据设备地址返回设备对象
     * @param addr 设备对象的地址
     * @return 设备对象
     */
    abstract public Device getDeviceByAddr(String addr);

    /**返回指定设备类型枚举到的设备
     * @param type 设备类型，目前只支持BLE和BT两种蓝牙设备
     * @return 枚举到的设备列表
     */
    abstract public List<BluetoothDevice> enumBlueDevices(AccessType type);
    /**搜索指定类型的设备
     * @param type 设备类型，目前只支持BLE和BT两种蓝牙设备
     */
    abstract public void startScanDevices(AccessType type);

    /**
     * 注册搜索监听回调接口
     *
     * @param listener 监听回调接口
     */
    abstract public void registerScanBLEDevicesListener(ScanBluetoothDevicesListener listener);

    /**
     * 取消注册监听
     *
     * @param listener 监听回调接口
     */
    abstract public void unRegisterScanBLEDevicesListener(ScanBluetoothDevicesListener listener);
}
