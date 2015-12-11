/**
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 *
 * @(#) FileChooserActivity.java
 */
package com.borg.androidemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.borg.androidemo.R;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author jinzhaoyu
 */
public class FileChooserActivity extends Activity 
				implements OnClickListener,OnItemClickListener{

	/**
	 * 
	 */
	public static final String SELECTED_FILES = "SELECTED_FILES";

	/**
	 * @param activity
	 * @param requestCode
	 */
	public static void launchForResult(Activity activity,int requestCode){
		Intent intent =new Intent(activity,FileChooserActivity.class);
		activity.startActivityForResult(intent,requestCode);
	}
	
	private ListView mListView;
	private MListAdapter mListAdapter;
	private Button mBtnOK;
	private Button mBtnCancel;
	
	private LoadTask mLoadTask;
	private File mCurrentFolder;
	private List<File> mFiles = new ArrayList<File>();
	private LinkedList<File> mFileBackstack = new LinkedList<File>();
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_file_chooser);
		mListView = (ListView)findViewById(R.id.lv_files);
		mListView.setOnItemClickListener(this);
		mListAdapter = new MListAdapter(this,mFiles);
		mListView.setAdapter(mListAdapter);
		mListView.setEmptyView(findViewById(R.id.lyt_empty_view));
		
		mBtnOK = (Button)findViewById(R.id.btn_ok);
		mBtnOK.setOnClickListener(this);
		mBtnCancel = (Button)findViewById(R.id.btn_cancel);
		mBtnCancel.setOnClickListener(this);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		loadFiles(mCurrentFolder);
	}

	private File getExtRootDir(){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			return Environment.getExternalStorageDirectory();
		}
		return Environment.getRootDirectory();
	}
	
	private void loadFiles(File folder){
		if(folder == null){
			folder = getExtRootDir();
		}
		mCurrentFolder = folder;

		finishTask();
		
		mLoadTask = new LoadTask(mCurrentFolder);
		mLoadTask.execute();
		
	}
	
	private void finishTask(){
		if(mLoadTask != null && mLoadTask.getStatus() != Status.FINISHED){
			mLoadTask.cancel(true);
			mLoadTask = null;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		File file = mListAdapter.getItem(position);
		if(file.isDirectory()){
			mFileBackstack.add(mCurrentFolder);
			loadFiles(file);
		}else{
			mListAdapter.toggleSelectFile(position);
			mListAdapter.notifyDataSetChanged();
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_ok:
			if(mListAdapter.getSelectedFiles().isEmpty()){
				Toast.makeText(this, "请选择文件", Toast.LENGTH_SHORT).show();
				return;
			}
			
			Set<File> fs = mListAdapter.getSelectedFiles();
			ArrayList<String> selFiles= new ArrayList<String>();
			for(File f : fs ){
				selFiles.add(f.getAbsolutePath());
			}
			Intent intent = new Intent();
			intent.putStringArrayListExtra(SELECTED_FILES, selFiles);
			setResult(RESULT_OK,intent);
			finish();
			break;
		case R.id.btn_cancel:
			finish();
			break;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if(! mFileBackstack.isEmpty()){
			File lastFolder = mFileBackstack.removeLast();
			loadFiles(lastFolder);
			return;
		}
		super.onBackPressed();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	
	//=========================Inner classes========================
	
	/**
	 * 
	 * @author jinzhaoyu
	 */
	private class LoadTask extends AsyncTask<Void, Void,List<File>>{
		private File folder;
		private LoadTask(File folder){
			this.folder = folder;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected List<File> doInBackground(Void... params) {
			List<File> list = new ArrayList<File>();
			File[] rs = folder.listFiles();
			if(rs != null && rs.length > 0){
				Collections.addAll(list, rs);
			}
			Collections.sort(list,new FileCompartor());
			return list;
		}


		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(List<File> result) {
			setProgressBarIndeterminateVisibility(false);
			mFiles.clear();
			if(result != null){
				mFiles.addAll(result);
			}
			mListAdapter.notifyDataSetChanged();
		}
		
		
		/**
		 * 
		 * @author jinzhaoyu
		 */
		private class FileCompartor implements Comparator<File>{

			/* (non-Javadoc)
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(File lhs, File rhs) {
				if(lhs == null) return -1;
				if(rhs == null) return 1;
				if(lhs.isDirectory() && ! rhs.isDirectory()){
					return -1;
				}
				if(! lhs.isDirectory() &&rhs.isDirectory()){
					return 1;
				}
				return lhs.getName().compareToIgnoreCase(rhs.getName());
			}
			
		}
	}
	
	/**
	 * 
	 * @author jinzhaoyu
	 */
	private class MListAdapter extends ArrayAdapter<File>{
		private LayoutInflater inflater;
		private Set<File> selectedFiles = new HashSet<File>();
		/**
		 * @param context
		 * @param objects
		 */
		public MListAdapter(Context context, List<File> objects) {
			super(context, 0, objects);
			inflater = LayoutInflater.from(context);
		}

		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#clear()
		 */
		@Override
		public void clear() {
			super.clear();
			selectedFiles.clear();
		}
		
		public void toggleSelectFile(int position){
			File file = getItem(position);
			if(selectedFiles.contains(file)){
				selectedFiles.remove(file);
			}else
				selectedFiles.add(file);
		}
		
		public Set<File> getSelectedFiles(){
			return selectedFiles;
		}
		
		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(convertView == null){
				convertView = inflater.inflate(R.layout.listitem_file_list, parent,false);
				viewHolder = new ViewHolder();
				viewHolder.icon = (ImageView)convertView.findViewById(R.id.iv_icon);
				viewHolder.name = (TextView)convertView.findViewById(R.id.tv_name);
				viewHolder.size = (TextView)convertView.findViewById(R.id.tv_size);
				viewHolder.checkbox = (CheckBox)convertView.findViewById(R.id.cb_select);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder)convertView.getTag();
			}
			
			File file = getItem(position);
			viewHolder.name.setText(file.getName());
			if(file.isDirectory()){
				viewHolder.icon.setImageResource(R.drawable.ic_folder);
				viewHolder.size.setVisibility(View.GONE);
				viewHolder.checkbox.setVisibility(View.GONE);
			}else{
				viewHolder.icon.setImageResource(R.drawable.ic_file_default);
				viewHolder.size.setVisibility(View.VISIBLE);
				viewHolder.size.setText(formateStorage(file.length(), 2));
				viewHolder.checkbox.setVisibility(View.VISIBLE);
				viewHolder.checkbox.setChecked(selectedFiles.contains(file));
			}
			return convertView;
		}
		
		private class ViewHolder {
			ImageView icon;
			TextView name;
			TextView size;
			CheckBox checkbox;
		}
	}
	
	public static final Long STORAGE_K = 1024l;
	public static final Long STORAGE_M = 1024l * 1024l;
	public static final Long STORAGE_G = 1024l * 1024l * 1024l;
	
	/**
	 * 获取容量大小的表示方法 <br/>
	 * 1. 如果数值为null或者空字符串，或者非数字类型，返回空字符串<br/>
	 * 2. 小于1K 返回1K <br/>
	 * 3. 小于1M 返回XXK </br> 4. 小于1G 返回xxM </br> 5. 其他返回xxG </br>
	 * 
	 * @param storage
	 *            storage unit:byte
	 * @param c
	 *            小数位数
	 * @return String 容量大小的表示方法
	 */
	public static String formateStorage(Long storage, int c) {
		if (storage == null) {
			return "";
		}

		String p = "#";

		if (c > 0 && c <= 8) {
			p += ".";
			for (int i = 0; i < c; i++) {
				p += "#";
			}
		}
		DecimalFormat df = new DecimalFormat(p);

		BigDecimal bigStorage = BigDecimal.valueOf(storage);
		BigDecimal unit;
		BigDecimal divideRs;

		if (storage == 0) {
			return "0B";
		} else if (storage < STORAGE_K) {
			return storage + "B";
		} else if (storage < STORAGE_M) {
			unit = BigDecimal.valueOf(STORAGE_K);
			divideRs = bigStorage.divide(unit, c, BigDecimal.ROUND_HALF_UP);
			return df.format(divideRs.doubleValue()) + "K";
		} else if (storage < STORAGE_G) {
			unit = BigDecimal.valueOf(STORAGE_M);
			divideRs = bigStorage.divide(unit, c, BigDecimal.ROUND_HALF_UP);
			return df.format(divideRs.doubleValue()) + "M";
		} else {
			unit = BigDecimal.valueOf(STORAGE_G);
			divideRs = bigStorage.divide(unit, c, BigDecimal.ROUND_HALF_UP);
			return df.format(divideRs.doubleValue()) + "G";
		}
	}

}
