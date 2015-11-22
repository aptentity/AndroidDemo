package com.borg.androidemo.devices.connection.bluetooth.ble.listener;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;

public abstract class BLENotificationListener extends Listener {

	public BLENotificationListener(final Handler listenerHandler) {
		super(listenerHandler);
	}

	public abstract void onNotificationSendCompleted(String address,
			String notification);

	public abstract void onNotificationSendFailed(String address,
			String notification, int failCode);

	public abstract void onReceiveNotification(BluetoothDevice device,
			byte[] content);

	public final void onReceiveNotificationInternal(
			final BluetoothDevice device, final byte[] content) {
		if (null != mListenerHandler) {
			mListenerHandler.post(new Runnable() {

				@Override
				public void run() {
					onReceiveNotification(device, content);
				}
			});
		} else {
			onReceiveNotification(device, content);
		}
	}

	public final void onNotificationSendCompletedInternal(
			final String address, final String notification) {
		if (null != mListenerHandler) {
			mListenerHandler.post(new Runnable() {

				@Override
				public void run() {
					onNotificationSendCompleted(address, notification);
				}
			});
		} else {
			onNotificationSendCompleted(address, notification);
		}
	}

	public final void onNotificationSendFailedInternal(
			final String adress, final String notification,
			final int failCode) {
		if (null != mListenerHandler) {
			mListenerHandler.post(new Runnable() {

				@Override
				public void run() {
					onNotificationSendFailed(adress, notification, failCode);
				}
			});
		} else {
			onNotificationSendFailed(adress, notification, failCode);
		}
	}

}
