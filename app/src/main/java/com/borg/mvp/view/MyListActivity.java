package com.borg.mvp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.borg.androidemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyListActivity extends Activity {
    private ArrayList<HashMap<String,Object>> data=new ArrayList<HashMap<String, Object>>();
    private ListView GroupManList;
    private BaseAdapter baseAdapter;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main3);
        initView();
        for (int i=0;i<20;i++){
            HashMap<String,Object> itemData=new HashMap<String, Object>();
            itemData.put("ManName","test"+i);
            itemData.put("LastTime","abc");
            data.add(itemData);
        }

        baseAdapter=new BaseAdapter() {
            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public Object getItem(int position) {
                return data.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView==null){
                    LayoutInflater layoutInflater=getLayoutInflater();
                    convertView=layoutInflater.inflate(R.layout.manager_group_list_item_parent,parent,false);
                }
                Map<String,Object> itemData=(Map<String,Object>)getItem(position);
                ImageView ManImg=(ImageView)convertView.findViewById(R.id.ManImg);
                TextView ManName=(TextView)convertView.findViewById(R.id.ManName);
                TextView LastTime=(TextView)convertView.findViewById(R.id.LastTime);
                ManName.setText(itemData.get("ManName").toString());
                LastTime.setText(itemData.get("LastTime").toString());
                return convertView;
            }
        };
        GroupManList.setAdapter(baseAdapter);
        final ListViewSwipeGesture touchListener = new ListViewSwipeGesture(
                GroupManList, swipeListener, this);
        touchListener.SwipeType	=	ListViewSwipeGesture.Double;    //设置两个选项列表项的背景
        GroupManList.setOnTouchListener(touchListener);
    }



    ListViewSwipeGesture.TouchCallbacks swipeListener = new ListViewSwipeGesture.TouchCallbacks() {

        @Override
        public void FullSwipeListView(int position) {
            // TODO Auto-generated method stub
            Toast.makeText(MyListActivity.this, "Action_2", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void HalfSwipeListView(int position) {
            // TODO Auto-generated method stub
//            System.out.println("<<<<<<<" + position);
            data.remove(position);
            baseAdapter.notifyDataSetChanged();
            Toast.makeText(MyListActivity.this,"删除", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void LoadDataForScroll(int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onDismiss(ListView listView, int[] reverseSortedPositions) {
            // TODO Auto-generated method stub
//            Toast.makeText(activity,"Delete", Toast.LENGTH_SHORT).show();
//            for(int i:reverseSortedPositions){
//                data.remove(i);
//                new MyAdapter().notifyDataSetChanged();
//            }
        }

        @Override
        public void OnClickListView(int position) {
            // TODO Auto-generated method stub


        }


    };
    /*
       初始化View
     */
    private void initView(){
        GroupManList=(ListView)findViewById(R.id.GroupManList);
    }



}

