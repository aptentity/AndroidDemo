package com.borg.androidemo.devices.connection.bluetooth.ble.listener;

import android.os.Handler;

public abstract class StatusListener extends Listener {

	public StatusListener(final Handler listenerHandler) {
		super(listenerHandler);
	}

	public static final int INIT_ERROR_COMMON = 0;
	public static final int INIT_ERROR_BIND_REMOTE_SERVICE_FAIL = 1;
	public static final int INIT_ERROR_CONTEXT_NULL = 2;

	public abstract void onInitSuccessful();

	public abstract void onInitFailed(final int errorCode);

	public final void onInitSuccessfulInternal() {
		if (null != mListenerHandler) {
			mListenerHandler.post(new Runnable() {

				@Override
				public void run() {
					onInitSuccessful();
				}
			});
		} else {
			onInitSuccessful();
		}
	}

	public final void onInitFailedInternal(final int errorCode) {
		if (null != mListenerHandler) {
			mListenerHandler.post(new Runnable() {

				@Override
				public void run() {
					onInitFailed(errorCode);
				}
			});
		} else {
			onInitFailed(errorCode);
		}
	}
}
