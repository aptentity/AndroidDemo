package com.borg.androidemo.devices.connection.bluetooth.ble.callback;

import android.bluetooth.BluetoothDevice;

public interface IAliBLESendStateCallback {

	public static final int FAIL_CODE_BT_OFF = 10051;
	public static final int FAIL_CODE_DEVICE_NOT_CONNECTED = 10052;
	public static final int FAIL_CODE_SENDING_NULL_CONTENT = 10053;
	public static final int FAIL_CODE_EXCEPTION = 10054;
	public static final int FAIL_CODE_JSON_EXCEPTION = 10055;
	public static final int FAIL_CODE_SEND_MSG = 10056;


	void onSendMessageCompleted(String address, String notification);
	void onSendMessageFailed(String address, String notification, int errorCode);
	void onReceiveMessage(BluetoothDevice device, byte[] content);
}