package com.borg.androidemo.devices.impl;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import com.borg.androidemo.common.utils.CKLOG;
import com.borg.androidemo.devices.api.AccessType;
import com.borg.androidemo.devices.api.BindType;
import com.borg.androidemo.devices.api.Device;
import com.borg.androidemo.devices.api.DeviceManager;
import com.borg.androidemo.devices.connection.bluetooth.AliBluetoothManager;
import com.borg.androidemo.devices.connection.bluetooth.ble.listener.ScanBluetoothDevicesListener;
import com.borg.androidemo.devices.init.CloudKitProfile;
import com.borg.androidemo.devices.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuzonglu on 15/10/10.
 */
public class DeviceManagerImpl extends DeviceManager {
    private static final String TAG = DeviceManagerImpl.class.getSimpleName();
    private List<Device> devices = new ArrayList<>();
    static private DeviceManagerImpl mInstance;


    public static DeviceManagerImpl instance() {
        if (mInstance == null) {
            synchronized (DeviceManager.class) {
                if (mInstance == null) {
                    mInstance = new DeviceManagerImpl();
                }
            }
        }
        return mInstance;
    }

    public void init() {
        AliBluetoothManager.instance().init(CloudKitProfile.instance().getContext());

//        ArrayList<String> addrs = SharedPreferencesUtil.getDeviceList(SharedPreferencesUtil.getActiveKp());
//        if (addrs != null) {
//            for (String addr : addrs) {
//                boolean bFound = false;
//                for (Device dev : devices) {
//                    if (dev.getAddress().equals(addr)) {
//                        bFound = true;
//                        break;
//                    }
//                }
//
//                if (!bFound) {
//                    BindType type = SharedPreferencesUtil.getDeviceType(SharedPreferencesUtil.getActiveKp(), addr);
//                    DeviceImpl device = new DeviceImpl(addr, type);
//                    devices.add(device);
//                }
//            }
//        }
    }

    /**
     * 添加一台设备
     *
     * @param deviceAddr 设备地址信息
     * @param type       BindType 设备绑定类型
     *                   BINDER_BT, BINDER_BLE： deviceAddr传入mac地址
     *                   BINDER_INWATCH:  deviceAddr传入cuuid
     *                   BINDER_JSONINFO  deviceAddr传入是包含完整JSON信息的字符串
     * @return 设备实例对象
     */
    public synchronized Device addDevice(String deviceAddr, BindType type) {
        CKLOG.Debug(TAG, "addDevice,deviceAddr=" + deviceAddr + ";BindType=" + type.toString());
        if (TextUtils.isEmpty(deviceAddr) || type == null) {
            CKLOG.Error(TAG, "null params... return");
            return null;
        }

        //1.获取地址：对于json需要解析
        String key = deviceAddr;

        //////////////begin//////////////////
        //////////////如果是json,需要从json中解析出address//////////////
        if (type == BindType.BINDER_JSONINFO) {
//            QrcodeInterpreter.QrCodeJsonInfo info = QrcodeInterpreter.getQrJsonCode(deviceAddr);
//            if (info == null) {
//                CKLOG.Error(TAG, "invalid deviceAddr... return");
//                return null;
//            }
//            key = info.id;
        }
        /////////////end////////////////////
        CKLOG.Debug(TAG, "addDevice,key=" + key);

        //2.根据地址查找已添加的设备，如果找到直接返回，找不到则创建并保存
        for (Device dev : devices) {
            if (dev.getAddress().equals(key)) {
                CKLOG.Debug(TAG, "addDevice find device end");
                return dev;
            }
        }

        DeviceImpl device = new DeviceImpl(deviceAddr, type);
        CKLOG.Debug(TAG, "addDevice not find device ,new device" + device.toString());
        devices.add(device);

        //使用key 否则扫描逻辑异常
        SharedPreferencesUtil.addDevice(CloudKitProfile.instance().getKp(), key, type);

        //对于json应该保存
        if (type == BindType.BINDER_JSONINFO) {
            SharedPreferencesUtil.setQrcodejson(CloudKitProfile.instance().getKp(), key, deviceAddr);
        }

        return device;
    }

    /**
     * 删除一台设备
     *
     * @param device 设备实例对象
     */
    public synchronized void removeDevice(Device device) {
        for (Device dev : devices) {
            if (dev == device) {
                devices.remove(dev);
            }
        }
    }

    /**
     * 返回被管理的设备数量
     *
     * @return 被管理的设备数量
     */
    public synchronized int getDeviceCount() {
        return devices.size();
    }

    /**
     * 返回所有设备实例列表
     *
     * @return 所有被管理的设备对象列表
     */
    public synchronized List<Device> getAllDevices() {
        return devices;
    }

    /**
     * 根据索引返回设备对象
     *
     * @param index 索引
     * @return 设备对象
     */
    public synchronized Device getDevice(int index) {
        if (index < 0 || index >= devices.size()) {
            return null;
        }
        return devices.get(index);
    }

    /**
     * 通过cuuid返回设备对象
     *
     * @param cuuid 设备的cuuid
     * @return 设备对象
     */
    public synchronized Device getDeviceByCuuid(String cuuid) {
        for (Device dev : devices) {
            if (cuuid != null && dev.getCuuid().equalsIgnoreCase(cuuid)) {
                return dev;
            }
        }

        return null;
    }

    /**
     * 通过deviceToken返回设备对象
     *
     * @param deviceToken 设备的deviceToken
     * @return 设备对象
     */
    synchronized Device getDeviceByDeviceToken(String deviceToken) {
        for (Device dev : devices) {
            if (deviceToken != null && dev.getDeviceToken().equalsIgnoreCase(deviceToken)) {
                return dev;
            }
        }

        return null;
    }

    /**
     * 根据设备地址返回设备对象
     *
     * @param addr 设备对象的地址
     * @return 设备对象
     */
    public synchronized Device getDeviceByAddr(String addr) {

        for (Device dev : devices) {
            if (addr != null && dev.getAddress().equalsIgnoreCase(addr)) {
                return dev;
            }
        }
        return null;
    }

    /**
     * 返回指定设备类型枚举到的设备
     *
     * @param type 设备类型，目前只支持BLE和BT两种蓝牙设备
     * @return 枚举到的设备列表
     */
    public List<BluetoothDevice> enumBlueDevices(AccessType type) {
        return AliBluetoothManager.instance().getScannedBLEDevices(type);
    }

    /**
     * 搜索指定类型的设备
     *
     * @param type 设备类型，目前只支持BLE和BT两种蓝牙设备
     */
    public void startScanDevices(AccessType type) {
        AliBluetoothManager.instance().scanBluetoothDevices(type);
    }

    /**
     * 注册搜索监听回调接口
     *
     * @param listener 监听回调接口
     */
    public void registerScanBLEDevicesListener(ScanBluetoothDevicesListener listener) {
        AliBluetoothManager.instance().registerScanBLEDevicesListener(listener);
    }

    /**
     * 取消注册监听
     *
     * @param listener 监听回调接口
     */
    public void unRegisterScanBLEDevicesListener(ScanBluetoothDevicesListener listener) {
        AliBluetoothManager.instance().unregisterScanBLEDevicesListener(listener);
    }
}
