package com.borg.androidemo.devices.connection.bluetooth.ble.service.characteristic;

import android.bluetooth.BluetoothDevice;

public class CharacteristicMsgObject {

	public BluetoothDevice device;
	public String serviceUuid;
	public String characteristicUuid;
	public byte[] characteristicValue;
	public int status;
	public int clientId;
	public int seqId;
}
