package com.borg.androidemo.devices.api.callback;


import com.borg.androidemo.devices.protocol.ResponseCode;

public interface AuthCallback extends ResponseCode {
	/** 认证设备，发现已经被成功绑定过 */
	public static final int AUTH_ALREADY_BINDED_SUCCESS = 9001;
	/** 认证设备，发现已经被成功绑定过，但是却没有在手表中拿到id，需要重新进行绑定 */
	public static final int AUTH_ALREADY_BINDED_BUT_FAILED_WATCH_ID = 9002;
	/** 认证设备，发现还没有被绑定过 */
	public static final int AUTH_UNBINDED = 9005;
	/** 认证设备，发现已经被别的设备绑定过 */
	public static final int AUTH_FAILED_ALREADING_BINDED_BY_OTHER = 9009;

	void onAuthResult(int responseCode);
}
