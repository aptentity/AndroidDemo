package com.borg.androidemo.devices.connection;

import com.borg.androidemo.common.utils.CKLOG;
import com.borg.androidemo.devices.device.bluetoothdevice.BluetoothDev;

import java.util.HashMap;

/**
 * Created by yiping.cyp on 2015/9/17.
 */
public class DeviceConnectionContainer extends HashMap<String, BluetoothDev> {


    private static final String TAG = DeviceConnectionContainer.class.getSimpleName();

    public DeviceConnectionContainer() {
        super();
    }

    @Override
    public synchronized BluetoothDev remove(Object key) {
        CKLOG.Debug(TAG, "remove Device: " + key + "from Device Container...");
        return super.remove(key);
    }

    @Override
    public synchronized BluetoothDev put(String key, BluetoothDev value) {
        CKLOG.Debug(TAG, "put Device: " + key + "Value:" + value + " to Device Container...");
        return super.put(key, value);
    }

    @Override
    public synchronized boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    @Override
    public synchronized boolean containsValue(Object value) {
        return super.containsValue(value);
    }

    @Override
    public synchronized BluetoothDev get(Object key) {
        return super.get(key);
    }
}
