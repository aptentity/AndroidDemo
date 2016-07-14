package com.borg.mvp.model.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.borg.mvp.utils.LogHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * Created by Gulliver(feilong) on 16/7/14.
 */
public class WifiAPManager {

    private static final String TAG = WifiAPManager.class.getSimpleName();
    private static WifiAPManager mInstance = null;
    private WifiManager mWifiManager = null;

    private WifiAPManager(Context context){
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }


    public static WifiAPManager getInstance(Context context){
        if (mInstance == null){
            mInstance = new WifiAPManager(context);
        }
        return mInstance;
    }

    // wifi热点开关
    public boolean openWifiAP(String ssid,String pwd){
        //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
        mWifiManager.setWifiEnabled(false);
        //热点的配置类
        WifiConfiguration apConfig = createWifiInfo(ssid,pwd,3);
        //配置热点的名称(可以在名字后面加点随机数什么的)
        //apConfig.SSID = ssid;
        //配置热点的密码
        //apConfig.preSharedKey=pwd;
        try{
            //通过反射调用设置热点
            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, Boolean.TYPE);
            //返回热点打开状态
            return (Boolean) method.invoke(mWifiManager, apConfig, true);
        }catch (NoSuchMethodException e){

        }catch (IllegalAccessException e){

        }catch (InvocationTargetException e){

        }
        return false;
    }

    public void closeWifiAp( ) {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);
            Method method2 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method2.invoke(mWifiManager, config, false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
                e.printStackTrace();
        } catch (InvocationTargetException e) {
                e.printStackTrace();
        }

        mWifiManager.setWifiEnabled(true);
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        if(Type == 1) //WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0]= "\""+Password+"\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\""+Password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }


    public final static int TYPE_NO_PASSWD = 1;
    public final static int TYPE_WEP = 2;
    public final static int TYPE_WPA = 3;

    /**
     * 连接信息生成配置对象
     * @param SSID
     * @param password
     * @param type
     * @return
     * @author wanghongbin

     */

    public static WifiConfiguration createWifiInfo(String SSID, String password, int type) {
        Log.v(TAG, "whb SSID =" + SSID + "## Password =" + password + "## Type = " + type);

        WifiConfiguration config = new WifiConfiguration();
        config.SSID = SSID;

        //clearAll(SSID);

        // 分为三种情况：1没有密码2用wep加密3用wpa加密

        if (type == TYPE_NO_PASSWD) {// WIFICIPHER_NOPASS
            config.hiddenSSID = false;
            config.status =  WifiConfiguration.Status.ENABLED;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.preSharedKey = null;
        } else if (type == TYPE_WEP) { // WIFICIPHER_WEP
            config.hiddenSSID = true;
            config.wepKeys[0] = password;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == TYPE_WPA) { // WIFICIPHER_WPA
            config.preSharedKey = password;
            config.hiddenSSID = false;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.priority = 10000;
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }


    /**
     * 移除所有同名节点
     * @param SSID
     */
    private void clearAll(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        //按照networkId从大到小排序
        //Collections.sort(existingConfigs, new ComparatorConfig());
        for (WifiConfiguration existingConfig : existingConfigs) {
            LogHelper.i(TAG,"existingConfig.SSID="+existingConfig.SSID+",netID = "+ existingConfig.networkId);
            if (existingConfig.SSID.equals("\""+SSID+"\"") /*&& existingConfig.preSharedKey.equals("\"" + password + "\"")*/) {
                mWifiManager.disableNetwork(existingConfig.networkId);
                mWifiManager.removeNetwork(existingConfig.networkId);
            }
        }
        mWifiManager.saveConfiguration();
    }

    public void addNetwork(WifiConfiguration wcg){
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b = mWifiManager.enableNetwork(wcgID,true);
    }

    /**
     * 添加一个网络节点并连接
     * @param ssid   wifi节点名称
     * @param passwd  密码
     * @param type  : TYPE_WEP TYPE_WPA
     */
    public void addNetwork(String ssid, String passwd, int type) {
        closeWifiAp();
        WifiConfiguration mConfig = createWifiInfo(ssid, passwd, type);
        connectToWifiWithConfiguration(mConfig);
    }


    public int isWifiContected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        Log.v(TAG, "isConnectedOrConnecting = " + wifiNetworkInfo.isConnected());
        Log.d(TAG, "wifiNetworkInfo.getDetailedState() = " + wifiNetworkInfo.getDetailedState());
        if (wifiNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.OBTAINING_IPADDR
                || wifiNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTING){
            return 1;
        } else if (wifiNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
            return 2;
        } else {
            Log.d(TAG, "getDetailedState() == " + wifiNetworkInfo.getDetailedState());
            return -1;

    }
}




    public static final int WAIT_FOR_SCAN_RESULT = 10 * 1000; //10 seconds

    public static final int WIFI_SCAN_TIMEOUT = 20 * 1000;
    /**
     * 连接指定热点
     * @param config
     * @return true : 调用函数成功，具体网络状态还得检测
     * @author wanghongbin  这个函数害死人阿，网上看了半天也没人说，最后是看的源代码里的WifiConnectionTest.java才明白需要等待，步步等待，步步惊心
     */

    public boolean connectToWifiWithConfiguration(WifiConfiguration config) {
        String ssid = config.SSID;
        config.SSID = "\""+ssid+"\"";

        //If Wifi is not enabled, enable it
        if (!mWifiManager.isWifiEnabled()) {
            Log.v(TAG, "Wifi is not enabled, enable it");
            mWifiManager.setWifiEnabled(true);
        }

        List<ScanResult> netList = mWifiManager.getScanResults();
        if (netList == null) {
            Log.v(TAG, "scan results are null");

            // if no scan results are available, start active scan
            mWifiManager.startScan();
            boolean mScanResultIsAvailable = false;
            long startTime = System.currentTimeMillis();
            while (!mScanResultIsAvailable) {
                if ((System.currentTimeMillis() - startTime) > WIFI_SCAN_TIMEOUT) {
                    return false;
                }

                // wait for the scan results to be available
                synchronized (this) {
                // wait for the scan result to be available
                    try {
                        this.wait(WAIT_FOR_SCAN_RESULT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if ((mWifiManager.getScanResults() == null) ||
                            (mWifiManager.getScanResults().size() <= 0)) {
                        continue;
                    }
                    mScanResultIsAvailable = true;
                }
            }
        }

        netList = mWifiManager.getScanResults();
        for (int i = 0; i < netList.size(); i++) {
            ScanResult sr= netList.get(i);
            if (sr.SSID.equals(ssid)) {
                Log.v(TAG, "found " + ssid + " in the scan result list");
                int networkId = mWifiManager.addNetwork(config);
                // Connect to network by disabling others.
                mWifiManager.enableNetwork(networkId, true);
                mWifiManager.saveConfiguration();
                mWifiManager.reconnect();
                break;
            }
        }

        List<WifiConfiguration> netConfList = mWifiManager.getConfiguredNetworks();
        if (netConfList.size() <= 0) {
            Log.v(TAG, ssid + " is not available");
            return false;
        }
        return true;
    }
}
