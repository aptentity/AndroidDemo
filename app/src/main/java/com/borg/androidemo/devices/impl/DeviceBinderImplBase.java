package com.borg.androidemo.devices.impl;

import android.os.Handler;
import android.os.Looper;

import com.borg.androidemo.common.utils.CKLOG;
import com.borg.androidemo.devices.api.DeviceBinder;
import com.borg.androidemo.devices.api.DeviceConnection;
import com.borg.androidemo.devices.api.callback.BindingListener;
import com.borg.androidemo.devices.api.callback.SendDataCallback;
import com.borg.androidemo.devices.device.DeviceInfo;
import com.borg.androidemo.devices.init.CloudKitProfile;
import com.borg.androidemo.devices.protocol.JsonProtocolConstant;
import com.borg.androidemo.devices.protocol.ResponseCode;
import com.borg.androidemo.devices.protocol.ServiceCategory;
import com.borg.androidemo.devices.utils.SharedPreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wuzonglu on 15/10/21.
 */
abstract public class DeviceBinderImplBase implements DeviceBinder {
    private static final String TAG = DeviceBinderImplBase.class.getSimpleName();
    private Handler mDeviceBindHandler;
    protected DeviceImpl mDevice;

    public DeviceBinderImplBase(DeviceImpl device) {
        mDevice = device;
    }

    /**
     * 异步方式绑定设备（只有蓝牙的设备）
     * @param listener    绑定结果通知回调接口
     */
    public void bindDevice(final DeviceConnection conn, final BindingListener listener) {
        CKLOG.Debug(TAG, "bindDevice...");

        if (listener == null) {
            CKLOG.Debug(TAG, "context or listener can not be null...return");
            return;
        }

//        if (AccountManager.instance() == null || !AccountManager.instance().isLogin()) {
//            CKLOG.Debug(TAG, "you must login before starting binding process...");
//            listener.onFail(ResponseCode.FAIL_UNLOGINED);
//            return;
//        }

        mDeviceBindHandler = new Handler(Looper.myLooper());

        doBind(conn, new BindingListener() {
            @Override
            public void onSuccess(int responseCode) {
                SharedPreferencesUtil.setDeviceBindInfo(CloudKitProfile.instance().getKp(), mDevice.getAddress(), true);
                bindNotify(listener, true, responseCode);
            }

            @Override
            public void onFail(int responseCode) {
                bindNotify(listener, false, responseCode);
            }
        });
    }

    /**解除设备绑定
     * @param conn
     * @param listener 解绑结果通知回调接口
     */
    public void unbindDevice(final DeviceConnection conn, final BindingListener listener) {
        CKLOG.Debug(TAG, "starting bindDevice process...");

        if (listener == null) {
            CKLOG.Debug(TAG, "context or listener can not be null...return");
            return;
        }

//        if (AccountManager.instance() == null || !AccountManager.instance().isLogin()) {
//            CKLOG.Debug(TAG, "you must login before starting binding process...");
//            listener.onFail(ResponseCode.FAIL_UNLOGINED);
//            return;
//        }
        doUnBind(conn,mDevice.getDeviceInfo(),listener);
        //bindDeviceToCloud(conn, mDevice.getDeviceInfo(), listener, "openAccountLogout");
    }

    /**返回手机是否和设备已经绑定
     * @return 返回设备是否和手机绑定
     */
    public boolean isBinded ()
    {
        return SharedPreferencesUtil.getDeviceBindInfo(CloudKitProfile.instance().getKp(), mDevice.getAddress());
    }

    abstract protected void doBind (final DeviceConnection conn, final BindingListener listener);

