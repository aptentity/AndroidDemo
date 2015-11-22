package com.borg.androidemo.devices.api;


import com.borg.androidemo.devices.protocol.ResponseCode;

/**
 * Created by junxu on 15/9/10.
 */
public interface DeviceProperty {
    /**
     * 获取设备属性的监听器
     */
    public interface PropertyListener extends ResponseCode {
        public void onSuccess();
        public void onFail(int failCode);
    }

    public void RefreshProperty(DeviceConnection conn, final PropertyListener listener);

    public String toString();
    public String getVersion();

    public String getType();

    public String getManufacturer();

    public String getModel();

    public String getDeviceModel();

    public String getInternalVersion();

    public String getInternalName();
}
