package com.borg.androidemo.devices.api.callback;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yiping.cyp
 * @ClassName: SendDataCallbackMap
 * @Version 版本
 * @date 2015-8-16 下午7:27:00
 * @description 蓝牙请求回调的存储集合
 */
class SendDataCallbackMap extends ConcurrentHashMap<Long, SendDataCallback> {
	/** serialVersionUID */
	private static final long serialVersionUID = -706996948572800129L;
	private static final Object mapLock = new Object();
	private static final String TAG = SendDataCallbackMap.class.getSimpleName();
	private static SendDataCallbackMap mSendDataCallbackMap = new SendDataCallbackMap();

	private SendDataCallbackMap() {
		super();
	}

	public static SendDataCallbackMap instance() {
		synchronized (mapLock) {
			return mSendDataCallbackMap;
		}
	}

	boolean isCallbackValid(Object seqId) {
		// 查看对应catigory的callback，是否存在，是否在独享时间段内,是否能够让这个callback生效
		// return contains(catigory);
		// return containKey(String.valueOf(catigory));

		synchronized (mapLock) {
			// travel();
			boolean containsKey = containsKey(seqId);
			// Log.e(TAG, "containsKey(" + seqId + ")=" + containsKey);
			return containsKey;
		}

	}

	// @Override
	// public boolean remove(Object key, Object value) {
	//
	// boolean callbackValid = isCallbackValid(key);
	// if (callbackValid) {
	// //Log.d(TAG, "before remove " + key + " , map do not contains " + key);
	// } else {
	// //Log.d(TAG, "before remove " + key + " , map contains " + key
	// + " !!!");
	// }
	//
	// boolean remove = super.remove(key, value);
	// if (remove) {
	// //Log.e(TAG, String.valueOf(key) + "is removed success!!!");
	// } else {
	// //Log.e(TAG, String.valueOf(key) + "is removed failed!!!");
	// }
	// callbackValid = isCallbackValid(key);
	// if (callbackValid) {
	// //Log.d(TAG, "after remove " + key + " , map do not contains " + key);
	// } else {
	// //Log.d(TAG, "after remove " + key + " , map contains " + key
	// + " !!!");
	// }
	//
	// return remove;
	// }
	@Override
	public SendDataCallback put(Long key, SendDataCallback cb) {
		synchronized (mapLock) {
			// Log.i(TAG, "==========The SendDataCallbackMap " + ",key=" + key
			// + " , do the put operation...===================");

			if (containsKey(key)) {
				// Log.e(TAG, "already has the category:" + key + ",return ");
				return null;
			}

			boolean callbackValid = isCallbackValid(key);
			if (!callbackValid) {
				// Log.d(TAG, "before put " + key + " , map do not contains "
				// + key);
			} else {
				// Log.d(TAG, "before put " + key + " , map contains " + key
				// + " !!!");
			}

			super.put(key, cb);
			callbackValid = isCallbackValid(cb.getSeqId());
			if (!callbackValid) {
				// Log.d(TAG, "after put " + key + " , map do not contains " +
				// key);
			} else {
				// Log.d(TAG, "after put " + key + " , map contains " + key);
			}

			// Log.i(TAG, "==========put operation " + ",key=" + key
			// + " finished...===================");
			return cb;
		}
	}


	@Override
	public SendDataCallback remove(Object key) {
		synchronized (mapLock) {
			// boolean callbackValid = isCallbackValid(key);
			// //Log.e(TAG, "isCallbackValid(" + String.valueOf(key) + ")="
			// + callbackValid);
			// if (callbackValid) {
			// return super.remove(key);
			// } else {
			// return null;
			// }

			// Log.d(TAG, "==================begin remove" + key
			// + "======================");

			boolean callbackValid = isCallbackValid(key);
			if (!callbackValid) {
				// Log.d(TAG, "before remove " + key + " , map do not contains "
				// + key);
			} else {
				// Log.d(TAG, "before remove " + key + " , map contains " + key
				// + " !!!");
			}

			// Log.d(TAG, "map doing the remove operation...");
			SendDataCallback cb = super.remove(key);
			boolean removed = cb == null ? false : true;
			if (removed) {
				// Log.e(TAG, "map do the remove " + key
				// + " operation ,find the cb is not null:" + cb);
			} else {
				// Log.e(TAG, "map do the remove " + key
				// + " operation ,find the cb is null:" + cb);
			}
			// if (remove) {
			// //Log.e(TAG, String.valueOf(key) + "is removed success!!!");
			// } else {
			// //Log.e(TAG, String.valueOf(key) + "is removed failed!!!");
			// }
			callbackValid = isCallbackValid(key);
			if (!callbackValid) {
				// Log.d(TAG, "after remove " + key + " , map do not contains "
				// + key);
			} else {
				// Log.d(TAG, "after remove " + key + " , map contains " + key
				// + " !!!");
			}

			// return remove;

			// Log.d(TAG, "==================remove" + key
			// + " finished ======================");
			return cb;
		}
	}

	// public boolean containKey(String key1) {
	// Iterator keys = this.keySet().iterator();
	// while (keys.hasNext()) {
	// String key = (String) keys.next();
	// if ("key2".equalsIgnoreCase(key)) {
	// // System.out.println("这里面有key2");
	// return true;
	// }
	// }
	// return false;
	// }

	// private void travel() {
	// synchronized (mapLock) {
	// for (Long seqId : this.keySet()) {
	// // System.out.println("key= " + entry.getKey() + " and value= "
	// // +
	// // entry.getValue());
	// //Log.d(TAG, "seqId=" + seqId + ",callback:" + get(seqId)
	// // + ",seqId of callback = " + get(seqId).getSeqId()
	// // + ",category of callback:" + get(seqId).getCatigory());
	// }
	// }
	// }
}