    abstract protected void doUnBind (final DeviceConnection conn, final DeviceInfo info,final BindingListener listener);
    /**
     * 绑定设备上云
     */
    protected void bindDeviceToCloud(final DeviceConnection conn, DeviceInfo deviceInfo, final BindingListener listener, final String action) {

        CKLOG.Debug(TAG, "bindDeviceToCloud...");

//        CloudDataCenter.instance().bindDeviceToCloud(conn, deviceInfo, new BindingListener() {
//
//            @Override
//            public void onSuccess(int responseCode) {
//                // 发送相关信息到手表进行绑定／手机--手表交互
//                CKLOG.Debug(TAG, "bindDeviceToCloud sucess..responseCode=" + responseCode);
//                if (responseCode == ResponseCode.SUCCESS) {
//                    if (action.equalsIgnoreCase("login")) {
//                        bindBTDevice(conn, listener);
//                    } else {
//                        //AliBluetoothManager.instance().disconnectFromBLEDevice(conn.getDevice());
//                        if(conn.getAccessType()== AccessType.CARRIER_DEVICE){
//                            listener.onSuccess(ResponseCode.SUCCESS);
//                            return;
//                        }
//                        unbindBTDevice(conn, listener);
//                    }
//                } else {
//                    listener.onFail(responseCode);
//                }
//            }
//
//            @Override
//            public void onFail(int responseCode) {
//                CKLOG.Debug(TAG, "bindDeviceToCloud faled...responseCode=" + responseCode);
//                listener.onFail(BindingListener.FAIL_BIND_DEV_TO_CLOUD);
//            }
//        }, action);
    }

    /**
     * 通过蓝牙绑定手表到手机
     *
     * @param listener
     */
    protected void bindBTDevice(DeviceConnection conn, final BindingListener listener) {
        CKLOG.Debug(TAG, "bindBTDevice...");

        try {
            conn.sendData(
                    new JSONObject().put(JsonProtocolConstant.JSON_CMD, JsonProtocolConstant.JSON_SET)
                            .put(JsonProtocolConstant.JSON_ACTION, JsonProtocolConstant.JSON_BIND)
                            .put(JsonProtocolConstant.JSON_ESCAPE, 0).
                            put(JsonProtocolConstant.JSON_PHONE_ID, CloudKitProfile.instance().getDeviceId()),
                    ServiceCategory.CATEGORY_BIND_AUTH, new SendDataCallback(ServiceCategory.CATEGORY_BIND_AUTH) {
                        @Override
                        public void onSuccess(final String data) {
                            try {
                                CKLOG.Debug(TAG, "bindBTDevice success...,tid=" + getSeqId());
                                JSONObject jObjContent = new JSONObject(data).optJSONObject(JsonProtocolConstant.JSON_CONTENT);
                                if (jObjContent.optString("errmsg").equalsIgnoreCase("noerror")
                                        && jObjContent.optString(JsonProtocolConstant.JSON_ACK).equalsIgnoreCase("bindsuccess")) {
                                    CKLOG.Debug(TAG, "SUCCESS_FINALLY");
                                    listener.onSuccess(ResponseCode.SUCCESS);
                                    //syncCloudCard();
                                } else {
                                    // 蓝牙绑定设备失败
                                    listener.onFail(BindingListener.FAIL_BIND_BT_DEV);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                CKLOG.Error(TAG, "bindBTDevice json exception..");
                                listener.onFail(ResponseCode.FAIL_JSON);
                            }
                        }

                        @Override
                        public void onFail(int failCode) {
                            CKLOG.Error(TAG, "bindBTDevice failed: failCode=" + failCode + ",tid=" + getSeqId());
                            listener.onFail(BindingListener.FAIL_BIND_BT_DEV);
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过蓝牙手机和设备解绑
     *
     * @param listener
     */
    protected void unbindBTDevice(DeviceConnection conn, final BindingListener listener) {
        CKLOG.Debug(TAG, "unbindBTDevice...");

        try {
            conn.sendData(
                    new JSONObject().put(JsonProtocolConstant.JSON_CMD, JsonProtocolConstant.JSON_SET)
                            .put(JsonProtocolConstant.JSON_ACTION, JsonProtocolConstant.JSON_UNBIND)
                            .put(JsonProtocolConstant.JSON_ESCAPE, 0).
                            put(JsonProtocolConstant.JSON_PHONE_ID, CloudKitProfile.instance().getDeviceId()),
                    ServiceCategory.CATEGORY_BIND_AUTH, new SendDataCallback(ServiceCategory.CATEGORY_BIND_AUTH) {
                        @Override
                        public void onSuccess(final String data) {
                            listener.onSuccess(ResponseCode.SUCCESS);
                        }

                        @Override
                        public void onFail(int failCode) {
                            listener.onFail(failCode);
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void bindNotify (final BindingListener listener, final boolean isOk, final int responseCode)
    {
        mDeviceBindHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    if (isOk) {
                        listener.onSuccess(responseCode);
                    } else {
                        listener.onFail(responseCode);
                    }
                }
            }
        });
    }
}
