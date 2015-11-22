package com.borg.androidemo.devices.connection.bluetooth.ble.uuid;

import android.text.TextUtils;
import com.borg.androidemo.common.utils.CKLOG;

import java.util.UUID;


public class AliBLEUuidUtils {

	private static final String TAG = "UuidUtils";

	public static boolean isValidUuidString(final String uuidString) {
		if (TextUtils.isEmpty(uuidString)) {
			CKLOG.Info(TAG, "isValidUuidString() : not valid, uuidString is empty.");
			return false;
		}
		UUID uuid = null;
		try {
			uuid = UUID.fromString(uuidString);
		} catch (Exception e) {
			CKLOG.Info(TAG,
					"isValidUuidString() : not valid, meet exception when create UUID from uuidString.");
			CKLOG.Error(TAG, "isValidUuidString() : exception - " + e.getMessage());
			return false;
		}
		if (null == uuid) {
			CKLOG.Info(TAG,
					"isValidUuidString() : not valid, UUID from uuidString is null.");
			return false;
		}
		if (TextUtils.isEmpty(uuid.toString())) {
			CKLOG.Info(TAG,
					"isValidUuidString() : not valid, UUID from uuidString is empty.");
			return false;
		}
		CKLOG.Info(TAG, "isValidUuidString() : valid.");
		return true;
	}

	public static boolean isNotificationFeedbackCharacteristicUuid(final String uuidString) {
		if (TextUtils.isEmpty(uuidString)) {
			return false;
		}
		return AliBLEUuid.NOTIFICATION_FEEDBACK_CHARACTERISTIC
				.equals(uuidString);
	}
}
