package com.borg.androidemo.devices.connection.bluetooth;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.text.TextUtils;

import com.borg.androidemo.common.utils.CKLOG;
import com.borg.androidemo.devices.api.AccessType;
import com.borg.androidemo.devices.connection.DeviceConnectionContainer;
import com.borg.androidemo.devices.connection.bluetooth.ble.bledevice.BLEDeviceType;
import com.borg.androidemo.devices.connection.bluetooth.ble.callback.IAliBLECharacteristicCallback;
import com.borg.androidemo.devices.connection.bluetooth.ble.callback.IAliBLEConnectCallback;
import com.borg.androidemo.devices.connection.bluetooth.ble.callback.IAliBLEScanCallback;
import com.borg.androidemo.devices.connection.bluetooth.ble.callback.IAliBLESendStateCallback;
import com.borg.androidemo.devices.connection.bluetooth.ble.callback.IAliBLEServiceBinder;
import com.borg.androidemo.devices.connection.bluetooth.ble.listener.BLECharacteristicListener;
import com.borg.androidemo.devices.connection.bluetooth.ble.listener.ScanBluetoothDevicesListener;
import com.borg.androidemo.devices.connection.bluetooth.ble.listener.StatusListener;
import com.borg.androidemo.devices.connection.bluetooth.ble.service.BLEService;
import com.borg.androidemo.devices.connection.bluetooth.ble.utils.BLEUtils;
import com.borg.androidemo.devices.device.bluetoothdevice.BluetoothDev;
import com.borg.androidemo.devices.protocol.ServiceCategory;
import com.borg.androidemo.devices.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This class supplies APIs for Application which uses AliBLESdk.
 *
 * @author jinyi
 */
public class AliBluetoothManager {

    private static final String TAG = "AliBluetoothManager";
    private static final String REMOTE_BT_SERVICE_NAME = "BTService";
    private static final String REMOTE_BLE_SERVICE_NAME = "BLEService";
    private static final String REMOTE_BT_SERVICE_ACTION = "com.yunos.alibt.remoteservice";
    private static final String REMOTE_BLE_SERVICE_ACTION = "com.yunos.alible.remoteservice";

    private static AliBluetoothManager sInstance;

    private Context mContext;
    private IAliBLEServiceBinder mBLEServiceBinder;
    private IAliBLEServiceBinder mBTServiceBinder;

    private final Object mStatusListenerLock = new Object();
    private final Object mConnectBLEDevicesListenerLock = new Object();
    private final Object mBLECharacteristicListenerLock = new Object();

    private final Object mBLENotificationListenerLock = new Object();
    private final ArrayList<StatusListener> mStatusListeners = new ArrayList<StatusListener>();
    //    private final ArrayList<BLENotificationListener> mBLENotificationListeners = new ArrayList<>();
    private final ArrayList<BLECharacteristicListener> mBLECharacteristicListeners = new ArrayList<BLECharacteristicListener>();
    private final DeviceConnectionContainer mTmpContainer = new DeviceConnectionContainer();
    private final DeviceConnectionContainer mDeviceConnectionContainer = new DeviceConnectionContainer();

    private Handler mWorkHandler;
    private static final String WORK_THREAD_NAME = "AliBluetoothManager_work_thread";
    private HandlerThread mWorkThread;

    /**
     * 绑定BLEService的连接对象
     */
    private ServiceConnection mBLEConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CKLOG.Info(TAG, "onServiceConnected() ... ");
            mBLEServiceBinder = ((IAliBLEServiceBinder) service);
            mBLEServiceBinder.sayHi(mContext.getPackageName());
            mBLEServiceBinder.registerBLEScanCallback(mRemoteBLEScanCallback);
            mBLEServiceBinder.registerBLEConnectCallback(mRemoteBLEConnectCallback);
            registerBLECharacteristicListener();
            mBLEServiceBinder.registerBLENotificationCallback(mRemoteBLENotificationCallback,
                    ServiceCategory.categorys, mContext.getPackageName());
            onInitSuccessfulEx();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            CKLOG.Info(TAG, "onServiceDisconnected() ...");
            mBLEServiceBinder = null;
            // Try to re-bind service
            bindRemoteBLEService();
        }
    };

    /**
     * 绑定BTService的连接对象
     */
    private ServiceConnection mBTConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CKLOG.Info(TAG, "onServiceConnected() ... ");
            mBTServiceBinder = ((IAliBLEServiceBinder) service);
            mBTServiceBinder.sayHi(mContext.getPackageName());
            mBTServiceBinder.registerBLEScanCallback(mRemoteBLEScanCallback);
            mBTServiceBinder.registerBLEConnectCallback(mRemoteBLEConnectCallback);
            registerBTCharacteristicListener();
            mBTServiceBinder.registerBLENotificationCallback(mRemoteBLENotificationCallback,
                    ServiceCategory.categorys, mContext.getPackageName());

            onInitSuccessfulEx();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            CKLOG.Info(TAG, "onServiceDisconnected() ...");
            mBTServiceBinder = null;
            // Try to re-bind service
            bindRemoteBTService();
        }
    };

    /**
     * 获取AliBluetoothManager实例。
     *
     * @return
     * @date 2015-6-15上午10:10:47
     */
    public synchronized static AliBluetoothManager instance() {
        if (null == sInstance) {
            sInstance = new AliBluetoothManager();
        }
        return sInstance;
    }

    private AliBluetoothManager() {
    }

