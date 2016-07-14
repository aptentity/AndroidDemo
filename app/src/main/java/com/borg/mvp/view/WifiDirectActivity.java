package com.borg.mvp.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.borg.androidemo.R;

public class WifiDirectActivity extends Activity implements View.OnClickListener{
    private String TAG = WifiDirectActivity.class.getSimpleName();
    private WifiP2pManager mManager;
    private Channel mChannel;
    private WiFiDirectBroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_direct);

        mManager =(WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(),null);
        mReceiver =new WiFiDirectBroadcastReceiver(mManager, mChannel,this);
        IntentFilter mIntentFilter =new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        registerReceiver(mReceiver, mIntentFilter);

        findViewById(R.id.btnConnectP2p).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnConnectP2p:
                discover();
                break;
        }
    }

    private void discover(){
        mManager.discoverPeers(mChannel,new WifiP2pManager.ActionListener(){
            @Override
            public void onSuccess(){
                Log.d(TAG,"discoverPeers onSuccess");
            }

            @Override
            public void onFailure(int reasonCode){
                Log.d(TAG,"discoverPeers onFailure reasonCode="+reasonCode);
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    /**

     * A BroadcastReceiver that notifies of important Wi-Fi p2p events.

     */

    public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
        private WifiP2pManager manager;
        private Channel channel;
        private WifiDirectActivity activity;
        public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
                                           WifiDirectActivity activity) {
            super();
            this.manager = manager;
            this.channel = channel;
            this.activity = activity;
        }



        @Override

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                //当启用或禁用设备上的Wi-Fi Direct时，发出这个广播
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);
                Log.d(TAG,"WifiP2pManager.EXTRA_WIFI_STATE="+state);
                if(state ==WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                    // Wifi Direct is enabled
                }else{
                    // Wi-Fi Direct is not enabled
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                // 在调用discoverPeers()方法时，发出这个广播，如果你要在应用程序中处理这个Intent，
                // 通常是希望调用requestPeers()方法来获取对等设备的更新列表。
                Log.d(TAG,"WIFI_P2P_PEERS_CHANGED_ACTION");
                if(manager !=null){
                    manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                        @Override
                        public void onPeersAvailable(WifiP2pDeviceList peers) {
                            Log.d(TAG,"onPeersAvailable");
                            for (WifiP2pDevice device:peers.getDeviceList()){
                                Log.d(TAG,device.toString());
                                connectDevice(device);
                            }
                            Log.d(TAG,"onPeersAvailable end");
                        }
                    });
                }
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                Log.d(TAG,"WIFI_P2P_CONNECTION_CHANGED_ACTION");
                // Respond to new connection or disconnections
                //在设备的Wi-Fi连接状态变化时，发出这个广播
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                Log.d(TAG,"WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
                // Respond to this device's wifi state changing
                // 当设备的细节（如设备的名称）发生变化时，发出这个广播
            }

        }

    }


    public void connectDevice(WifiP2pDevice device){
        WifiP2pConfig config =new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        mManager.connect(mChannel, config,new WifiP2pManager.ActionListener(){
            @Override
            public void onSuccess(){
                Log.d(TAG,"connectDevice onSuccess");
            }

            @Override
            public void onFailure(int reason){
                Log.d(TAG,"connectDevice onFailure reason="+reason);
            }
        });
    }
}
