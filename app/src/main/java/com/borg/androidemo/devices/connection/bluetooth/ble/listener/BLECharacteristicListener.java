package com.borg.androidemo.devices.connection.bluetooth.ble.listener;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import com.borg.androidemo.common.utils.CKLOG;

import java.util.UUID;


public abstract class BLECharacteristicListener extends Listener {
	
	private String TAG = "BLECharacteristicListener";
	
	public BLECharacteristicListener(final Handler listenerHandler) {
		super(listenerHandler);
	}

	public abstract void onCharacteristicRead(BluetoothDevice device,
			UUID serviceUuid, UUID characteristicUuid,
			byte[] characteristicValue, int status);

	public abstract void onCharacteristicWrite(BluetoothDevice device,
			UUID serviceUuid, UUID characteristicUuid,
			byte[] characteristicValue, int status);

	public abstract void onCharacteristicChanged(BluetoothDevice device,
			UUID serviceUuid, UUID characteristicUuid,
			byte[] characteristicValue);
	
	public abstract void onAsyncStatus( BluetoothDevice device, String cmd, String value );
	

	public final void onCharacteristicReadInternal(
			final BluetoothDevice device, final UUID serviceUuid,
			final UUID characteristicUuid, final byte[] characteristicValue,
			final int status) {
		if (null != mListenerHandler) {
			mListenerHandler.post(new Runnable() {

				@Override
				public void run() {
					onCharacteristicRead(device, serviceUuid,
							characteristicUuid, characteristicValue, status);
				}
			});
		} else {
			onCharacteristicRead(device, serviceUuid, characteristicUuid,
					characteristicValue, status);
		}
	}

	public final void onCharacteristicWriteInternal(
			final BluetoothDevice device, final UUID serviceUuid,
			final UUID characteristicUuid, final byte[] characteristicValue,
			final int status) {
		if (null != mListenerHandler) {
			mListenerHandler.post(new Runnable() {

				@Override
				public void run() {
					onCharacteristicWrite(device, serviceUuid,
							characteristicUuid, characteristicValue, status);
				}
			});
		} else {
			
			onCharacteristicWrite(device, serviceUuid, characteristicUuid,
					characteristicValue, status);
		}
	}

	public final void onCharacteristicChangedInternal(
			final BluetoothDevice device, final UUID serviceUuid,
			final UUID characteristicUuid, final byte[] characteristicValue) {
		if (null != mListenerHandler) {
			
			CKLOG.Debug(TAG + " (null != mListenerHandler)", new String(characteristicValue));

			
			mListenerHandler.post(new Runnable() {

				@Override
				public void run() {
					onCharacteristicChanged(device, serviceUuid,
							characteristicUuid, characteristicValue);					
				}
			});
		} else {
			onCharacteristicChanged(device, serviceUuid, characteristicUuid,
					characteristicValue);
			
			CKLOG.Error(TAG, new String(characteristicValue) );
		}
	}

}
