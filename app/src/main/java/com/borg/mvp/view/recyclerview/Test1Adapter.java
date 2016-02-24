package com.borg.mvp.view.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.borg.androidemo.R;

import java.util.List;

/**
 * Created by moon.zhong on 2015/3/25.
 */
public class Test1Adapter extends BaseAdapter<BaseAdapter.BaseViewHolder, String> {

    public Test1Adapter(List<String> list) {
        super(list);
    }

    @Override
    public BaseViewHolder createViewHolder(LayoutInflater inflater, int position) {
        BaseAdapter.BaseViewHolder holder;
        View view = inflater.inflate(R.layout.test_recycler_item, null);
        holder = new ViewHolder(view);
        return holder ;
    }



    @Override
    public void onBindViewHolder(BaseAdapter.BaseViewHolder holder, int position, final String data) {

            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.mTitle.setText(data);

    }

    public class ViewHolder extends BaseAdapter.BaseViewHolder {
        public TextView mTitle;

        protected ViewHolder(View mView) {
            super(mView);
            mTitle = findView(R.id.id_title);
        }
    }
    public class ViewHolder2 extends BaseAdapter.BaseViewHolder {
        public TextView mTitle;
        public TextView mTitle1;

        protected ViewHolder2(View mView) {
            super(mView);
            mTitle = findView(R.id.id_title);
            mTitle1 = findView(R.id.id_title1);
        }
    }

}
