package com.borg.mvp.model.Network;

/**
 * Created by Gulliver(feilong) on 16/7/13.
 */



import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class MulticastServer implements Runnable{
    private String TAG = MulticastServer.class.getSimpleName();
    private InetAddress mBroadcastAddr;
    private DatagramSocket mSocket;
    private DatagramPacket mDatagram;

//	private String mSendData = "AAAA";

    private byte[] mBuffer = null;
    // mSendData.getBytes();

    public static final int SERVER_SEND_PORT = 4445;
    public static final int CLIENT_RECEIVE_PORT = 4446;

    //Seehttp://www.gznc.edu.cn/yxsz/jjglxy/book/Java_api/java/net/MulticastSocket.html
    public static final String BROADCAST_IP = "234.5.6.7";

    private Thread mThread = null;

    private Context mContext = null;
    public MulticastServer(Context context){
        Log.d(TAG, "######## MulticastServer ##########");

        mContext = context;

        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock multicastLock = wifiManager.createMulticastLock("mydebuginfo");
        multicastLock.acquire();

        try {
            mSocket = new DatagramSocket(SERVER_SEND_PORT);
        } catch (SocketException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            mBroadcastAddr=InetAddress.getByName(BROADCAST_IP);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.d(TAG, "MulticastServer");
        Log.d(TAG, "String = " + getLocalIpAddress());
        mBuffer = getLocalIpAddress().getBytes();

        String str = new String(mBuffer);
        Log.d(TAG, "mBuffer = " + str);

        mDatagram = new DatagramPacket(mBuffer, mBuffer.length, mBroadcastAddr, CLIENT_RECEIVE_PORT);

        mThread = new Thread(this);

    }

    public void startSend() {
        Log.d(TAG, "startSend");

        SEND_FLAG = true;
        mThread.start();
    }


    public void stopSend() {
        SEND_FLAG = false;
    }


    private boolean SEND_FLAG = true;
    @Override
    public void run() {
        // TODO Auto-generated method stub
        while(SEND_FLAG) {
            broadcast_data();
        }
    }

    private String sendStr = "";
    private void broadcast_data()
    {

        try {
            Log.d(TAG, "broadcast_data");

            mSocket.send(mDatagram);
            sendStr = new String(mDatagram.getData());
            Log.d(TAG, "send ok data = " + sendStr);
            mHandler.sendEmptyMessage(TOAST_MSG_SEND);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return "";
    }

    private final int TOAST_MSG_SEND = 0x01;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case TOAST_MSG_SEND:
                    Toast.makeText(mContext, sendStr, Toast.LENGTH_SHORT).show();
            }
        }
    };
}

