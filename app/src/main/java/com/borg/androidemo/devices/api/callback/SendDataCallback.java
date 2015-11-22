package com.borg.androidemo.devices.api.callback;

import com.borg.androidemo.common.utils.CKLOG;
import com.borg.androidemo.devices.protocol.ResponseCode;

/**
 * 手机到手表请求数据然后数据回传的回调，注意，目前每个请求都必须要new一个新都callback，否则会出错
 * 
 * @author jinyi
 * 
 */
public abstract class SendDataCallback implements ResponseCode {
	private static final String TAG = SendDataCallback.class.getSimpleName();
	private static int seqId_seed = 1;
	protected int catigory;
	protected int latencyTime = 15;
	private long seqId;

	public long getSeqId() {
		return seqId;
	}

	/**
	 * 构造 默认latency延迟时间为15秒
	 * 
	 * @param catigory
	 */
	public SendDataCallback(int catigory) {
		this.catigory = catigory;
		setSeqId();
	}

	/**
	 * 构造 手动设置latency延迟时间
	 * 
	 * @param catigory
	 * @param latencyTime
	 */
	public SendDataCallback(int catigory, int latencyTime) {
		// this.catigory = catigory;
		this(catigory);
		if (latencyTime > 0) {
			this.latencyTime = latencyTime;
		}
	}

	private void setSeqId() {
		++seqId_seed;
		if (seqId_seed > 60000) {
			seqId_seed = 1;
		}
		seqId = seqId_seed;
	}

	public int getCatigory() {
		return catigory;
	}

	public void setCatigory(int catigory) {
		this.catigory = catigory;
	}

	public int getLatencyTime() {
		return latencyTime;
	}

	public void setLatencyTime(int latencyTime) {
		this.latencyTime = latencyTime;
	}

	public abstract void onSuccess(String data);

	public abstract void onFail(int responseCode);

	public void failAndRemove(int failCode) {
		onFail(failCode);
		if (seqId >= 0) {
			AsyncDataTask.instance().remove(seqId);
		} else {
			CKLOG.Error(TAG, "seqId < 0 , remove operation interrupt");
		}
	}
}
