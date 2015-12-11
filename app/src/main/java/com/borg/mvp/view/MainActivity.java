package com.borg.mvp.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.borg.androidemo.R;
import com.borg.mvp.presenter.SplashPresenter;


public class MainActivity extends Activity implements ISplashView {

	SplashPresenter presenter;
	private ProgressDialog progressBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mvp);
		
		presenter = new SplashPresenter(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		presenter.didFinishLoading(this);
	}
	
	@Override
	public void showProcessBar() {
		if (progressBar == null) {
			progressBar = new ProgressDialog(this);
			progressBar.setCancelable(true);
			progressBar.setCanceledOnTouchOutside(true);
			progressBar.setMessage("更新数据中，请稍后");
		}
		progressBar.show();
	}

	@Override
	public void hideProcessBar() {
		progressBar.hide();
	}

	@Override
	public void showNetError() {
		Toast.makeText(this, "暂无网络", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void startNextActivity() {
		Toast.makeText(this, "跳到下个activity", Toast.LENGTH_SHORT).show();
	}

}
