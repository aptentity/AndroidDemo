package com.borg.mvp.view.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.borg.androidemo.R;

import java.util.List;

/**
 * Created by moon.zhong on 2015/3/25.
 */
public class TestAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder, String> {

    public TestAdapter(List<String> list) {
        super(list);
    }

    @Override
    public BaseRecyclerViewHolder createViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        BaseRecyclerAdapter.BaseRecyclerViewHolder holder;
        if (viewType == 0){
            View view = inflater.inflate(R.layout.test_recycler_item, parent, false);
            holder = new ViewHolder(view);
        }else {
            View view = inflater.inflate(R.layout.test_recycler_item2, parent, false);
            holder = new ViewHolder2(view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerAdapter.BaseRecyclerViewHolder holder, int position, final String data) {
        if (getItemViewType(position)  == 0){
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.mTitle.setText(data);
        }else {
            ViewHolder2 viewHolder = (ViewHolder2) holder;
            viewHolder.mTitle.setText(data);
            viewHolder.mTitle1.setText(data);
        }

    }

    public class ViewHolder extends BaseRecyclerAdapter.BaseRecyclerViewHolder {
        public TextView mTitle;

        protected ViewHolder(View mView) {
            super(mView);
            mTitle = findView(R.id.id_title);
        }
    }
    public class ViewHolder2 extends BaseRecyclerAdapter.BaseRecyclerViewHolder {
        public TextView mTitle;
        public TextView mTitle1;

        protected ViewHolder2(View mView) {
            super(mView);
            mTitle = findView(R.id.id_title);
            mTitle1 = findView(R.id.id_title1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position  == 0){
            return 0;
        }else {
            return 1 ;
        }
    }
}
