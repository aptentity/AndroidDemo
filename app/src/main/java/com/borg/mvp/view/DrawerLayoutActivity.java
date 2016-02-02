package com.borg.mvp.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.borg.androidemo.R;
import com.borg.mvp.utils.LogHelper;
import com.borg.mvp.utils.NetworkUtils;
import com.borg.mvp.view.fragment.PlanetFragment;

public class DrawerLayoutActivity extends Activity {
    private final String TAG = DrawerLayoutActivity.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mPlanetTitles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                LogHelper.d(TAG,"onDrawerSlide slideOffset:"+slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                LogHelper.d(TAG,"onDrawerOpened");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                LogHelper.d(TAG,"onDrawerClosed");
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                LogHelper.d(TAG,"onDrawerStateChanged");
            }
        });

        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        setNotification();

        findViewById(R.id.content_frame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte type= NetworkUtils.getNetworkClass(DrawerLayoutActivity.this);
                LogHelper.d("type="+type);
            }
        });

    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    private void selectItem(int position) {
        LogHelper.d(TAG,"selectItem:"+position);
        // update the main content by replacing fragments
        Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void setNotification(){
        //简单的Notification
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("新消息")
                .setContentText("点我就下载!")
                .setSmallIcon(R.drawable.ic_folder)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1,notification);
    }
}
