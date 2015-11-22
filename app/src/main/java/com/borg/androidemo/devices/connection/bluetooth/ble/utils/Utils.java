package com.borg.androidemo.devices.connection.bluetooth.ble.utils;

public class Utils {

	public static byte[] clonebytes(final byte[] original) {
		if (null == original || 0 >= original.length) {
			return null;
		}
		final byte[] ret = new byte[original.length];
		for (int i = 0; i < original.length; ++i) {
			ret[i] = original[i];
		}
		return ret;
	}
}
