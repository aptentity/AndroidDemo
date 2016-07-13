package com.borg.mvp.model.Network;

/**
 * Created by Gulliver(feilong) on 16/7/13.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class MulticastClient implements Runnable {

    private MulticastSocket mMultiSocket;
    private InetAddress mAddress;

    private boolean mScan = false;

    private Thread mThread = null;

    private byte[] mBuffer = new byte[256];

    private DatagramPacket mDatagram = null;

    private static final String TAG = "MulticastClient";
    private Context mContext = null;

    public MulticastClient(Context context) throws IOException {
        mContext = context;

        Log.d(TAG, "######## MulticastClient #########");
        mAddress = InetAddress.getByName(MulticastServer.BROADCAST_IP);

        mMultiSocket = new MulticastSocket(MulticastServer.CLIENT_RECEIVE_PORT);

        mMultiSocket.joinGroup(mAddress);

        Log.d(TAG, "MulticastClient");

        mThread = new Thread(this);

        // scan_recv();
    }

    public void startReceive() {
        this.mScan = true;
        mThread.start();
    }

    public void stopReceive() {
        this.mScan = false;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        Log.d(TAG, "run");
        scan_recv();
    }

    private String receiveStr = "";

    public void scan_recv() {

        // DatagramPacket packet;

        while (mScan) {
            Log.d(TAG, "scan_recv");

            try {
                mDatagram = new DatagramPacket(mBuffer, mBuffer.length);
                mMultiSocket.receive(mDatagram);
                receiveStr = new String(mDatagram.getData());
                Log.v(TAG, "^^^^^^^^^data = " + receiveStr + "^^^^^^^^^^^^");
                mHandler.sendEmptyMessage(TOAST_MSG_RECEIVE);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                this.mScan = false;
            }

            try {

                Log.d(TAG, "sleep");

                Thread.sleep(100);
                // sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    private final int TOAST_MSG_RECEIVE = 0x01;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TOAST_MSG_RECEIVE:
                    Toast.makeText(mContext, receiveStr, Toast.LENGTH_SHORT).show();
            }
        }
    };

}
