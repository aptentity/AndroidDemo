package com.borg.mvp.view;

import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.borg.androidemo.R;
import com.borg.mvp.model.Network.WifiAPManager;

public class WifiAPActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_ap);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        findViewById(R.id.btnOpenAP).setOnClickListener(this);
        findViewById(R.id.btnCloseAP).setOnClickListener(this);
        findViewById(R.id.btnConnectAP).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOpenAP:
                WifiAPManager.getInstance(this).openWifiAP("mytest","123456789");
                break;
            case R.id.btnCloseAP:
                WifiAPManager.getInstance(this).closeWifiAp();
                break;
            case R.id.btnConnectAP:
                WifiConfiguration configuration = WifiAPManager.createWifiInfo("mytest","123456789",3);
                //WifiAPManager.getInstance(this).addNetwork(configuration);
                WifiAPManager.getInstance(this).addNetwork("mytest","123456789",3);
                break;
        }
    }
}
