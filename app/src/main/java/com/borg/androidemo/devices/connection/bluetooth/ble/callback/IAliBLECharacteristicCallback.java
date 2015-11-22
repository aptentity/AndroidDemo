package com.borg.androidemo.devices.connection.bluetooth.ble.callback;

import android.bluetooth.BluetoothDevice;

public interface IAliBLECharacteristicCallback {
	void onCharacteristicRead(BluetoothDevice device, String serviceUuid, String characteristicUuid, byte[] characteristicValue, int status);
	void onCharacteristicWrite(BluetoothDevice device, String serviceUuid, String characteristicUuid, byte[] characteristicValue, int status);
	void onCharacteristicChanged(BluetoothDevice device, String serviceUuid, String characteristicUuid, byte[] characteristicValue);
}