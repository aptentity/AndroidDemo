package com.borg.mvp.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.borg.androidemo.R;
import com.borg.mvp.view.widget.SlideCutListView;
import com.borg.mvp.view.widget.SlideCutListView.RemoveListener;

public class SlideCutListActivity extends Activity implements RemoveListener{
	private SlideCutListView slideCutListView ;
	private ArrayAdapter<String> adapter;
	private List<String> dataSourceList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slide_cut_list_activity);
		init();
	}

	private void init() {
		slideCutListView = (SlideCutListView) findViewById(R.id.slideCutListView);
		slideCutListView.setRemoveListener(this);
		
		for(int i=0; i<20; i++){
			dataSourceList.add("test" + i); 
		}
		
		adapter = new ArrayAdapter<String>(this, R.layout.listview_item, R.id.list_item, dataSourceList);
		slideCutListView.setAdapter(adapter);
		
		slideCutListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(SlideCutListActivity.this, dataSourceList.get(position), Toast.LENGTH_SHORT).show();
			}
		});
	}


	//滑动删除之后的回调方法
	@Override
	public void removeItem(SlideCutListView.RemoveDirection direction, int position) {
		adapter.remove(adapter.getItem(position));

		switch (direction) {
			case RIGHT:
				Toast.makeText(this, "向右删除  "+ position, Toast.LENGTH_SHORT).show();
				break;
			case LEFT:
				Toast.makeText(this, "向左删除  "+ position, Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
		}

	}


}
