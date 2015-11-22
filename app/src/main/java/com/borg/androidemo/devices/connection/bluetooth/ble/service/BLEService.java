package com.borg.androidemo.devices.connection.bluetooth.ble.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.borg.androidemo.common.utils.CKLOG;
import com.borg.androidemo.devices.connection.bluetooth.AliBluetoothManager;
import com.borg.androidemo.devices.connection.bluetooth.ble.callback.IAliBLECharacteristicCallback;
import com.borg.androidemo.devices.connection.bluetooth.ble.callback.IAliBLEConnectCallback;
import com.borg.androidemo.devices.connection.bluetooth.ble.callback.IAliBLEScanCallback;
import com.borg.androidemo.devices.connection.bluetooth.ble.callback.IAliBLESendStateCallback;
import com.borg.androidemo.devices.connection.bluetooth.ble.callback.IAliBLEServiceBinder;
import com.borg.androidemo.devices.connection.bluetooth.ble.service.ble.BLEManager;
import com.borg.androidemo.devices.connection.bluetooth.ble.service.characteristic.CharacteristicMsgObject;
import com.borg.androidemo.devices.connection.bluetooth.ble.uuid.AliBLEUuid;
import com.borg.androidemo.devices.device.bluetoothdevice.BluetoothDev;
import com.borg.androidemo.devices.protocol.JsonProtocolConstant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class BLEService extends Service {

    private static final String TAG = "BLEService";
    private BLEManager mBLEManager;
    private BroadcastReceiver mBluetoothReceiver;

    public static final int INTERNAL_COMMAND_SCANNED_BLE_UPDATED = 1001;
    public static final int INTERNAL_COMMAND_SCAN_BLE_START = 1002;
    public static final int INTERNAL_COMMAND_SCAN_BLE_STOP = 1003;
    public static final int INTERNAL_COMMAND_BLE_CONNECTED = 1004;
    public static final int INTERNAL_COMMAND_BLE_DISCONNECTED = 1005;
    public static final int INTERNAL_COMMAND_BLE_CONNECTING = 1006;
    public static final int INTERNAL_COMMAND_ON_CHARACTERISTIC_READ = 1007;
    public static final int INTERNAL_COMMAND_ON_CHARACTERISTIC_WRITE = 1008;
    public static final int INTERNAL_COMMAND_ON_CHARACTERISTIC_CHANGED = 1009;
    public static final int INTERNAL_COMMAND_ON_NOTIFICATION_FEEDBACK = 1010;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INTERNAL_COMMAND_SCANNED_BLE_UPDATED:
                    onScannedBLEDevicesUpdatedEx();
                    break;
                case INTERNAL_COMMAND_SCAN_BLE_START:
                    onScanStartEx();
                    break;
                case INTERNAL_COMMAND_SCAN_BLE_STOP:
                    onScanStopEx();
                    break;
                case INTERNAL_COMMAND_BLE_CONNECTED:
                    onBLEConnectedEx(msg);
                    break;
                case INTERNAL_COMMAND_BLE_DISCONNECTED:
                    onBLEDisonnectedEx(msg);
                    break;
                case INTERNAL_COMMAND_BLE_CONNECTING:
                    onBLEConnectingEx(msg);
                    break;
                case INTERNAL_COMMAND_ON_CHARACTERISTIC_READ:
                    onCharacteristicReadEx(msg);
                    break;
                case INTERNAL_COMMAND_ON_CHARACTERISTIC_WRITE:
                    onCharacteristicWriteEx(msg);
                    break;
                case INTERNAL_COMMAND_ON_CHARACTERISTIC_CHANGED:
                    onCharacteristicChangedEx(msg);
                    break;
                case INTERNAL_COMMAND_ON_NOTIFICATION_FEEDBACK:
                    onNotificationFeedbackEx(msg);
                    break;
                default:
                    break;
            }
        }
    };

    private BLEServiceBinder mBinder = new BLEServiceBinder();

    public class BLEServiceBinder extends Binder implements IAliBLEServiceBinder {


        @Override
        public void sayHi(final String words) {
            sayHiEx(words);
        }

        @Override
        public void scanBLEDevices() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBLEManager.scanBLEDevices();
                }
            });
        }

        @Override
        public void stopScanBLEDevices() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBLEManager.stopScanBLEDevices();
                }
            });
        }

        @Override
        public void registerBLEScanCallback(final IAliBLEScanCallback callback) {
            registerBLEScanCallbackEx(callback);
        }

        @Override
        public void unregisterBLEScanCallback(final IAliBLEScanCallback callback) {
            unregisterBLEScanCallbackEx(callback);
        }

        @Override
        public void registerBLEConnectCallback(
                final IAliBLEConnectCallback callback) {
            registerBLEConnectCallbackEx(callback);
        }

        @Override
        public void unregisterBLEConnectCallback(
                final String addr) {
            unregisterBLEConnectCallbackEx(addr);
        }

        @Override
        public void connectToBLEDeviceByAddress(final String deviceAddress,
                                                final boolean autoConnect) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBLEManager.connectToBLEDevice(deviceAddress, autoConnect);
                }
            });
        }

        @Override
        public void disconnectFromBLEDevice(final String deviceAddress) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBLEManager.disconnectFromBLEDevice(deviceAddress);
                }
            });
        }

        @Override
        public List<BluetoothDevice> getScannedBLEDevices() {
            return mBLEManager.getScannedBLEDevices();
        }

        @Override
        public List<BluetoothDevice> getScannedBLEWatches() {
            return mBLEManager.getScannedBLEWatches();
        }

        @Override
        public List<BluetoothDevice> getConnectedBLEDevices() {
            return mBLEManager.getConnectedBLEDevices();
        }

        @Override
        public List<BluetoothDevice> getConnectedBLEWatches() {
            return mBLEManager.getConnectedBLEWatches();
        }

        @Override
        public void sendMessgeToBluetoothDevice(final String address,
                                                final String notification,
                                                final IAliBLESendStateCallback callback) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBLEManager.sendNotificationToBLEDevice(address,
                            AliBLEUuid.NOTIFICATION_SERVICE,
                            AliBLEUuid.NOTIFICATION_CHARACTERISTIC,
                            notification, callback);
                }
            });
        }

        @Override
        public void readCharacteristicToBLEDevice(final BluetoothDevice device,
                                                  final String serviceUuid, final String characteristicUuid) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBLEManager.readCharacteristicToBLEDevice(device,
                            serviceUuid, characteristicUuid);
                }
            });
        }

        @Override
        public void writeCharacteristicToBLEDeviceString(
                final BluetoothDevice device, final String serviceUuid,
                final String characteristicUuid, final String content) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBLEManager.writeCharacteristicToBLEDevice(device,
                            serviceUuid, characteristicUuid, content);
                }
            });
        }

        @Override
        public void writeCharacteristicToBLEDeviceBytes(
                final BluetoothDevice device, final String serviceUuid,
                final String characteristicUuid, final byte[] content) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBLEManager.writeCharacteristicToBLEDevice(device,
                            serviceUuid, characteristicUuid, content);
                }
            });
        }

        @Override
        public void registerBLECharacteristicCallback(
                IAliBLECharacteristicCallback callback, String packageName) {
            registerBLECharacteristicCallbackEx(callback,
                    packageName);
        }

        @Override
        public void unregisterBLECharacteristicCallback(
                IAliBLECharacteristicCallback callback,
                List<String> characteristicUuids, String packageName) {
            unregisterBLECharacteristicCallbackEx(callback,
                    characteristicUuids, packageName);
        }

        @Override
        public void clearBLECharacteristicCallback(String packageName) {
            clearBLECharacteristicCallbackEx(packageName);
        }

        @Override
        public void registerBLENotificationCallback(
                IAliBLESendStateCallback callback, List<String> ids,
                String packageName) {
            registerBLENotificationCallbackEx(callback, ids, packageName);
        }

        @Override
        public void unregisterBLENotificationCallback(
                IAliBLESendStateCallback callback, List<String> ids,
                String packageName) {
            unregisterBLENotificationCallbackEx(callback, ids, packageName);
        }

        @Override
        public void clearBLENotificationCallback(String packageName) {
            clearBLENotificationCallbackEx(packageName);
        }

        @Override
        public boolean setBLEDeviceCharacteristicNotification(
                BluetoothDevice device, String serviceUuid,
                String characteristicUuid, boolean enable) {
            return mBLEManager.setBLEDeviceCharacteristicNotification(device,
                    serviceUuid, characteristicUuid, enable);
        }
    }

    private Object mAliBLECharacteristicCallbackLock = new Object();
    private HashMap<String, IAliBLECharacteristicCallback> mAliBLECharacteristicCallbacks = new HashMap<String, IAliBLECharacteristicCallback>();

    private void onCharacteristicReadEx(final Message msg) {
        synchronized (mAliBLECharacteristicCallbackLock) {
            final CharacteristicMsgObject characteristicMsgObject = (CharacteristicMsgObject) msg.obj;
            ArrayList<IAliBLECharacteristicCallback> list = new ArrayList<IAliBLECharacteristicCallback>(mAliBLECharacteristicCallbacks.values());
            if (null == list || list.isEmpty()) {
                CKLOG.Info(TAG,
                        "onCharacteristicReadEx() : no callback for this characteristic , return.");
                return;
            }
            CKLOG.Info(TAG, "onCharacteristicReadEx() ... ");
            for (IAliBLECharacteristicCallback callback : list) {
                try {
                    callback.onCharacteristicRead(
                            characteristicMsgObject.device,
                            characteristicMsgObject.serviceUuid,
                            characteristicMsgObject.characteristicUuid,
                            characteristicMsgObject.characteristicValue,
                            characteristicMsgObject.status);
                } catch (Exception e) {
                    CKLOG.Error(TAG,
                            "onCharacteristicReadEx() : exception - "
                                    + e.getMessage());
                }
            }
        }
    }

    private void onCharacteristicWriteEx(final Message msg) {
        synchronized (mAliBLECharacteristicCallbackLock) {
            final CharacteristicMsgObject characteristicMsgObject = (CharacteristicMsgObject) msg.obj;
//            ArrayList<IAliBLECharacteristicCallback> list = mAliBLECharacteristicCallbacksMap
//                    .get(characteristicMsgObject.characteristicUuid);
            ArrayList<IAliBLECharacteristicCallback> list = new ArrayList<IAliBLECharacteristicCallback>(mAliBLECharacteristicCallbacks.values());
            if (null == list || list.isEmpty()) {
                CKLOG.Info(TAG,
                        "onCharacteristicWriteEx() : no callback for this characteristic , return.");
                return;
            }
            CKLOG.Info(TAG, "onCharacteristicWriteEx() ... ");
            for (IAliBLECharacteristicCallback callback : list) {
                try {
                    callback.onCharacteristicWrite(
                            characteristicMsgObject.device,
                            characteristicMsgObject.serviceUuid,
                            characteristicMsgObject.characteristicUuid,
                            characteristicMsgObject.characteristicValue,
                            characteristicMsgObject.status);
                } catch (Exception e) {
                    CKLOG.Error(TAG,
                            "onCharacteristicWriteEx() : exception - "
                                    + e.getMessage());
                }
            }
        }
    }

    private void onCharacteristicChangedEx(final Message msg) {
        synchronized (mAliBLECharacteristicCallbackLock) {
            final CharacteristicMsgObject characteristicMsgObject = (CharacteristicMsgObject) msg.obj;

            ArrayList<IAliBLECharacteristicCallback> list = new ArrayList<IAliBLECharacteristicCallback>(mAliBLECharacteristicCallbacks.values());
            if (null == list || list.isEmpty()) {
                CKLOG.Info(TAG,
                        "onCharacteristicChangedEx() : no callback for this characteristic , return.");
                return;
            }
            CKLOG.Info(TAG, "onCharacteristicChangedEx() ... ");
            for (IAliBLECharacteristicCallback callback : list) {
                try {
                    String info = new String(
                            characteristicMsgObject.characteristicValue);
                    CKLOG.Debug(TAG, info);
                    String jsonString = "{\""
                            + JsonProtocolConstant.JSON_CATIGORY + "\":"
                            + characteristicMsgObject.clientId + ",\""
                            + JsonProtocolConstant.JSON_CONTENT + "\":" + info
                            + ",\"" + JsonProtocolConstant.JSON_TRANSACT_ID
                            + "\":" + characteristicMsgObject.seqId + "}";
                    CKLOG.Debug(TAG, "full json:" + jsonString);
                    callback.onCharacteristicChanged(
                            characteristicMsgObject.device,
                            characteristicMsgObject.serviceUuid,
                            characteristicMsgObject.characteristicUuid,
                            // TODO 这里只能使用字符串形式，使用jsonbject会解析出错
                            jsonString.getBytes());
                } catch (Exception e) {
                    CKLOG.Error(TAG,
                            "onCharacteristicChangedEx() : exception - "
                                    + e.getMessage());
                }
            }
        }
    }

    private void registerBLECharacteristicCallbackEx(
            final IAliBLECharacteristicCallback callback, final String packageName) {
        synchronized (mAliBLECharacteristicCallbackLock) {
            if (null == callback) {
                CKLOG.Info(TAG,
                        "registerBLECharacteristicCallbackEx() : callback null , return.");
                return;
            }
            if (TextUtils.isEmpty(packageName)) {
                CKLOG.Info(TAG,
                        "registerBLECharacteristicCallbackEx() : package name empty , return.");
                return;
            }
            CKLOG.Info(TAG, "registerBLECharacteristicCallbackEx() ... ");
            if (!mAliBLECharacteristicCallbacks.containsKey(packageName)) {
                mAliBLECharacteristicCallbacks.put(packageName, callback);
            }
        }
    }

    private void unregisterBLECharacteristicCallbackEx(
            final IAliBLECharacteristicCallback callback,
            final List<String> characteristicUuids, final String packageName) {
        synchronized (mAliBLECharacteristicCallbackLock) {
            if (null == callback) {
                CKLOG.Info(TAG,
                        "unregisterBLECharacteristicCallbackEx() : callback null , return.");
                return;
            }
            if (null == characteristicUuids || characteristicUuids.isEmpty()) {
                CKLOG.Info(TAG,
                        "unregisterBLECharacteristicCallbackEx() : characteristicUuids null or empty , return.");
                return;
            }
            CKLOG.Info(TAG, "unregisterBLECharacteristicCallbackEx() ... ");

            mAliBLECharacteristicCallbacks.remove(callback);
        }
    }

    private void clearBLECharacteristicCallbackEx(final String packageName) {
        synchronized (mAliBLECharacteristicCallbackLock) {
            if (TextUtils.isEmpty(packageName)) {
                CKLOG.Info(TAG,
                        "clearBLECharacteristicCallbackEx() : package name empty , return.");
                return;
            }
            CKLOG.Info(TAG, "clearBLECharacteristicCallbackEx() ... ");
            final IAliBLECharacteristicCallback cb = mAliBLECharacteristicCallbacks
                    .remove(packageName);
            if (null == cb) {
                return;
            }
        }
    }

    private void unregisterAllBLECharacteristicCallbacks() {
        mAliBLECharacteristicCallbacks.clear();
    }

    private Object mAliBLENotificationCallbackLock = new Object();
    private HashMap<String, IAliBLESendStateCallback> mAliBLENotificationCallbacks = new HashMap<String, IAliBLESendStateCallback>();
    private HashMap<String, ArrayList<IAliBLESendStateCallback>> mAliBLENotificationCallbacksMap = new HashMap<String, ArrayList<IAliBLESendStateCallback>>();

    private void onNotificationFeedbackEx(final Message msg) {
        synchronized (mAliBLENotificationCallbackLock) {
            final CharacteristicMsgObject characteristicMsgObject = (CharacteristicMsgObject) msg.obj;
            if (null == characteristicMsgObject.characteristicValue
                    || characteristicMsgObject.characteristicValue.length < 1) {
                CKLOG.Info(TAG,
                        "onNotificationFeedbackEx() : value null or empty , return.");
                return;
            }
            final String catigory = String
                    .valueOf(characteristicMsgObject.clientId);
            CKLOG.Info(TAG, "onNotificationFeedbackEx() : catigory - " + catigory);
            final ArrayList<IAliBLESendStateCallback> list = mAliBLENotificationCallbacksMap
                    .get(catigory);
            if (null == list || list.isEmpty()) {
                CKLOG.Info(TAG,
                        "onNotificationFeedbackEx() : no callback of catigory - "
                                + catigory + " , return.");
                return;
            }
            for (IAliBLESendStateCallback callback : list) {
                try {
                    callback.onReceiveMessage(
                            characteristicMsgObject.device,
                            (characteristicMsgObject.characteristicValue));
                } catch (Exception e) {
                    CKLOG.Error(TAG,
                            "onNotificationFeedbackEx() : exception - "
                                    + e.getMessage());
                }
            }
        }
    }

    private void registerBLENotificationCallbackEx(
            final IAliBLESendStateCallback callback, final List<String> ids,
            final String packageName) {
        synchronized (mAliBLENotificationCallbackLock) {
            if (null == callback) {
                CKLOG.Info(TAG,
                        "registerBLENotificationCallbackEx() : callback null , return.");
                return;
            }
            if (null == ids || ids.isEmpty()) {
                CKLOG.Info(TAG,
                        "registerBLENotificationCallbackEx() : ids null or empty , return.");
                return;
            }
            if (TextUtils.isEmpty(packageName)) {
                CKLOG.Info(TAG,
                        "registerBLENotificationCallbackEx() : package name empty , return.");
                return;
            }
            CKLOG.Info(TAG, "registerBLENotificationCallbackEx() ... ");
            IAliBLESendStateCallback callbackToRe;
            if (!mAliBLENotificationCallbacks.containsKey(packageName)) {
                callbackToRe = callback;
                mAliBLENotificationCallbacks.put(packageName, callbackToRe);
            } else {
                callbackToRe = mAliBLENotificationCallbacks.get(packageName);
            }

            ArrayList<IAliBLESendStateCallback> listToAdd;

            for (String id : ids) {
                if (TextUtils.isEmpty(id)) {
                    continue;
                }
                if (!mAliBLENotificationCallbacksMap.containsKey(id)) {
                    listToAdd = new ArrayList<IAliBLESendStateCallback>();
                    mAliBLENotificationCallbacksMap.put(id, listToAdd);
                } else {
                    listToAdd = mAliBLENotificationCallbacksMap.get(id);
                    listToAdd.remove(callbackToRe);
                }
                listToAdd.add(callbackToRe);
            }
        }
    }

    private void unregisterBLENotificationCallbackEx(
            final IAliBLESendStateCallback callback, final List<String> ids,
            final String packageName) {
        synchronized (mAliBLENotificationCallbackLock) {
            if (null == callback) {
                CKLOG.Info(TAG,
                        "unregisterBLENotificationCallbackEx() : callback null , return.");
                return;
            }
            if (null == ids || ids.isEmpty()) {
                CKLOG.Info(TAG,
                        "unregisterBLENotificationCallbackEx() : ids null or empty , return.");
                return;
            }
            CKLOG.Info(TAG, "unregisterBLENotificationCallbackEx() ... ");
            ArrayList<IAliBLESendStateCallback> listToRm;
            for (String id : ids) {
                if (TextUtils.isEmpty(id)) {
                    continue;
                }
                listToRm = mAliBLENotificationCallbacksMap.get(id);
                if (null == listToRm) {
                    continue;
                }
                listToRm.remove(callback);
            }
        }
    }

    private void clearBLENotificationCallbackEx(final String packageName) {
        synchronized (mAliBLENotificationCallbackLock) {
            if (TextUtils.isEmpty(packageName)) {
                CKLOG.Info(TAG,
                        "clearBLENotificationCallbackEx() : package name empty , return.");
                return;
            }
            CKLOG.Info(TAG, "clearBLENotificationCallbackEx() ... ");
            final IAliBLESendStateCallback cb = mAliBLENotificationCallbacks
                    .remove(packageName);
            if (null == cb) {
                return;
            }
            final Collection<ArrayList<IAliBLESendStateCallback>> values = mAliBLENotificationCallbacksMap
                    .values();
            if (null == values) {
                return;
            }
            for (ArrayList<IAliBLESendStateCallback> list : values) {
                if (null != list) {
                    list.remove(cb);
                }
            }
        }
    }

    private void unregisterAllBLENotificationCallbacks() {
        mAliBLENotificationCallbacks.clear();
        mAliBLENotificationCallbacksMap.clear();
    }

    private Object mAliBLEScanCallbackLock = new Object();
    private ArrayList<IAliBLEScanCallback> mAliBLEScanCallbacks = new ArrayList<IAliBLEScanCallback>();

    private void registerBLEScanCallbackEx(final IAliBLEScanCallback callback) {
        synchronized (mAliBLEScanCallbackLock) {
            if (null != callback) {
                mAliBLEScanCallbacks.add(callback);
            }
        }
    }

    private void unregisterBLEScanCallbackEx(final IAliBLEScanCallback callback) {
        synchronized (mAliBLEScanCallbackLock) {
            if (null != callback) {
                mAliBLEScanCallbacks.remove(callback);
            }
        }
    }

    private void unregisterAllBLEScanCallback() {
        synchronized (mAliBLEScanCallbackLock) {
            mAliBLEScanCallbacks.clear();
        }
    }

    private Object mAliBLEConnectCallbackLock = new Object();
    private ArrayList<IAliBLEConnectCallback> mAliBLEConnectCallbacks = new ArrayList<IAliBLEConnectCallback>();

    private void registerBLEConnectCallbackEx(
            final IAliBLEConnectCallback callback) {
        synchronized (mAliBLEConnectCallbackLock) {
            if (null != callback) {
                mAliBLEConnectCallbacks.add(callback);
            }
        }
    }

    private void unregisterBLEConnectCallbackEx(
            String addr) {
        synchronized (mAliBLEConnectCallbackLock) {
            if (null != addr) {
                mAliBLEConnectCallbacks.remove(addr);
            }
        }
    }

    private void unregisterAllBLEConnectCallback() {
        synchronized (mAliBLEConnectCallbackLock) {
            mAliBLEConnectCallbacks.clear();
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        CKLOG.Info(TAG, "onBind() ... ");
        return mBinder;
    }

    @Override
    public void onCreate() {
        CKLOG.Info(TAG, "onCreate() ... ");
        super.onCreate();
        init();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        CKLOG.Info(TAG, "onUnbind() ... ");
        clearAllCallbacks();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        CKLOG.Info(TAG, "onDestroy() ... ");
        mBLEManager.destroy();
        unregisterReceiver(mBluetoothReceiver);
        mBluetoothReceiver = null;
        super.onDestroy();
    }

    private void init() {
        mBLEManager = BLEManager.instance();
        mBLEManager.init(getApplicationContext(), mHandler);
        mBluetoothReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                CKLOG.Info(TAG, "mBluetoothReceiver.onReceive() ... ");
                mBLEManager.onBluetoothStateChanged();
            }
        };
        final IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothReceiver, filter);
    }

    private void clearAllCallbacks() {
        unregisterAllBLEScanCallback();
        unregisterAllBLEConnectCallback();
        unregisterAllBLECharacteristicCallbacks();
        unregisterAllBLENotificationCallbacks();
    }

    private void sayHiEx(final String words) {
        CKLOG.Info(TAG, "sayHi() : client say hi to me , words : " + words);
        CKLOG.Info(TAG, "sayHi() : this is : " + BLEService.this);
    }

    private void onScannedBLEDevicesUpdatedEx() {
        synchronized (mAliBLEScanCallbackLock) {
            CKLOG.Info(TAG, "onScannedBLEDevicesUpdatedEx() ... ");
            for (IAliBLEScanCallback callback : mAliBLEScanCallbacks) {
                if (null != callback) {
                    try {
                        callback.onScannedBLEDevicesUpdated(mBLEManager
                                .getScannedBLEDevices());
                    } catch (Exception e) {
                        CKLOG.Error(TAG,
                                "onScannedBLEDevicesUpdatedEx() : exception - "
                                        + e.getMessage());
                    }
                }
            }
        }
    }

    private void onScanStartEx() {
        synchronized (mAliBLEScanCallbackLock) {
            CKLOG.Info(TAG, "onScanStartEx() ... ");
            for (IAliBLEScanCallback callback : mAliBLEScanCallbacks) {
                if (null != callback) {
                    try {
                        callback.onScanStart();
                    } catch (Exception e) {
                        CKLOG.Error(TAG,
                                "onScanStart() : exception - " + e.getMessage());
                    }
                }
            }
        }
    }

    private void onScanStopEx() {
        synchronized (mAliBLEScanCallbackLock) {
            CKLOG.Info(TAG, "onScanStopEx() ... ");
            for (IAliBLEScanCallback callback : mAliBLEScanCallbacks) {
                if (null != callback) {
                    try {
                        callback.onScanStop();
                    } catch (Exception e) {
                        CKLOG.Error(TAG,
                                "onScanStop() : exception - " + e.getMessage());
                    }
                }
            }
        }
    }

    private void onBLEConnectedEx(final Message message) {
        synchronized (mAliBLEConnectCallbackLock) {
            CKLOG.Info(TAG, "onBLEConnectedEx() ... ");
            final BluetoothDevice device = (BluetoothDevice) message.obj;
            for (IAliBLEConnectCallback callback : mAliBLEConnectCallbacks) {
                if (null != callback) {
                    try {
                        callback.onConnected(device);
                    } catch (Exception e) {
                        CKLOG.Error(TAG,
                                "onBleConnectEx() : exception - "
                                        + e.getMessage());
                    }
                }
            }
        }
    }

    private void onBLEDisonnectedEx(final Message message) {
        synchronized (mAliBLEConnectCallbackLock) {
            CKLOG.Info(TAG, "onBLEDisonnectedEx() ... ");
            final BluetoothDevice device = (BluetoothDevice) message.obj;
            BluetoothDev dev = AliBluetoothManager.instance().getDevice(device.getAddress());


            for (IAliBLEConnectCallback callback : mAliBLEConnectCallbacks) {
                if (null != callback) {
                    try {
                        callback.onDisconnected(dev);
                    } catch (Exception e) {
                        CKLOG.Error(TAG,
                                "onBleConnectEx() : exception - "
                                        + e.getMessage());
                    }
                }
            }
        }
    }

    private void onBLEConnectingEx(final Message message) {
        synchronized (mAliBLEConnectCallbackLock) {
            CKLOG.Info(TAG, "onBleConnectingEx() ... ");
            final BluetoothDevice device = (BluetoothDevice) message.obj;

            for (IAliBLEConnectCallback callback : mAliBLEConnectCallbacks) {
                if (null != callback) {
                    try {
                        callback.onConnecting(device);
                    } catch (Exception e) {
                        CKLOG.Error(TAG,
                                "onBleConnectEx() : exception - "
                                        + e.getMessage());
                    }
                }
            }

        }
    }
}
