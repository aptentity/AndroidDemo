package com.borg.androidemo.devices.api.callback;

import com.borg.androidemo.devices.protocol.ResponseCode;


public interface BindingListener extends ResponseCode {
	/** 解绑设备，发现设备没有被绑定过 */
	public static final int FAIL_UNBIND_DEV_BT_NOT_BINDED = 24;
	/** 其他原因导致解绑设备失败 */
	public static final int FAIL_UNBIND_OTHER = 28;
	/** 解绑设备失败 */
	public static final int FAIL_UNBIND_DEV_BT = 29;
	/** 绑定设备上云失败 */
	public static final int FAIL_BIND_DEV_TO_CLOUD = 32;
	/** 通过BT绑定设备失败 */
	public static final int FAIL_BIND_BT_DEV = 33;
	/** 绑定用户上云失败 */
	public static final int FAIL_BIND_USER_TO_CLOUD = 34;
	
	/** 绑定用户时发现用户的登陆态已经过期，需要重新登陆 */
	public static final int FAIL_BIND_LOGIN_EXPIRED = 35;
	/** 通过CMNS发送绑定信息到设备失败 */
	public static final int FAIL_BIND_CMNS = 36;
	/** 解绑云端用户失败 */
	public static final int FAIL_UNBIND_USER_TO_CLOUD = 37;

	public void onSuccess(int responseCode);

	public void onFail(int responseCode);

}
