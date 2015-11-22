package com.borg.androidemo.devices.connection.bluetooth.ble.callback;

import android.bluetooth.BluetoothDevice;

import java.util.List;

public interface IAliBLEScanCallback {
	void onScannedBLEDevicesUpdated(List<BluetoothDevice> devices);

	void onScanStart();

	void onScanStop();
}