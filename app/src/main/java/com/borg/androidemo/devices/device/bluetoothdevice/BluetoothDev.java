package com.borg.androidemo.devices.device.bluetoothdevice;

import android.text.TextUtils;

import com.borg.androidemo.common.utils.CKLOG;
import com.borg.androidemo.devices.api.AccessType;
import com.borg.androidemo.devices.api.DeviceConnection;
import com.borg.androidemo.devices.api.callback.AsyncDataTask;
import com.borg.androidemo.devices.api.callback.SendDataCallback;
import com.borg.androidemo.devices.connection.bluetooth.AliBluetoothManager;
import com.borg.androidemo.devices.connection.bluetooth.ble.callback.IAliBLESendStateCallback;
import com.borg.androidemo.devices.impl.DeviceCommand;
import com.borg.androidemo.devices.protocol.JsonProtocolConstant;
import com.borg.androidemo.devices.protocol.ResponseCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yiping.cyp on 2015/9/18.
 */
public abstract class BluetoothDev {

    private static final String TAG = BluetoothDev.class.getName();
    private static final String ACTIVE_KEY = "__act_command__";
    private static final String ACTIVE_SYNC_CLOUD_CARD = "synccloudcard";
    private static final int LATENCY_TIME = 15;
    private static final int AUTH_NOT_ESCAPE = 0;

    protected String mDeviceAddr;
    protected AccessType mAccessType;

    private final ArrayList<BluetoothDeviceListener> mDeviceListeners = new ArrayList<>();
    private final Object mDevicesListenerLock = new Object();

    public BluetoothDev(String deviceAddr, AccessType type){
        this.mAccessType = type;
        this.mDeviceAddr = deviceAddr;
    }

    public String getAddress() {
        return mDeviceAddr;
    }

    public AccessType getDeviceType() {
        return mAccessType;
    }

    public void connectToDevice() {
        AliBluetoothManager.instance().connectToBluetoothDevice(this, true);
    }

    public void disconnectFromDevice() {
        AliBluetoothManager.instance().disconnectFromBLEDevice(this);
    }

    /**
     * 注册监听器， 监听设备连接状态。
     *
     * @param listener
     * @return
     * @date 2015-6-15上午10:15:57
     */
    public synchronized boolean registerListener(final BluetoothDeviceListener listener) {
        synchronized (mDevicesListenerLock) {
            if (null == listener) {
                CKLOG.Info(TAG, "registerConnectBLEDevicesListener() : listener is null.");
                return false;
            }
            mDeviceListeners.add(listener);
            return true;
        }
    }

    /**
     * 移除指定的设备连接状态监听器
     *
     * @param listener
     * @return
     * @date 2015-6-15上午10:16:38
     */
    public boolean unRegisterListener(final BluetoothDeviceListener listener) {
        synchronized (mDevicesListenerLock) {
            return mDeviceListeners.remove(listener);
        }
    }

    public void onConnecting() {
        notifyDeviceState(DeviceConnection.DEVICE_CONNECTING);
    }

    public void onConnected() {

        if (mAccessType == AccessType.BLE_DEVICE_Direct ||mAccessType == AccessType.BT_DEVICE_Direct) {
            notifyDeviceState(DeviceConnection.DEVICE_WAITFORAUTH);
            notifyDeviceState(DeviceConnection.DEVICE_CONNECTED);
            try {
                /*DeviceCommand.authBTDevice(AUTH_NOT_ESCAPE, new AuthCallback() {
                            @Override
                            public void onAuthResult(final int responseCode) {

                                if (responseCode == AuthCallback.AUTH_ALREADY_BINDED_SUCCESS) {
                                    notifyDeviceState(DeviceConnection.DEVICE_BINDED);
                                } else if (responseCode == AuthCallback.AUTH_FAILED_ALREADING_BINDED_BY_OTHER) {
                                    notifyDeviceState(DeviceConnection.DEVICE_BINDBYOTHER);
                                } else {
                                    notifyDeviceState(DeviceConnection.DEVICE_CONNECTED);
                                }

                            }
                        }, CloudKitProfile.instance().getContext(),
                        LATENCY_TIME,
                        this);*/

            } catch (Exception e) {
                e.printStackTrace();
                CKLOG.Error(TAG, "onBLEDevicesConnectedEx:" + e.getMessage());
            }
        } else {
            notifyDeviceState(DeviceConnection.DEVICE_CONNECTED);
        }
    }

    public void onDisconnected() {
        notifyDeviceState(DeviceConnection.DEVICE_DISCONNECTED);
    }

    public void sendData(final String data, final int categoryCode, final SendDataCallback callback)
    {
        IAliBLESendStateCallback mRemoteBLENotificationCallback = getiAliBLENotificationCallback(callback);
        try {
            JSONObject notification = generateNotification(categoryCode, new JSONObject(data), callback);
            sendNotificationMessageToBLEDevice(this, notification, mRemoteBLENotificationCallback);
        }catch(JSONException e){
            mRemoteBLENotificationCallback.onSendMessageFailed(getAddress(), data.toString(), IAliBLESendStateCallback.FAIL_CODE_JSON_EXCEPTION);
        } catch (Exception e) {
            mRemoteBLENotificationCallback.onSendMessageFailed(getAddress(), data.toString(), IAliBLESendStateCallback.FAIL_CODE_EXCEPTION);
        }
    }

