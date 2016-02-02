package com.borg.mvp.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.borg.androidemo.R;
import com.borg.mvp.presenter.SplashPresenter;
import com.borg.mvp.utils.LogHelper;
import com.borg.mvp.utils.QRCodeUtils;
import com.borg.mvp.utils.ToastUtil;
import com.borg.mvp.view.widget.GenderSelectDlg;
import com.borg.mvp.view.widget.WaitDlg;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends Activity implements ISplashView,View.OnClickListener {
	private final String TAG = MainActivity.class.getSimpleName();
	SplashPresenter presenter;
	private ProgressDialog progressBar;
	private WaitDlg waitDlg;
	private ImageView imageview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mvp);
		
		presenter = new SplashPresenter(this);
		findViewById(R.id.btn_select_date).setOnClickListener(this);
		findViewById(R.id.btn_select_gender).setOnClickListener(this);

		imageview = (ImageView)findViewById(R.id.imageView);

		File file = new File(this.getApplicationContext().getFilesDir(), "qr.jpg");
		QRCodeUtils.createQRImage("hahaha", 300, 300, null, file.getPath());
		Bitmap bMap = BitmapFactory.decodeFile(file.getPath());
		imageview.setImageBitmap(bMap);
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
//		waitDlg = new WaitDlg(this);
//		waitDlg.show("haha");
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
				final int iyear = dateAndTime.get(Calendar.YEAR);
				final int imonth = dateAndTime.get(Calendar.MONTH);
				final int iday = dateAndTime.get(Calendar.DAY_OF_MONTH);
				DatePickerDialog dlg = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						ToastUtil.showShort(Integer.toString(year)+"/"+Integer.toString(monthOfYear+1)+"/"+Integer.toString(dayOfMonth));
						LogHelper.d(TAG, "onDateSet:"+Integer.toString(year) + "/" + Integer.toString(monthOfYear + 1) + "/" + Integer.toString(dayOfMonth));
					}
				},iyear,imonth,iday);
				//控制显示范围
				final DatePicker picker = dlg.getDatePicker();
				picker.init(iyear, imonth, iday, new DatePicker.OnDateChangedListener() {
					@Override
					public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						LogHelper.d(TAG,"onDateChanged:"+Integer.toString(year)+"/"+Integer.toString(monthOfYear+1)+"/"+Integer.toString(dayOfMonth));
						LogHelper.d(TAG,"onDateChanged today:"+Integer.toString(iyear)+"/"+Integer.toString(imonth+1)+"/"+Integer.toString(iday));

						if (year*10000+(monthOfYear+1)*100+dayOfMonth>iyear*10000+(imonth+1)*100+iday){
							picker.updateDate(iyear,imonth,iday);
						}
					}
				});
				dlg.show();
			break;
			case R.id.btn_select_gender:
				GenderSelectDlg dlg1 = new GenderSelectDlg(MainActivity.this);
				dlg1.show();
				break;
		}
	}



}
