package com.borg.mvp.view.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.borg.androidemo.R;

import java.util.List;

public class MyListAdapter extends BaseAdapter {

	private Activity activity;
	private List<ListItem> list;

	public MyListAdapter(Activity activity) {
		this.activity = activity;
	}

	public void setList(List<ListItem> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		if (list != null) {
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (list != null && position < list.size()) {
			return list.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		if (list != null && position < list.size()) {
			return list.get(position).getType();
		}
		return super.getItemViewType(position);
	}

	@Override
	public int getViewTypeCount() {
		return ListItem.TYPE_COUNT;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		switch (type) {
		case ListItem.TYPE_TEXT: {
			TextViewHolder holder = null;
			if (convertView == null) {
				convertView = activity.getLayoutInflater().inflate(R.layout.list_test_item, null);
				holder = new TextViewHolder();
				holder.textView = (TextView) convertView.findViewById(R.id.tv_text);
				convertView.setTag(holder);
			} else {
				holder = (TextViewHolder) convertView.getTag();
			}
			holder.textView.setText(list.get(position).getName());
			break;
		}
		case ListItem.TYPE_EDIT: {
			EditViewHolder holder = null;
			if (convertView == null) {
				convertView = activity.getLayoutInflater().inflate(R.layout.list_edittext_item, null);
				holder = new EditViewHolder();
				holder.editText = (EditText) convertView.findViewById(R.id.ed_edittext);
				convertView.setTag(holder);
			} else {
				holder = (EditViewHolder) convertView.getTag();
			}
			holder.editText.setText(list.get(position).getName());
			break;
		}
		case ListItem.TYPE_BUTTON: {
			ButtonViewHolder holder = null;
			if (convertView == null) {
				convertView = activity.getLayoutInflater().inflate(R.layout.list_button_item, null);
				holder = new ButtonViewHolder();
				holder.button = (Button) convertView.findViewById(R.id.btn_button);
				convertView.setTag(holder);
			} else {
				holder = (ButtonViewHolder) convertView.getTag();
			}
			holder.button.setText(list.get(position).getName());
			break;
		}
		default:
			break;
		}

		return convertView;
	}

	static class TextViewHolder {
		TextView textView;
	}

	static class EditViewHolder {
		EditText editText;
	}

	static class ButtonViewHolder {
		Button button;
	}

}