    public void onReceiveData(final String data) {
        CKLOG.Verbose(TAG, "onReceiveData: " + data);
        try {
            final JSONObject jsonObject = new JSONObject(data);
            final int category = jsonObject.optInt(JsonProtocolConstant.JSON_CATIGORY);

            final String activeInfo = jsonObject.getJSONObject(JsonProtocolConstant.JSON_CONTENT).optString(ACTIVE_KEY);
            CKLOG.Debug(TAG, "activeInfo=" + activeInfo);
            if (TextUtils.isEmpty(activeInfo)) {
                // 主动请求
                long seqId = jsonObject.optLong(JsonProtocolConstant.JSON_TRANSACT_ID);
                final SendDataCallback callBack = AsyncDataTask.instance().remove(seqId);

                if (callBack != null) {

                    callBack.onSuccess(data);

                } else {
                    CKLOG.Debug(TAG, "received a message from bt/ble out of time...");
                }

            } else {
                for (BluetoothDeviceListener listener: mDeviceListeners) {
                    listener.onReceiveData(data, category);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            CKLOG.Error(TAG, "in handleSaveData: " + e.toString());
        }
    }

    protected void sendNotificationMessageToBLEDevice(BluetoothDev device, JSONObject content,
                                                      IAliBLESendStateCallback mRemoteBLENotificationCallback)
    {
        AliBluetoothManager.instance().sendNotificationMessageToBLEDevice(this, content, mRemoteBLENotificationCallback);
    }

    protected IAliBLESendStateCallback getiAliBLENotificationCallback(final SendDataCallback callback) {
        return new IAliBLESendStateCallback() {
            @Override
            public void onSendMessageCompleted(final String address, final String notification) {
                DeviceCommand.startTask(callback);
            }

            @Override
            public void onSendMessageFailed(final String adress, final String notification, final int failCode) {
                callback.failAndRemove(ResponseCode.FAIL_SENDING_BT_REQ);
            }

            @Override
            public void onReceiveMessage(final android.bluetooth.BluetoothDevice device, final byte[] content) {
                CKLOG.Debug(TAG, "onReceiveMessage... device address : " + device.getAddress());
            }
        };
    }

    /**
     * 组装完整的指令
     *
     * @param catigory
     * @param content
     * @param callback
     * @return
     */
    protected JSONObject generateNotification(final int catigory, final JSONObject content, SendDataCallback callback) {
        final JSONObject json = new JSONObject();
        try {
            json.put(JsonProtocolConstant.JSON_CATIGORY, String.valueOf(catigory));
            json.put(JsonProtocolConstant.JSON_CONTENT, content);
            json.put(JsonProtocolConstant.JSON_TRANSACT_ID, callback.getSeqId());
            return json;
        } catch (JSONException e) {
            CKLOG.Error(TAG, "generateNotification() : JSONException - " + e.getMessage());
        }
        return null;
    }

// TODO: 15/10/22
//    private void handlerIncommingData(final String record) throws JSONException {
//
//            if (ServiceCategory.CATEGORY_WATER == category) {
//                CKLOG.Debug(TAG, "water info from bt:" + record);
//                CmnsProxy.instance().sendMessage(record, "1000200", System.currentTimeMillis(), CloudKitProfile.instance().getBaichuanAppKey(),
//                        new MessageResult() {
//
//                            @Override
//                            public IBinder asBinder() {
//                                return null;
//                            }
//
//                            @Override
//                            public void sendError(String arg0) throws RemoteException {
//                                CKLOG.Error(TAG, "water info upload failed.");
//                            }
//
//                            @Override
//                            public void sendOver() throws RemoteException {
//                                CKLOG.Debug(TAG, "water info upload sendOver.");
//                            }
//
//                            @Override
//                            public void sendSuccess(int arg0) throws RemoteException {
//                                CKLOG.Debug(TAG, "water info upload successfully.");
//                            }
//
//                        });
//            } else if (ServiceCategory.CATEGORY_CLOUD_CARD == category && activeInfo.equalsIgnoreCase(ACTIVE_SYNC_CLOUD_CARD)) {
//                // cloud card..
//                sendData(new JSONObject().put(JsonProtocolConstant.JSON_CMD, JsonProtocolConstant.JSON_ACK).put(JsonProtocolConstant.JSON_RESULT,
//                        JsonProtocolConstant.JSON_OK).toString(), ServiceCategory.CATEGORY_CLOUD_CARD, new SendDataCallback(ServiceCategory.CATEGORY_CLOUD_CARD) {
//                    @Override
//                    public void onSuccess(String data) {
//                        // 删除过期卡片数据
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                LifeCardInter.deleteexpired();
//                            }
//                        }).start();
//
//                        // 开始同步卡片
//                        ArrayList<String> params = new ArrayList<String>();
//                        params.add(CloudKitProfile.MOVIE_CARD);
//                        params.add(CloudKitProfile.LOGISTICS_CARD);
//                        OnResultCallback syncCardCallback = new SyncCardCallbackInternal(BluetoothDev.this);
//                        CardManager.instance().syncCardToDevice(params, syncCardCallback);
//                    }
//
//                    @Override
//                    public void onFail(int failCode) {
//                        CKLOG.Error(TAG, "ack for cloud card active request failed...failCode:" + failCode);
//                    }
//                });
//            } else {
//                // TODO: 15/10/20
////                if (owner != null) {
////                    // 把数据往主动蓝牙消息监听器传递
////                    owner.onReceiveActiveData(jRecord.toString());
////                } else {
////                    throw new IllegalArgumentException("ActiveDataReceiveListener has not been set,return");
////                }
//            }
//    }


    private void notifyDeviceState (int deviceState) {
        for (BluetoothDeviceListener listener: mDeviceListeners) {
            listener.onDeviceStateChange(deviceState);
        }
    }
}
