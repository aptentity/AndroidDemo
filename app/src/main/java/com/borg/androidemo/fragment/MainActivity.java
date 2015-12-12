package com.borg.androidemo.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.borg.androidemo.R;

public class MainActivity extends FragmentActivity implements
		BackHandledInterface {
	private static MainActivity mInstance;
	private BackHandledFragment mBackHandedFragment;
	private Button btnSecond;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment_main);
		btnSecond = (Button) findViewById(R.id.btnSecond);

		// btnSecond.setVisibility(View.VISIBLE);
		btnSecond.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FirstFragment first = new FirstFragment();
				loadFragment(first);
				// btnSecond.setVisibility(View.GONE);
			}
		});

	}

	public static MainActivity getInstance() {
		if (mInstance == null) {
			mInstance = new MainActivity();
		}
		return mInstance;
	}

	public void loadFragment(BackHandledFragment fragment) {
		BackHandledFragment second = fragment;
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.firstFragment, second, "other");
		ft.addToBackStack("tag");
		ft.commit();
	}

	@Override
	public void setSelectedFragment(BackHandledFragment selectedFragment) {
		this.mBackHandedFragment = selectedFragment;
	}

	@Override
	public void onBackPressed() {
		if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
			if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
				super.onBackPressed();
			} else {
				if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
					btnSecond.setVisibility(View.VISIBLE);
				}
				getSupportFragmentManager().popBackStack();
			}
		}
	}
}
