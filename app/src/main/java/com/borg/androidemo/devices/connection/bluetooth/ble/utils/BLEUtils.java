package com.borg.androidemo.devices.connection.bluetooth.ble.utils;

import android.bluetooth.BluetoothDevice;

import com.borg.androidemo.devices.connection.bluetooth.ble.bledevice.BLEDeviceType;


public class BLEUtils {

	private static final String WATCH_DEVICE_NAME = "AliWatch";

	/**
	 * 获取设备类型
	 * 
	 * @param device
	 * @return
	 */
	public static BLEDeviceType getBLEDeviceType(final BluetoothDevice device) {
		if (null == device) {
			return BLEDeviceType.NULL;
		}
		if (isWatch(device)) {
			return BLEDeviceType.WATCH;
		}
		return BLEDeviceType.OTHER;
	}

	/**
	 * 判断是否是手表
	 * 
	 * @param device
	 * @return
	 */
	public static boolean isWatch(final BluetoothDevice device) {
		if (null == device) {
			return false;
		}
		return isWatchName(device.getName())
				|| isWatchAddress(device.getAddress());
	}

	/**
	 * 判读手表名字是否合法
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isWatchName(final String name) {
		return WATCH_DEVICE_NAME.equals(name);
	}

	/**
	 * 判断手表地址是否合法
	 * 
	 * @param address
	 * @return
	 */
	public static boolean isWatchAddress(final String address) {
		//return (null != address && address.startsWith("22:22"));
		return true;
	}

}
