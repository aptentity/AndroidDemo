package com.borg.androidemo.fragment;

/**
 * Created by Gulliver(feilong) on 15/11/27.
 */

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.borg.androidemo.R;

public class TitlesFragment extends ListFragment {
    static String[] array;

    boolean mDualPane;
    int mCurCheckPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        array = getResources().getStringArray(R.array.countries_array);
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, array));

        View detailsFrame = getActivity().findViewById(R.id.details);

        mDualPane = detailsFrame != null
                && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0); //从保存的状态中取出数据
        }

        if (mDualPane) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            showDetails(mCurCheckPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        showDetails(mCurCheckPosition);
    }

    void showDetails(int index) {
        mCurCheckPosition = index;
//        if (mDualPane) {
//            getListView().setItemChecked(index, true);
//            DetailsFragment details = (DetailsFragment) getFragmentManager()
//                    .findFragmentById(R.id.details);
//            if (details == null || details.getShownIndex() != index) {
//                details = DetailsFragment.newInstance(mCurCheckPosition);
//
//                //得到一个fragment事务（类似sqlite的操作）
//                FragmentTransaction ft = getFragmentManager()
//                        .beginTransaction();
//                ft.replace(R.id.details, details);
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//                ft.commit();//提交
//            }
//        } else {
//            new AlertDialog.Builder(getActivity()).setTitle(
//                    android.R.string.dialog_alert_title).setMessage(
//                    array[index]).setPositiveButton(android.R.string.ok,
//                    null).show();
//        }
    }

}