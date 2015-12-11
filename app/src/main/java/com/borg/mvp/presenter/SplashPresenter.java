package com.borg.mvp.presenter;

import android.content.Context;

import com.borg.mvp.model.INetConnect;
import com.borg.mvp.model.impl.NetConnect;
import com.borg.mvp.view.ISplashView;


public class SplashPresenter {
	private INetConnect connect;
	private ISplashView iView;
	
	public SplashPresenter(ISplashView iView){
		this.iView = iView;
		connect = new NetConnect();
	}
	
	public void didFinishLoading(Context context){
		iView.showProcessBar();
		if(connect.isNetConnect(context)){
			iView.startNextActivity();
		}else{
			iView.showNetError();
		}
		iView.hideProcessBar();
	}
}
