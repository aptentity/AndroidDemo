package com.borg.mvp.view.recyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.borg.androidemo.R;
import com.borg.mvp.view.recyclerview.HomeAdapter.OnItemClickLitener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends ActionBarActivity
{

	private LoadMoreRecyclerView mRecyclerView;
	private List<String> mDatas;
	private HomeAdapter mAdapter;
	private SwipeRefreshLayout swipeRefreshLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_recyclerview);

		initData();

		RecyclerViewHeader header = (RecyclerViewHeader) findViewById(R.id.header);

		mRecyclerView = (LoadMoreRecyclerView) findViewById(R.id.id_recyclerview);
		mAdapter = new HomeAdapter(this, mDatas);

		mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4,
				StaggeredGridLayoutManager.VERTICAL));

		header.attachTo(mRecyclerView, true);

		mRecyclerView.setAdapter(mAdapter);

		mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));
		// 设置item动画
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setAutoLoadMoreEnable(true);
		mRecyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
			@Override
			public void onLoadMore() {
				mRecyclerView.postDelayed(new Runnable() {
					@Override
					public void run() {
						swipeRefreshLayout.setRefreshing(false);
						mRecyclerView.notifyMoreFinish(false);
					}
				}, 1000);
			}
		});

		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				swipeRefreshLayout.setRefreshing(false);
			}
		});
		initEvent();

	}

	private void initEvent()
	{
		mAdapter.setOnItemClickLitener(new OnItemClickLitener()
		{
			@Override
			public void onItemClick(View view, int position)
			{
				Toast.makeText(HomeActivity.this, position + " click",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onItemLongClick(View view, int position)
			{
				Toast.makeText(HomeActivity.this, position + " long click",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	protected void initData()
	{
		mDatas = new ArrayList<String>();
		for (int i = 'A'; i < 'z'; i++)
		{
			mDatas.add("" + (char) i);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.id_action_add:
			mAdapter.addData(1);
			break;
		case R.id.id_action_delete:
			mAdapter.removeData(1);
			break;
		case R.id.id_action_gridview:
			mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
			break;
		case R.id.id_action_listview:
			mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
			break;
		case R.id.id_action_horizontalGridView:
			mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4,
					StaggeredGridLayoutManager.HORIZONTAL));
			break;

		case R.id.id_action_staggeredgridview:
			Intent intent = new Intent(this , StaggeredGridLayoutActivity.class);
			startActivity(intent);
			break;
		}
		return true;
	}

}
