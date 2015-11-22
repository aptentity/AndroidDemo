package com.borg.androidemo.devices.connection.bluetooth.ble.callback;

import android.bluetooth.BluetoothDevice;

import com.borg.androidemo.devices.device.bluetoothdevice.BluetoothDev;


public interface IAliBLEConnectCallback {
	void onConnected(BluetoothDevice device);
	void onDisconnected(BluetoothDev device);
	void onConnecting(BluetoothDevice device);
}