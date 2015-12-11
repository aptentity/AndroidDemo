package com.borg.mvp.model.impl;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.borg.mvp.model.INetConnect;

public class NetConnect implements INetConnect {

	@Override
	public boolean isNetConnect(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

}
