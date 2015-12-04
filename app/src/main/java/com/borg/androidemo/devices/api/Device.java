package com.borg.androidemo.devices.api;


import com.borg.androidemo.devices.device.DeviceInfo;

/**本接口用来管理一台设备，
 * 通过DeviceManager.addDevice创建。
 * Created by   on 15/10/20.
 */
abstract public class Device {
    /**返回设备地址
     * @return 设备地址，蓝牙设备返回蓝牙mac地址，wifi或者2G/3G设备返回设备cuuid
     */
    abstract public String getAddress();

    /**返回设备绑定的UUID
     * @return 设备绑定的UUID
     */
    abstract public String getCuuid();

    /**获取设备的devicetoken
     * @return
     */
    abstract public String getDeviceToken();

    /**获取设备的详细信息
     * @return 返回详细的设备信息
     */
    abstract public DeviceInfo getDeviceInfo() ;

    /**获取默认的命令连接，所有和设备的直接通信（非服务器中转）都是通过这个接口来发送和接收
     * @return    DeviceConnection接口
     */
    abstract public DeviceConnection getDefaultDeviceConnection ();

    /**创建蓝牙连接
     * @param mac     蓝牙mac地址
     * @param type    蓝牙类型
     * @return    DeviceConnection接口
     */
    abstract public DeviceConnection CreateBlueDeviceConnection (String mac, AccessType type);

    /**销毁无用的连接
     * @param conn 带销毁连接
     */
    abstract public void RemoveDeviceConnection (DeviceConnection conn);

    /**
     * 获取设备属性对象
     *
     */
    abstract public DeviceProperty getDeviceProperty();

    /**
     * 获取手机和设备的绑定接口类
     *
     */
    abstract public DeviceBinder getDeviceBinder();

}
