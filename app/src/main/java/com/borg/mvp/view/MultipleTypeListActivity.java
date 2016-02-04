package com.borg.mvp.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.borg.androidemo.R;
import com.borg.mvp.view.adapter.ListItem;
import com.borg.mvp.view.adapter.MyListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 多种类型list item
 */
public class MultipleTypeListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ListView listView = (ListView) findViewById(R.id.list_main);
		MyListAdapter listAdapter = new MyListAdapter(this);
		listView.setAdapter(listAdapter);

		List<ListItem> list = new ArrayList<ListItem>();

		for (int i = 0; i < 30; i++) {
			ListItem item = new ListItem(i % 3, "index:" + i + " type:" + i % 3);
			list.add(item);
		}
		listAdapter.setList(list);
		listAdapter.notifyDataSetChanged();
	}
}
