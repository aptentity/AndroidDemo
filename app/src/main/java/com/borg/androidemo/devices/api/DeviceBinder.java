package com.borg.androidemo.devices.api;

import com.borg.androidemo.devices.api.callback.BindingListener;

/**手机和设备的绑定接口类，通过
 * Device.getDeviceBinder获取。
 * Created by wuzonglu on 15/10/20.
 */
public interface DeviceBinder {
    /**
     * 异步方式绑定设备
     * @param listener    绑定结果通知回调接口
     */
    public void bindDevice(DeviceConnection conn, final BindingListener listener);


    /**异步方式解绑设备
     * @param listener 解绑结果通知回调接口
     */
    public void unbindDevice(DeviceConnection conn, final BindingListener listener);

    /**返回手机是否和设备已经绑定
     * @return 返回设备是否和手机绑定
     */
    public boolean isBinded();


//    /**返回设备是否被其它手机绑定，若已经被其它设备绑定，需要先重置设备才可以绑定设备
//     * @return 设备是否被其它手机绑定
//     */
//    public boolean deviceBindedByOther ();
}
