package com.borg.androidemo.devices.api.callback;


import com.borg.androidemo.devices.protocol.ResponseCode;

/**
 * @author yiping.cyp
 * @ClassName: SimpleCallback
 * @Version 1.0.0
 * @date 2015-8-16 下午7:28:15
 * @description
 */
public interface SimpleCallback extends ResponseCode {
	public void onSuccess(String data);

	public void onFail(String msg);
}
