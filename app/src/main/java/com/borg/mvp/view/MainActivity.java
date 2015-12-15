package com.borg.mvp.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.borg.androidemo.R;
import com.borg.mvp.presenter.SplashPresenter;
import com.borg.mvp.utils.ToastUtil;
import com.borg.mvp.view.widget.GenderSelectDlg;

import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends Activity implements ISplashView,View.OnClickListener {

	SplashPresenter presenter;
	private ProgressDialog progressBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mvp);
		
		presenter = new SplashPresenter(this);
		findViewById(R.id.btn_select_date).setOnClickListener(this);
		findViewById(R.id.btn_select_gender).setOnClickListener(this);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btn_select_date:
				Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
				int year = dateAndTime.get(Calendar.YEAR);
				int month = dateAndTime.get(Calendar.MONTH);
				int day = dateAndTime.get(Calendar.DAY_OF_MONTH);
				DatePickerDialog dlg = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						ToastUtil.showShort(Integer.toString(year)+"/"+Integer.toString(monthOfYear+1)+"/"+Integer.toString(dayOfMonth));
					}
				},year,month,day);
				dlg.show();
			break;
			case R.id.btn_select_gender:
				GenderSelectDlg dlg1 = new GenderSelectDlg(MainActivity.this);
				dlg1.show();
				break;
		}
	}
}
