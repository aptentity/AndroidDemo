package com.borg.androidemo.devices.connection.bluetooth.ble.callback;

import android.bluetooth.BluetoothDevice;

import java.util.List;

public interface IAliBLEServiceBinder {

	void sayHi(String words);
	void scanBLEDevices();
	void stopScanBLEDevices();
	List<BluetoothDevice> getScannedBLEDevices();
	List<BluetoothDevice> getScannedBLEWatches();

	void registerBLEScanCallback(IAliBLEScanCallback callback);
	void unregisterBLEScanCallback(IAliBLEScanCallback callback);

	void registerBLEConnectCallback(IAliBLEConnectCallback callback);
	void unregisterBLEConnectCallback(String addr);

	void registerBLECharacteristicCallback(IAliBLECharacteristicCallback callback, String packageName);
//	void registerBLECharacteristicCallback(IAliBLECharacteristicCallback callback, List<String> characteristicUuids, String packageName);
	void unregisterBLECharacteristicCallback(IAliBLECharacteristicCallback callback, List<String> characteristicUuids, String packageName);
	void clearBLECharacteristicCallback(String packageName);

	void registerBLENotificationCallback(IAliBLESendStateCallback callback, List<String> ids, String packageName);
	void unregisterBLENotificationCallback(IAliBLESendStateCallback callback, List<String> ids, String packageName);
	void clearBLENotificationCallback(String packageName);

	List<BluetoothDevice> getConnectedBLEDevices();
	List<BluetoothDevice> getConnectedBLEWatches();
	void connectToBLEDeviceByAddress(String deviceAddress, boolean autoConnect);
	void disconnectFromBLEDevice(String deviceAddress);

	void writeCharacteristicToBLEDeviceString(BluetoothDevice device, String serviceUuid, String characteristicUuid, String characteristicContent);
	void writeCharacteristicToBLEDeviceBytes(BluetoothDevice device, String serviceUuid, String characteristicUuid, byte[] characteristicContent);
	void readCharacteristicToBLEDevice(BluetoothDevice device, String serviceUuid, String characteristicUuid);

	void sendMessgeToBluetoothDevice(String address, String notification, IAliBLESendStateCallback callback);
	boolean setBLEDeviceCharacteristicNotification(BluetoothDevice device, String serviceUuid, String characteristicUuid, boolean enable);
}