//    private static Object mConnStateLock = new Object();
    private static HashMap<String, Integer> mConnStateMap = new HashMap<>();

    private static int STATE_DISCONNECTED = 0;
    private static int STATE_CONNECTING = 1;
    private static int STATE_CONNECTED = 2;


    private synchronized void setConnState(int state, String address) {
//        synchronized (mConnStateLock) {
            if (mConnStateMap.containsKey(address)) {
                mConnStateMap.remove(address);
            }
            CKLOG.Debug(TAG, "in AliBLEManager setConnState()...set device " + address + " connect state to --->" + (state == STATE_CONNECTING ? "CONNECTING" : (state == STATE_CONNECTED ? "CONNECTED" : "DISCONNECTED")));
            if (state != STATE_DISCONNECTED) {
                mConnStateMap.put(address, state);
            }
//        }
    }

    private IAliBLEScanCallback mRemoteBLEScanCallback = new IAliBLEScanCallback() {

        @Override
        public void onScannedBLEDevicesUpdated(final List<BluetoothDevice> devices) {
            onScannedBLEDevicesUpdatedEx(devices);
        }

        @Override
        public void onScanStart() {
            CKLOG.Debug(TAG, "onScanStart() ...");
            onScanStartEx();
        }

        @Override
        public void onScanStop() {
            CKLOG.Debug(TAG, "onScanStop() ...");
            onScanStopEx();
        }
    };

    private IAliBLEConnectCallback mRemoteBLEConnectCallback = new IAliBLEConnectCallback() {

        @Override
        public void onConnected(final BluetoothDevice device) {
            CKLOG.Debug(TAG, "onConnected(): " + device.getAddress());

            CKLOG.Debug(TAG, "IAliBLEConnectCallback onConnected...");
            if (device == null) {
                CKLOG.Error(TAG, "onConnected...device is null...return");
                return;
            }
            setConnState(STATE_CONNECTED, device.getAddress());
            onBLEDevicesConnectedEx(device);
        }

        @Override
        public void onDisconnected(final BluetoothDev device) {
            CKLOG.Debug(TAG, "onDisconnected(): " + device.getAddress());
            CKLOG.Debug(TAG, "IAliBLEConnectCallback onDisconnected...");
            setConnState(STATE_DISCONNECTED, device.getAddress());
            onBLEDevicesDisconnectedEx(device);
        }

        @Override
        public void onConnecting(final BluetoothDevice device) {
            CKLOG.Debug(TAG, "onConnecting(): " + device.getAddress());
            CKLOG.Debug(TAG, "IAliBLEConnectCallback onConnecting...");
            if (device == null) {
                CKLOG.Error(TAG, "onConnecting...device is null...return");
                return;
            }
            setConnState(STATE_CONNECTING, device.getAddress());
            onBLEDevicesConnectingEx(device);
        }
    };

    private IAliBLESendStateCallback mRemoteBLENotificationCallback = new IAliBLESendStateCallback() {

        @Override
        public void onSendMessageCompleted(final String device, final String notification) {
            onSendMessageCompletedEx(device, notification);
        }

        @Override
        public void onSendMessageFailed(final String adress, final String notification, final int failCode) {
            onSendMessageFailedEx(adress, notification, failCode);
        }

        @Override
        public void onReceiveMessage(final BluetoothDevice device, final byte[] content) {
            onReceiveMessageEx(device, content);
        }
    };

    private IAliBLECharacteristicCallback mRemoteBLECharacteristicCallback = new IAliBLECharacteristicCallback() {

        @Override
        public void onCharacteristicRead(final BluetoothDevice device, final String serviceUuid, final String characteristicUuid,
                                         final byte[] characteristicValue, final int status) {
            onCharacteristicReadEx(device, UUID.fromString(serviceUuid), UUID.fromString(characteristicUuid), characteristicValue, status);
        }

        @Override
        public void onCharacteristicWrite(final BluetoothDevice device, final String serviceUuid, final String characteristicUuid,
                                          final byte[] characteristicValue, final int status) {
            onCharacteristicWriteEx(device, UUID.fromString(serviceUuid), UUID.fromString(characteristicUuid), characteristicValue, status);
        }

        @Override
        public void onCharacteristicChanged(final BluetoothDevice device, final String serviceUuid, final String characteristicUuid,
                                            final byte[] characteristicValue) {
            onCharacteristicChangedEx(device, UUID.fromString(serviceUuid), UUID.fromString(characteristicUuid), characteristicValue);
        }
    };

    /**
     * 初始化AliBluetoothManager， 第一次使用时，必须调用这个初始化方法。
     *
     * @param context ， ApplicationContext实例。
     * @date 2015-6-15上午10:11:42
     */
    public void init(final Context context) {
        if (null == context) {
            onInitFailedEx(StatusListener.INIT_ERROR_CONTEXT_NULL);
            return;
        }
        CKLOG.Info(TAG, "init() ... ");
        mContext = context.getApplicationContext();
        bindRemoteBLEService();
        bindRemoteBTService();
        startWorkThread();
    }

    private void onScanStartEx() {
        synchronized (mScanBLEDevicesListenerLock) {
            CKLOG.Info(TAG, "onScanStartEx() ... ");
            for (ScanBluetoothDevicesListener listener : mScanBluetoothDevicesListeners) {
                if (null != listener) {
                    listener.onScanStartInternal();
                }
            }
        }
    }

    private void onScanStopEx() {
        synchronized (mScanBLEDevicesListenerLock) {
            CKLOG.Info(TAG, "onScanStopEx() ... ");
            for (ScanBluetoothDevicesListener listener : mScanBluetoothDevicesListeners) {
                if (null != listener) {
                    listener.onScanStopInternal();
                }
            }
        }
    }

    private void startWorkThread() {
        if (null == mWorkThread) {
            mWorkThread = new HandlerThread(WORK_THREAD_NAME);
            mWorkThread.start();
            mWorkHandler = new Handler(mWorkThread.getLooper());
        }
    }

    private void bindRemoteBLEService() {
        CKLOG.Info(TAG, "bindRemoteService() ...");
        String intentname = REMOTE_BLE_SERVICE_ACTION;
        //classname = REMOTE_BLE_SERVICE_NAME;
        Intent bleIntent = new Intent(mContext, BLEService.class);
        bleIntent.setAction(REMOTE_BLE_SERVICE_ACTION);
        mContext.bindService(bleIntent, mBLEConnection, Context.BIND_AUTO_CREATE);
    }

    private void bindRemoteBTService() {
//        CKLOG.Info(TAG, "bindRemoteService() ...");
//        String intentname = REMOTE_BT_SERVICE_ACTION;
//        //classname = REMOTE_BT_SERVICE_NAME;
//        Intent btIntent = new Intent(mContext, BTService.class);
//        btIntent.setAction(REMOTE_BT_SERVICE_ACTION);
//        mContext.bindService(btIntent, mBTConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindRemoteBLEService() {
        if (null == mContext) {
            CKLOG.Error(TAG, "unbindRemoteService() : mContext is null ! ");
            return;
        }
        if (null == mBLEServiceBinder) {
            return;
        }
        mContext.unbindService(mBTConnection);
        mBLEServiceBinder = null;
    }

    private void unbindRemoteBTService() {
        if (null == mContext) {
            CKLOG.Error(TAG, "unbindRemoteService() : mContext is null ! ");
            return;
        }
        if (null == mBLEServiceBinder) {
            return;
        }
        mContext.unbindService(mBTConnection);
        mBLEServiceBinder = null;
    }

    public void destroy() {
        clearRemoteBLECallbacks();
        unbindRemoteBLEService();
        unbindRemoteBTService();
        unregisterAllListeners();
        mContext = null;
    }

    private void clearRemoteBLECallbacks() {
        if (null == mBLEServiceBinder) {
            return;
        }
        mBLEServiceBinder.unregisterBLEScanCallback(mRemoteBLEScanCallback);
        mBLEServiceBinder.clearBLECharacteristicCallback(StringUtils.getCurProcessName(mContext));
    }

    private void clearRemoteBTCallbacks() {
        if (null == mBTServiceBinder) {
            return;
        }
        mBTServiceBinder.unregisterBLEScanCallback(mRemoteBLEScanCallback);
        mBTServiceBinder.clearBLECharacteristicCallback(StringUtils.getCurProcessName(mContext));
    }

    private void unregisterAllListeners() {
        unregisterAllStatusListeners();
//        unregisterAllNotificationListeners();
        unregisterAllScanBLEDevicesListeners();
        unregisterAllCharacteristicListeners();
        unregisterBLECharacteristicListener(ServiceCategory.categorys);
        unregisterBTCharacteristicListener(ServiceCategory.categorys);
    }

    public boolean registerStatusListener(final StatusListener listener) {
        synchronized (mStatusListenerLock) {
            if (null == listener) {
                CKLOG.Info(TAG, "registerStatusListener() : listener is null.");
                return false;
            }
            mStatusListeners.add(listener);
            return true;
        }
    }

    public boolean unregisterStatusListener(final StatusListener listener) {
        synchronized (mStatusListenerLock) {
            return mStatusListeners.remove(listener);
        }
    }

    public void unregisterAllStatusListeners() {
        synchronized (mStatusListenerLock) {
            mStatusListeners.clear();
        }
    }

    private void onInitSuccessfulEx() {
        synchronized (mStatusListenerLock) {
            for (StatusListener listener : mStatusListeners) {
                if (null != listener) {
                    listener.onInitSuccessfulInternal();
                }
            }
        }
    }

    private void onInitFailedEx(final int errorCode) {
        synchronized (mStatusListenerLock) {
            for (StatusListener listener : mStatusListeners) {
                if (null != listener) {
                    listener.onInitFailedInternal(errorCode);
                }
            }
        }
    }

    private Object mScanBLEDevicesListenerLock = new Object();

    // TODO 扫描的listener
    private ArrayList<ScanBluetoothDevicesListener> mScanBluetoothDevicesListeners = new ArrayList<ScanBluetoothDevicesListener>();

    public boolean registerScanBLEDevicesListener(final ScanBluetoothDevicesListener listener) {
        synchronized (mScanBLEDevicesListenerLock) {
            if (null == listener) {
                CKLOG.Info(TAG, "registerScanBLEDevicesListener() : listener is null.");
                return false;
            }
            mScanBluetoothDevicesListeners.add(listener);
            return true;
        }
    }

    public boolean unregisterScanBLEDevicesListener(final ScanBluetoothDevicesListener listener) {
        synchronized (mScanBLEDevicesListenerLock) {
            return mScanBluetoothDevicesListeners.remove(listener);
        }
    }

    public void unregisterAllScanBLEDevicesListeners() {
        synchronized (mScanBLEDevicesListenerLock) {
            mScanBluetoothDevicesListeners.clear();
        }
    }

    private void onScannedBLEDevicesUpdatedEx(final List<BluetoothDevice> devices) {
        synchronized (mScanBLEDevicesListenerLock) {
            CKLOG.Info(TAG, "onScannedBLEDevicesUpdatedEx() ... ");
            for (ScanBluetoothDevicesListener listener : mScanBluetoothDevicesListeners) {
                if (null != listener) {
                    listener.onScannedBLEDevicesUpdatedInternal(devices);
                }
            }
        }
    }

    /**
     * 开始扫描BLE设备。
     *
     * @date 2015-6-15上午10:13:28
     */
    public void scanBluetoothDevices(AccessType type) {

        switch (type) {
            case BLE_DEVICE_Direct:
                if (null == mBLEServiceBinder) {
                    CKLOG.Info(TAG, "scanBluetoothDevices() : mBLEServiceBinder is null , return.");
                    return;
                }
                CKLOG.Info(TAG, "scanBLEDevices() ... ");
                mBLEServiceBinder.scanBLEDevices();
                break;
            case BT_DEVICE_Direct:
                if (null == mBTServiceBinder) {
                    CKLOG.Info(TAG, "scanBluetoothDevices() : mBTServiceBinder is null , return.");
                    return;
                }
                CKLOG.Info(TAG, "scanBTDevices() ... ");
                mBTServiceBinder.scanBLEDevices();
                break;
        }
    }

    /**
     * 停止扫描BLE设备。
     *
     * @date 2015-6-15上午10:13:49
     */
    public void stopScanBLEDevices(AccessType type) {
        switch (type) {
            case BLE_DEVICE_Direct:
                if (null == mBLEServiceBinder) {
                    CKLOG.Info(TAG, "stopScanBLEDevices() : mServiceBinder is null , return.");
                    return;
                }
                CKLOG.Info(TAG, "stopScanBLEDevices() ... ");
                mBLEServiceBinder.stopScanBLEDevices();
                break;
            case BT_DEVICE_Direct:
                if (null == mBTServiceBinder) {
                    CKLOG.Info(TAG, "stopScanBLEDevices() : mServiceBinder is null , return.");
                    return;
                }
                CKLOG.Info(TAG, "stopScanBLEDevices() ... ");
                mBTServiceBinder.stopScanBLEDevices();
                break;
        }
    }

    /**
     * 获取已经被扫描到的设备列表。
     *
     * @return
     * @date 2015-6-15上午10:14:10
     */
    public synchronized List<BluetoothDevice> getScannedBLEDevices(AccessType type) {


        switch (type) {
            case BLE_DEVICE_Direct:
                if (null == mBLEServiceBinder) {
                    CKLOG.Info(TAG, "getScannedBLEDevices() :  mServiceBinder is null , return null.");
                    return null;
                }
                CKLOG.Info(TAG, "getScannedBLEDevices() ... ");
                List<BluetoothDevice> ret = null;
                ret = mBLEServiceBinder.getScannedBLEDevices();
                return ret;
            case BT_DEVICE_Direct:
                if (null == mBTServiceBinder) {
                    CKLOG.Info(TAG, "getScannedBLEDevices() :  mServiceBinder is null , return null.");
                    return null;
                }
                CKLOG.Info(TAG, "getScannedBLEDevices() ... ");
                List<BluetoothDevice> btRet = null;
                btRet = mBTServiceBinder.getScannedBLEDevices();
                return btRet;
            default:
                return null;
        }
    }

    private void onBLEDevicesConnectedEx(final BluetoothDevice dev) {
        synchronized (mConnectBLEDevicesListenerLock) {
            //final DeviceConnection conn = getDevice(conn.getAddress());
            if (dev == null) {
                CKLOG.Error("BluetoothDevice is null, error....return");
                return;
            }
            final BluetoothDev device = mTmpContainer.get(dev.getAddress());
            if (device == null) {
                CKLOG.Error("Device is null...return");
                return;
            }

            CKLOG.Debug(TAG, "onBLEDevicesConnectedEx() : device address - " + device.getAddress());

            mDeviceConnectionContainer.put(device.getAddress(), device);
            mTmpContainer.remove(device.getAddress());
            device.onConnected();
        }
    }

    private void onBLEDevicesDisconnectedEx(final BluetoothDev dev) {
        synchronized (mConnectBLEDevicesListenerLock) {
            if (dev == null) {
                CKLOG.Error(TAG, "device is null...return");
                return;
            }

            CKLOG.Debug(TAG, "onBLEDevicesDisconnectedEx() : device address - " + dev.getAddress());
            mDeviceConnectionContainer.remove(dev.getAddress());
            mTmpContainer.remove(dev.getAddress());
            dev.onDisconnected();
        }
    }

    private synchronized void onBLEDevicesConnectingEx(final BluetoothDevice dev) {
        synchronized (mConnectBLEDevicesListenerLock) {
            if (dev == null) {
                CKLOG.Error(TAG, "onBLEDevicesConnectingEx...device is null...return");
                return;
            }
            CKLOG.Debug(TAG, "onBLEDevicesConnectingEx() : device address - " + dev.getAddress());
            BluetoothDev device = mTmpContainer.get(dev.getAddress());
            if (device == null) {
                CKLOG.Error("DeviceConnection is null...return");
                return;
            }
            device.onConnecting();
        }
    }

    /**
     * 判断对应地址的设备是否连接
     *
     * @param dev 蓝牙设备
     * @return
     */
    public synchronized boolean isDeviceConnected(BluetoothDev dev) {
        List<BluetoothDevice> connectedBLEDevices = getConnectedBLEDevices(dev.getDeviceType());
        for (BluetoothDevice device : connectedBLEDevices) {
            if (dev.getAddress().equalsIgnoreCase(device.getAddress())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 连接到蓝牙设备
     *
     * @param device      蓝牙设备的连接对象。
     * @param autoConnect 是否自动重连
     * @date 2015-6-15上午10:18:17
     */
    public synchronized void connectToBluetoothDevice(final BluetoothDev device, final boolean autoConnect) {
        CKLOG.Debug(TAG, "in " + AliBluetoothManager.class.getSimpleName() + " connectToBluetoothDevice...");

//        synchronized (mConnStateLock) {
            if (mConnStateMap.containsKey(device.getAddress()) && (Integer.valueOf(mConnStateMap.get(device.getAddress())) == STATE_CONNECTING)) {
                CKLOG.Debug(TAG, "connectToBLEDevice() : device address - " + device.getAddress() + " , is already doing the connection....waiting for callback...return");
                return;
            }
            if (mConnStateMap.containsKey(device.getAddress()) && (Integer.valueOf(mConnStateMap.get(device.getAddress())) == STATE_CONNECTED)) {
                CKLOG.Debug(TAG, "connectToBLEDevice() : device address - " + device.getAddress() + " , is already connected....no need to connect again...return");
                return;
            }
//        }

        switch (device.getDeviceType()) {
            case BLE_DEVICE_Direct:
            case BLE_DEVICE_Secondary:
                if (null == mBLEServiceBinder) {
                    CKLOG.Info(TAG, "connectToBluetoothDevice() : mBLEServiceBinder is null, return.");
                    return;
                }

                if (!BluetoothAdapter.checkBluetoothAddress(device.getAddress())) {
                    CKLOG.Info(TAG, "connectToBluetoothDevice() : device address not valid, return.");
                    return;
                }

                CKLOG.Debug(TAG, "connectToBluetoothDevice() : device address - " + device.getAddress() + " , autoConnect - " + autoConnect);
                // mDeviceConnectionContainer.put(bleDevice.getAddress(), bleDevice);
                mBLEServiceBinder.connectToBLEDeviceByAddress(device.getAddress(), autoConnect);
                synchronized (mConnectBLEDevicesListenerLock) {
                    mTmpContainer.put(device.getAddress(), device);
                }
                break;
            case BT_DEVICE_Direct:
            case BT_DEVICE_Secondary:
                if (null == mBTServiceBinder) {
                    CKLOG.Info(TAG, "connectToBluetoothDevice() : mBTServiceBinder is null, return.");
                    return;
                }

                if (!BluetoothAdapter.checkBluetoothAddress(device.getAddress())) {
                    CKLOG.Info(TAG, "connectToBluetoothDevice() : device address not valid, return.");
                    return;
                }

                CKLOG.Debug(TAG, "connectToBluetoothDevice() : device address - " + device.getAddress() + " , autoConnect - " + autoConnect);
                // mDeviceConnectionContainer.put(bleDevice.getAddress(), bleDevice);
                mBTServiceBinder.connectToBLEDeviceByAddress(device.getAddress(), autoConnect);
                synchronized (mConnectBLEDevicesListenerLock) {
                    mTmpContainer.put(device.getAddress(), device);
                }
                break;
            default:
                CKLOG.Error(TAG, "connectToBluetoothDevice error ,run to default branch...device type is : " + device.getDeviceType());
                break;
        }
    }

    /**
     * 断开设备连接。
     *
     * @param device
     * @date 2015-6-15上午10:20:30
     */
    public synchronized void disconnectFromBLEDevice(final BluetoothDev device) {

        CKLOG.Debug(TAG, "in " + AliBluetoothManager.class.getSimpleName() + " disconnectFromBLEDevice...");

        switch (device.getDeviceType()) {
            case BLE_DEVICE_Direct:
            case BLE_DEVICE_Secondary:
                if (null == mBLEServiceBinder) {
                    CKLOG.Info(TAG, "disconnectFromBLEDevice() : mServiceBinder is null, return.");
                    return;
                }
                if (null == device) {
                    CKLOG.Info(TAG, "disconnectFromBLEDevice() : device is null, return.");
                    return;
                }
                CKLOG.Info(TAG, "disconnectFromBLEDevice() ... ");
                if (device != null && !TextUtils.isEmpty(device.getAddress())) {
                    mDeviceConnectionContainer.remove(device.getAddress());
                    mBLEServiceBinder.disconnectFromBLEDevice(device.getAddress());
                }
                break;
            case BT_DEVICE_Direct:
            case BT_DEVICE_Secondary:
                if (null == mBTServiceBinder) {
                    CKLOG.Info(TAG, "disconnectFromBLEDevice() : mServiceBinder is null, return.");
                    return;
                }
                if (null == device) {
                    CKLOG.Info(TAG, "disconnectFromBLEDevice() : device is null, return.");
                    return;
                }
                CKLOG.Info(TAG, "disconnectFromBLEDevice() ... ");
                if (device != null && !TextUtils.isEmpty(device.getAddress())) {
                    mDeviceConnectionContainer.remove(device.getAddress());
                    mBTServiceBinder.disconnectFromBLEDevice(device.getAddress());
                }
                break;
        }
    }

    private synchronized void onSendMessageCompletedEx(final String address, final String message) {
        synchronized (mBLENotificationListenerLock) {
            CKLOG.Debug(TAG, "onSendMessageCompletedEx() : device address - " + address + " , message - " + message);
            IAliBLESendStateCallback callback = IAliBLESendStateCallbackMap.get(address);
            if (callback != null) {
                callback.onSendMessageCompleted(address, message);
            } else {
                CKLOG.Error(TAG, "onSendMessageCompletedEx() callback is null...");
            }
        }
    }

    private synchronized void onSendMessageFailedEx(final String address, final String message, final int failCode) {
        synchronized (mBLENotificationListenerLock) {
            CKLOG.Debug(TAG, "onSendMessageFailedEx() : device address - " + address + " , message - " + message
                    + " , fail code - " + failCode);

            IAliBLESendStateCallback callback = IAliBLESendStateCallbackMap.get(address);
            if (callback != null) {
                callback.onSendMessageFailed(address, message, failCode);
            } else {
                CKLOG.Error(TAG, "onSendMessageFailedEx() callback is null...");
            }
        }
    }

    private synchronized void onReceiveMessageEx(final BluetoothDevice device, final byte[] content) {
        synchronized (mBLENotificationListenerLock) {
            CKLOG.Debug(TAG, "onReceiveMessageEx() : device address - " + device.getAddress() + " , content - " + content);
//            for (BLENotificationListener listener : mBLENotificationListeners) {
//                if (null != listener) {
//                    listener.onReceiveNotificationInternal(device, content);
//                }
//            }
            //mRemoteBLENotificationCallback.onReceiveMessage(device,content);
//            IAliBLESendStateCallback callback = IAliBLESendStateCallbackMap.remove(device.getAddress());
//            if (callback != null) {
//                callback.onReceiveMessage(device, content);
//            } else {
//                CKLOG.Error(TAG, "onReceiveMessageEx() callback is null...");
//            }
        }
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public boolean registerCharacteristicListener(final BLECharacteristicListener listener) {
        synchronized (mBLECharacteristicListenerLock) {
            if (null == listener) {
                CKLOG.Info(TAG, "registerCharacteristicListener() : listener is null.");
                return false;
            }
            mBLECharacteristicListeners.add(listener);
            return true;
        }
    }

    public boolean unregisterCharacteristicListener(final BLECharacteristicListener listener) {
        synchronized (mBLECharacteristicListenerLock) {
            return mBLECharacteristicListeners.remove(listener);
        }
    }

    public void unregisterAllCharacteristicListeners() {
        synchronized (mBLECharacteristicListenerLock) {
            mBLECharacteristicListeners.clear();
        }
    }

    private void onCharacteristicReadEx(final BluetoothDevice device, final UUID serviceUuid, final UUID characteristicUuid,
                                        final byte[] characteristicValue, final int status) {
        synchronized (mBLECharacteristicListenerLock) {
            CKLOG.Debug(TAG, "onCharacteristicReadEx() : device address - " + device.getAddress() + " , service uuid - " + serviceUuid
                    + " , characteristic uuid - " + characteristicUuid + " , characteristic content - " + new String(characteristicValue)
                    + " , status - " + status);

            for (BLECharacteristicListener listener : mBLECharacteristicListeners) {
                if (null != listener) {
                    listener.onCharacteristicReadInternal(device, serviceUuid, characteristicUuid, characteristicValue, status);
                }
            }
        }
    }

    private void onCharacteristicWriteEx(final BluetoothDevice device, final UUID serviceUuid, final UUID characteristicUuid,
                                         final byte[] characteristicValue, final int status) {
        synchronized (mBLECharacteristicListenerLock) {
            CKLOG.Debug(TAG, "onCharacteristicWriteEx() : device address - " + device.getAddress() + " , service uuid - " + serviceUuid
                    + " , characteristic uuid - " + characteristicUuid + " , characteristic content - " + new String(characteristicValue)
                    + " , status - " + status);

            for (BLECharacteristicListener listener : mBLECharacteristicListeners) {
                if (null != listener) {
                    listener.onCharacteristicWriteInternal(device, serviceUuid, characteristicUuid, characteristicValue, status);
                }
            }
        }
    }

    private void onCharacteristicChangedEx(final BluetoothDevice device, final UUID serviceUuid, final UUID characteristicUuid,
                                           final byte[] characteristicValue) {
        String str = new String(characteristicValue);
        try {
            JSONObject msg = new JSONObject(str);
            CKLOG.Debug(TAG, "receive data from bluetooth device , address : " + device.getAddress() + ", data : " + msg);
            BluetoothDev dev = getDevice(device.getAddress());
            dev.onReceiveData(msg.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            CKLOG.Debug(TAG, "JSONException:" + e.toString());
        }
        CKLOG.Debug(TAG, "onCharacteristicChangedEx-->originalData-->" + str);
    }

    HashMap<String, IAliBLESendStateCallback> IAliBLESendStateCallbackMap = new HashMap<String, IAliBLESendStateCallback>();

    public void sendNotificationMessageToBLEDevice(final BluetoothDev device, final JSONObject message,
                                                   final IAliBLESendStateCallback remoteBLENotificationCallback) {
        if (null == device || message == null || TextUtils.isEmpty(message.toString())) {
            CKLOG.Info(TAG, "sendNotificationMessageToBLEDevice() : parameter null or empty, return.");
            remoteBLENotificationCallback.onSendMessageFailed(device.getAddress(), message.toString(), IAliBLESendStateCallback.FAIL_CODE_SENDING_NULL_CONTENT);
            return;
        }
        CKLOG.Debug(TAG, "sendNotificationMessageToBLEDevice() : device address - " + device.getAddress() + " , notificationContent - "
                + message.toString());
        switch (device.getDeviceType()) {
            case BLE_DEVICE_Direct:
            case BLE_DEVICE_Secondary:
                if (null == mBLEServiceBinder) {
                    CKLOG.Info(TAG, "getConnectedBLEDevices() : mServiceBinder is null, return null.");
                    return;
                }
                mBLEServiceBinder.sendMessgeToBluetoothDevice(device.getAddress(), message.toString(), remoteBLENotificationCallback);
                break;
            case BT_DEVICE_Direct:
            case BT_DEVICE_Secondary:
                if (null == mBTServiceBinder) {
                    CKLOG.Info(TAG, "getConnectedBLEDevices() : mServiceBinder is null, return null.");
                    return;
                }
                IAliBLESendStateCallbackMap.put(device.getAddress(), remoteBLENotificationCallback);
                mBTServiceBinder.sendMessgeToBluetoothDevice(device.getAddress(), message.toString(), remoteBLENotificationCallback);
                break;
        }
    }

    public synchronized List<BluetoothDev> getConnectedBluetoothDevices() {
        return new ArrayList<BluetoothDev>(mDeviceConnectionContainer.values());
    }

    public synchronized List<BluetoothDev> getConnectingBluetoothDevices() {
        return new ArrayList<BluetoothDev>(mTmpContainer.values());
    }

    public synchronized List<BluetoothDevice> getConnectedBLEDevices(AccessType type) {
        switch (type) {
            case BLE_DEVICE_Direct:
            case BLE_DEVICE_Secondary:
                if (null == mBLEServiceBinder) {
                    CKLOG.Info(TAG, "getConnectedBLEDevices() : mServiceBinder is null, return null.");
                    return null;
                }
                CKLOG.Info(TAG, "getConnectedBLEDevices() ... ");
                List<BluetoothDevice> bleRet = null;
                bleRet = mBLEServiceBinder.getConnectedBLEDevices();
                return bleRet;
            case BT_DEVICE_Direct:
            case BT_DEVICE_Secondary:
                if (null == mBTServiceBinder) {
                    CKLOG.Info(TAG, "getConnectedBLEDevices() : mServiceBinder is null, return null.");
                    return null;
                }
                CKLOG.Info(TAG, "getConnectedBLEDevices() ... ");
                List<BluetoothDevice> btRet = null;
                btRet = mBTServiceBinder.getConnectedBLEDevices();
                return btRet;
            default:
                CKLOG.Error("getConnectedBLEDevices error...run to default branch...");
                return null;
        }
    }

    public synchronized BluetoothDev getDevice(String address) {
        CKLOG.Info(TAG, "getDevice() ... ");
        BluetoothDev dev = mDeviceConnectionContainer.get(address);
        if (dev == null) {
            dev = mTmpContainer.get(address);
        }
        return dev;
    }

    public synchronized BLEDeviceType getBLEDeviceType(final BluetoothDevice device) {
        return BLEUtils.getBLEDeviceType(device);
    }

    public synchronized boolean registerBLECharacteristicListener() {
        if (null == mBLEServiceBinder) {
            CKLOG.Info(TAG, "registerBLECharacteristicListener() : mBTServiceBinder null, return false.");
            return false;
        }
        CKLOG.Info(TAG, "registerBLECharacteristicListener() ... ");
        mBLEServiceBinder.registerBLECharacteristicCallback(mRemoteBLECharacteristicCallback, StringUtils.getCurProcessName(mContext));
        return true;
    }


    public synchronized boolean registerBTCharacteristicListener() {
        if (null == mBTServiceBinder) {
            CKLOG.Info(TAG, "registerBTCharacteristicListener() : mBTServiceBinder null, return false.");
            return false;
        }
        CKLOG.Info(TAG, "registerBTCharacteristicListener() ... ");
        mBTServiceBinder.registerBLECharacteristicCallback(mRemoteBLECharacteristicCallback, StringUtils.getCurProcessName(mContext));
        return true;
    }

    public synchronized boolean unregisterBTCharacteristicListener(final ArrayList<String> characteristicUuids) {

        if (null == mBTServiceBinder) {
            CKLOG.Info(TAG, "unregisterCharacteristicListener() : mServiceBinder null, return false.");
            return false;
        }
        if (null == characteristicUuids || characteristicUuids.isEmpty()) {
            CKLOG.Info(TAG, "unregisterCharacteristicListener() : characteristicUuids null or empty, return false.");
            return false;
        }
        final ArrayList<String> list = new ArrayList<String>();
        for (String uuid : characteristicUuids) {
            if (null != uuid) {
                list.add(uuid.toString());
            }
        }
        if (list.isEmpty()) {
            CKLOG.Info(TAG, "unregisterCharacteristicListener() : no valid uuid , return false.");
            return false;
        }
        CKLOG.Info(TAG, "unregisterCharacteristicListener() ... ");
        mBTServiceBinder.unregisterBLECharacteristicCallback(mRemoteBLECharacteristicCallback, list, mContext.getPackageName());
        return true;
    }

    public synchronized void unregisterBLECharacteristicListener(final ArrayList<String> characteristicUuids) {

        if (null == mBLEServiceBinder) {
            CKLOG.Info(TAG, "unregisterCharacteristicListener() : mServiceBinder null, return false.");
            return;
        }
        if (null == characteristicUuids || characteristicUuids.isEmpty()) {
            CKLOG.Info(TAG, "unregisterCharacteristicListener() : characteristicUuids null or empty, return false.");
            return;
        }
        final ArrayList<String> list = new ArrayList<String>();
        for (String uuid : characteristicUuids) {
            if (null != uuid) {
                list.add(uuid);
            }
        }
        if (list.isEmpty()) {
            CKLOG.Info(TAG, "unregisterCharacteristicListener() : no valid uuid , return false.");
            return;
        }
        CKLOG.Info(TAG, "unregisterCharacteristicListener() ... ");
        mBLEServiceBinder.unregisterBLECharacteristicCallback(mRemoteBLECharacteristicCallback, list, mContext.getPackageName());
    }

    public synchronized void registerNotifications(final ArrayList<String> catigorys, IAliBLESendStateCallback callback) {
        if (null == mBLEServiceBinder) {
            CKLOG.Info(TAG, "registerNotifications() : mServiceBinder null, return.");
            return;
        }
        if (null == catigorys || catigorys.isEmpty()) {
            CKLOG.Info(TAG, "registerNotifications() : catigorys null or empty, return.");
            return;
        }
        CKLOG.Info(TAG, "registerNotifications() ... ");
        mBLEServiceBinder.registerBLENotificationCallback(callback, catigorys, mContext.getPackageName());
    }
//
//    public synchronized boolean unregisterNotifications(final ArrayList<String> catigorys, IAliBLESendStateCallback callback) {
//        if (null == mServiceBinder) {
//            CKLOG.Info(TAG, "unregisterNotifications() : mServiceBinder null, return false.");
//            return false;
//        }
//        if (null == catigorys || catigorys.isEmpty()) {
//            CKLOG.Info(TAG, "unregisterNotifications() : catigorys null or empty, return false.");
//            return false;
//        }
//
//        CKLOG.Info(TAG, "unregisterNotifications() ... ");
//        mServiceBinder.unregisterBLENotificationCallback(callback, catigorys, mContext.getPackageName());
//        return true;
//    }

    //    /**
//     * 获取扫描到的手表类BLE设备。
//     *
//     * @return
//     * @date 2015-6-15上午10:14:58
//     */
//
//    public synchronized List<BluetoothDev> getScannedBLEWatches() {
//        if (null == mServiceBinder) {
//            CKLOG.Info(TAG, "getScannedBLEWatches() :  mServiceBinder is null , return null.");
//            return null;
//        }
//        CKLOG.Info(TAG, "getScannedBLEWatches() ... ");
//        List<BluetoothDev> ret = null;
//        ret = mServiceBinder.getScannedBLEWatches();
//        return ret;
//    }


//    public synchronized List<BluetoothDev> getConnectedBLEWatches() {
//        if (null == mServiceBinder) {
//            CKLOG.Info(TAG, "getConnectedBLEWatches() : mServiceBinder is null, return null.");
//            return null;
//        }
//        CKLOG.Info(TAG, "getConnectedBLEWatches() ... ");
//        List<BluetoothDev> ret = null;
//        ret = mServiceBinder.getConnectedBLEWatches();
//        return ret;
//    }

//    public void writeCharacteristicToBLEDevice(final BluetoothDev device, final UUID serviceUuid, final UUID characteristicUuid,
//                                               final String characteristicValue) {
//        postRunnableToWorkThread(new Runnable() {
//
//            @Override
//            public void run() {
//                if (null == mServiceBinder) {
//                    CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : string value, mServiceBinder is null, return.");
//                    return;
//                }
//                if (null == device || null == serviceUuid || null == characteristicUuid || TextUtils.isEmpty(characteristicValue)) {
//                    CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : string value, parameter null or empty, return.");
//                    return;
//                }
//                CKLOG.Debug(TAG, "writeCharacteristicToBLEDevice() : string value, device address - " + device.getAddress() + " , serviceUuid - "
//                        + serviceUuid + " , characteristicUuid - " + characteristicUuid + " , characteristicValue - " + characteristicValue);
//                mServiceBinder.writeCharacteristicToBLEDeviceString(device, serviceUuid.toString(), characteristicUuid.toString(),
//                        characteristicValue);
//            }
//        });
//    }

//    public void writeCharacteristicToBLEDevice(final BluetoothDev device, final UUID serviceUuid, final UUID characteristicUuid,
//                                               final byte[] characteristicValue) {
//        postRunnableToWorkThread(new Runnable() {
//
//            @Override
//            public void run() {
//                if (null == mServiceBinder) {
//                    CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : bytes value , mServiceBinder is null, return.");
//                    return;
//                }
//                if (null == device || null == serviceUuid || null == characteristicUuid || null == characteristicValue) {
//                    CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : bytes value , parameter null or empty, return.");
//                    return;
//                }
//                CKLOG.Debug(TAG, "writeCharacteristicToBLEDevice() : bytes value , device address - " + device.getAddress() + " , serviceUuid - "
//                        + serviceUuid + " , characteristicUuid - " + characteristicUuid + " , characteristicValue - " + characteristicValue);
//                mServiceBinder.writeCharacteristicToBLEDeviceBytes(device, serviceUuid.toString(), characteristicUuid.toString(),
//                        characteristicValue);
//            }
//        });
//    }

//    public void readCharacteristicToBLEDevice(final BluetoothDev device, final UUID serviceUuid, final UUID characteristicUuid) {
//        postRunnableToWorkThread(new Runnable() {
//
//            @Override
//            public void run() {
//                if (null == mServiceBinder) {
//                    CKLOG.Info(TAG, "readCharacteristicToBLEDevice() : mServiceBinder is null, return.");
//                    return;
//                }
//                if (null == device || null == serviceUuid || null == characteristicUuid) {
//                    CKLOG.Info(TAG, "readCharacteristicToBLEDevice() : parameter null, return.");
//                    return;
//                }
//                CKLOG.Debug(TAG, "readCharacteristicToBLEDevice() : device address - " + device.getAddress() + " , serviceUuid - " + serviceUuid
//                        + " , characteristicUuid - " + characteristicUuid);
//                mServiceBinder.readCharacteristicToBLEDevice(device, serviceUuid.toString(), characteristicUuid.toString());
//
//            }
//        });
//    }

    //    public boolean registerNotificationListener(final BLENotificationListener listener) {
//        synchronized (mBLENotificationListenerLock) {
//            if (null == listener) {
//                CKLOG.Info(TAG, "registerNotificationListener() : listener is null.");
//                return false;
//            }
//            mBLENotificationListeners.add(listener);
//            return true;
//        }
//    }

//    public boolean unregisterNotificationListener(final BLENotificationListener listener) {
//        synchronized (mBLENotificationListenerLock) {
//            return mBLENotificationListeners.remove(listener);
//        }
//    }
//
//    public void unregisterAllNotificationListeners() {
//        postRunnableToWorkThread(new Runnable() {
//            @Override
//            public void run() {
//                synchronized (mBLENotificationListenerLock) {
//                    mBLENotificationListeners.clear();
//                }
//            }
//        });
//    }


    //    public void registerBLEConnectCallback(IAliBLEConnectCallback connectCallback) {
//        if (mServiceBinder != null) {
//            mServiceBinder.registerBLEConnectCallback(connectCallback);
//        }
//    }
//
//    public void unRegisterBLEConnectCallback(IAliBLEConnectCallback connectCallback) {
//        if (mServiceBinder != null) {
//            mServiceBinder.unregisterBLEConnectCallback(connectCallback);
//        }
//    }


    //    public synchronized boolean setBLEDeviceCharacteristicNotification(final BluetoothDev device, final UUID serviceUuid,
//                                                                       final UUID characteristicUuid, final boolean enable) {
//        if (null == mServiceBinder) {
//            CKLOG.Info(TAG, "setBLEDeviceCharacteristicNotification() : mServiceBinder is null, return false.");
//            return false;
//        }
//        if (null == device) {
//            CKLOG.Info(TAG, "setBLEDeviceCharacteristicNotification() : device null , return false.");
//            return false;
//        }
//        if (null == serviceUuid || null == characteristicUuid) {
//            CKLOG.Info(TAG, "setBLEDeviceCharacteristicNotification() : uuid invalid , return false.");
//            return false;
//        }
//        CKLOG.Debug(TAG,
//                "setBLEDeviceCharacteristicNotification() : device address - " + device.getAddress() + " , service uuid - " + serviceUuid.toString()
//                        + " , characteristic uuid - " + characteristicUuid.toString() + " , enable - " + enable);
//        return mServiceBinder.setBLEDeviceCharacteristicNotification(device, serviceUuid.toString(), characteristicUuid.toString(), enable);
//    }
}
