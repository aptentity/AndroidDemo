package com.borg.androidemo.fragment;

/**
 * Created by Gulliver(feilong) on 15/11/27.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.borg.androidemo.R;

public class DetailsFragment extends Fragment {
    static String[] array;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public static DetailsFragment newInstance(int index) {
        DetailsFragment details = new DetailsFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        details.setArguments(args);
        return details;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (container == null)
            return null;

        array = getResources().getStringArray(R.array.countries_array);

        ScrollView scroller = new ScrollView(getActivity());

        /*GridView gridview = (GridView) getActivity().findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(getActivity()));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(DetailsFragment.this.getActivity(), " " + position, Toast.LENGTH_SHORT).show();
            }
        });*/

        /*//定义UI组件
        final ImageView iv= (ImageView)getActivity().findViewById(R.id.ImageView01);
        Gallery g = (Gallery) getActivity().findViewById(R.id.Gallery01);

        //设置图片匹配器
        g.setAdapter(new ImageAdapter(getActivity()));

        //设置AdapterView点击监听器，Gallery是AdapterView的子类
        g.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                //显示点击的是第几张图片
                Toast.makeText(DetailsFragment.this.getActivity(), "" + position,
                        Toast.LENGTH_LONG).show();
                //设置背景部分的ImageView显示当前Item的图片
                iv.setImageResource(((ImageView)view).getId());
            }
        });*/


        TextView text = new TextView(getActivity());

        int padding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, getActivity()
                        .getResources().getDisplayMetrics());
        text.setPadding(padding, padding, padding, padding);
        scroller.addView(text);

        //text.setText(array[getShownIndex()]);

        /*Button btnContact = (Button) getActivity().findViewById(R.id.bt1);
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(), MainHelloGallery.class);
                startActivity(intent);
            }
        });*/

        return scroller;
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
         menu.add("Menu 1a").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            menu.add("Menu 1b").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        Toast.makeText(getActivity(), "index is"+getShownIndex()+" && menu text is "+item.getTitle(), 1000).show();
        return super.onOptionsItemSelected(item);
    }*/
}