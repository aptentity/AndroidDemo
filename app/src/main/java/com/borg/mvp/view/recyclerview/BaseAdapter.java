package com.borg.mvp.view.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by moon.zhong on 2015/3/11.
 */
public abstract class BaseAdapter<T extends BaseAdapter.BaseViewHolder,D> extends android.widget.BaseAdapter {

    private List<D> mDataList ;

    public BaseAdapter(List<D> list) {
        mDataList = list ;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public D getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<D> getDataList(){
        return mDataList ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        long start=System.currentTimeMillis();

        T viewHolder ;
        if (convertView == null){
            viewHolder = createViewHolder(LayoutInflater.from(parent.getContext()),position) ;
            convertView = viewHolder.mView ;
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (T) convertView.getTag();
        }
        onBindViewHolder(viewHolder,position,getItem(position)) ;
//        long end=System.currentTimeMillis();
        return convertView;
    }

    public static abstract class BaseViewHolder{
        public final View mView ;

        protected BaseViewHolder(View mView) {
            this.mView = mView;
        }

        public <K extends View> K findView(int id){
            return (K) mView.findViewById(id);
        }
    }

    public abstract T createViewHolder(LayoutInflater inflater,int position);

    public abstract void onBindViewHolder(T holder, int position,D data);
}
