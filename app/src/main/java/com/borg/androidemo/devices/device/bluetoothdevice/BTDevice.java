package com.borg.androidemo.devices.device.bluetoothdevice;

import com.borg.androidemo.devices.api.AccessType;

/**
 * Created by yiping.cyp on 2015/9/18.
 */
public class BTDevice extends BluetoothDev {
    private static final String TAG = BTDevice.class.getSimpleName();

    public BTDevice(String deviceAddr, AccessType type) {
        super(deviceAddr, type);
    }

}
