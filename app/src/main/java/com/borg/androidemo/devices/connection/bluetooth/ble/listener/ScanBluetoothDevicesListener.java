package com.borg.androidemo.devices.connection.bluetooth.ble.listener;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import java.util.List;

public abstract class ScanBluetoothDevicesListener extends Listener {
	public ScanBluetoothDevicesListener(final Handler listenerHandler) {
		super(listenerHandler);
	}

	public abstract void onScannedBLEDevicesUpdated(
			List<BluetoothDevice> devices);

	public abstract void onScanStart();

	public abstract void onScanStop();

	public final void onScannedBLEDevicesUpdatedInternal(
			final List<BluetoothDevice> devices) {
		if (null != mListenerHandler) {
			mListenerHandler.post(new Runnable() {

				@Override
				public void run() {
					onScannedBLEDevicesUpdated(devices);
				}
			});
		} else {
			onScannedBLEDevicesUpdated(devices);
		}
	}

	public final void onScanStartInternal() {
		if (null != mListenerHandler) {
			mListenerHandler.post(new Runnable() {

				@Override
				public void run() {
					onScanStart();
				}
			});
		} else {
			onScanStart();
		}
	}

	public final void onScanStopInternal() {
		if (null != mListenerHandler) {
			mListenerHandler.post(new Runnable() {

				@Override
				public void run() {
					onScanStop();
				}
			});
		} else {
			onScanStop();
		}
	}
}
