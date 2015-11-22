package com.borg.androidemo.devices.impl;

import android.content.Context;
import android.text.TextUtils;

import com.borg.androidemo.common.utils.CKLOG;
import com.borg.androidemo.devices.api.DeviceConnection;
import com.borg.androidemo.devices.api.callback.AsyncDataTask;
import com.borg.androidemo.devices.api.callback.AuthCallback;
import com.borg.androidemo.devices.api.callback.SendDataCallback;
import com.borg.androidemo.devices.api.callback.SimpleCallback;
import com.borg.androidemo.devices.device.DeviceInfo;
import com.borg.androidemo.devices.device.bluetoothdevice.BluetoothDev;
import com.borg.androidemo.devices.init.CloudKitProfile;
import com.borg.androidemo.devices.protocol.JsonProtocolConstant;
import com.borg.androidemo.devices.protocol.ResponseCode;
import com.borg.androidemo.devices.protocol.ServiceCategory;
import com.borg.androidemo.devices.utils.SharedPreferencesUtil;
import com.borg.androidemo.devices.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yiping.cyp on 2015/9/16.
 */
public class DeviceCommand {

    private static final String TAG = DeviceCommand.class.getSimpleName();

    /**
     * 开始计时任务
     *
     * @param callback
     * @return
     */
    public static long startTask(final SendDataCallback callback) {
        AsyncDataTask.instance().add(callback);
        return callback.getSeqId();
    }

    /**
     * 查看callback的category是否正确
     *
     * @param callback
     * @param category
     * @return
     */
    public static boolean isCategoryValid(final SendDataCallback callback, int category) {
        if (category != callback.getCatigory()) {
            callback.failAndRemove(ResponseCode.FAIL_CATEGORY);
            return false;
        }
        return true;
    }

    /**
     * 一次获取多个同category＝CATEGORY_SYS_WATCHER的信息
     *
     * @param callback
     * @param params   存放多个type
     */
    public static void getSeveralDeviceInfo(ArrayList<String> params, DeviceConnection connection, final SendDataCallback callback) {

        if (params == null || params.size() == 0) {
            callback.failAndRemove(ResponseCode.FAIL_PARAM);
        }
        try {

            JSONArray paramArray = new JSONArray(params);
            CKLOG.Debug(TAG, "paramArray=" + paramArray);

            connection.sendData(
                    new JSONObject().put(JsonProtocolConstant.JSON_CMD, JsonProtocolConstant.JSON_GET)
                            .put(JsonProtocolConstant.JSON_TYPE, paramArray), callback.getCatigory(), callback);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.failAndRemove(ResponseCode.FAIL_JSON);
        }
    }

    /**
     * 认证手表到手机，蓝牙通道
     *
     * @param escape
     * @param callback
     * @param ctx
     */
    public static void authBTDevice(final int escape, final AuthCallback callback, final Context ctx, final int latencyTime, final BluetoothDev dev) {
        final String deviceId="";//= CloudKitProfile.instance().getDeviceId();
        sendBTBindAuthInfo(JsonProtocolConstant.JSON_AUTH, escape, deviceId,
                new SendDataCallback(ServiceCategory.CATEGORY_BIND_AUTH, latencyTime) {
                    @Override
                    public void onSuccess(String data) {
                        JSONObject jObj;
                        try {
                            jObj = new JSONObject(data);

                            String authResult = jObj.optJSONObject(JsonProtocolConstant.JSON_CONTENT).optString(JsonProtocolConstant.JSON_AUTH);
                            CKLOG.Debug(TAG, "authResult=" + authResult);
                            if (JsonProtocolConstant.JSON_SUCCESS.equalsIgnoreCase(authResult.trim())) {
                                callback.onAuthResult(AuthCallback.AUTH_ALREADY_BINDED_SUCCESS);
                            } else if (JsonProtocolConstant.JSON_FAILED.equalsIgnoreCase(authResult.trim())) {
                                CKLOG.Debug(TAG, "AUTH_FAILED_ALREADING_BINDED_BY_OTHER");
                                callback.onAuthResult(AuthCallback.AUTH_FAILED_ALREADING_BINDED_BY_OTHER);
                            } else if (JsonProtocolConstant.JSON_UNBIND.equalsIgnoreCase(authResult.trim())) {
                                CKLOG.Debug(TAG, "AUTH_UNBINDED");
                                callback.onAuthResult(AuthCallback.AUTH_UNBINDED);
                            } else {
                                CKLOG.Error(TAG, "something went wrong....authResult=" + authResult);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onAuthResult(ResponseCode.FAIL_JSON);
                        }
                    }

                    @Override
                    public void onFail(int failCode) {
                        CKLOG.Debug(TAG, "onFail failCode:" + failCode + ",tid=" + getSeqId());
                        switch (failCode) {
                            case ResponseCode.FAIL_JSON:
                            case ResponseCode.FAIL_PARAM:
                            case ResponseCode.FAIL_CMNS:
                            case ResponseCode.FAIL_TIME_OUT:
                                callback.onAuthResult(failCode);
                                break;
                            case ResponseCode.FAIL_SENDING_BT_REQ:
                            case ResponseCode.FAIL_BT_CONNECTION:
                                callback.onAuthResult(ResponseCode.FAIL_BT_CONNECTION);
                                break;
                            default:
                                callback.onAuthResult(ResponseCode.FAIL_OTHER);
                                break;
                        }
                    }
                }, dev);
    }

    static void sendBTBindAuthInfo(String action, int escape, String phoneid, final SendDataCallback callback, final BluetoothDev dev) {
        try {
            if (!isCategoryValid(callback, ServiceCategory.CATEGORY_BIND_AUTH)) {
                return;
            }
            dev.sendData(
                    new JSONObject().put(JsonProtocolConstant.JSON_CMD, JsonProtocolConstant.JSON_SET)
                            .put(JsonProtocolConstant.JSON_ACTION, action)
                            .put(JsonProtocolConstant.JSON_ESCAPE, escape)
                            .put(JsonProtocolConstant.JSON_PHONE_ID, phoneid).toString(),
                    ServiceCategory.CATEGORY_BIND_AUTH, callback);

        } catch (JSONException e) {
            e.printStackTrace();
            callback.failAndRemove(ResponseCode.FAIL_JSON);
        }
    }

    /**
     * 当手表显示曾经绑定过的时候，去查找一下当前是否有手表的数据
     *
     * @param callback
     * @date 2015-8-18 下午5:13:40
     * @return: void
     */
    private static void checkForDeviceInfo(DeviceConnection conn, AuthCallback callback) {
        String kp = SharedPreferencesUtil.getActiveKp();
        String cuuid = conn.getCuuid();
        if (StringUtils.isOneParamEmpty("in DeviceCommand checkForDeviceInfo", kp, cuuid)) {
            CKLOG.Debug(TAG, "AUTH_ALREADY_BINDED_BUT_FAILED_WATCH_ID");
            callback.onAuthResult(AuthCallback.AUTH_ALREADY_BINDED_BUT_FAILED_WATCH_ID);
        } else {
            CKLOG.Debug(TAG, "AUTH_ALREADY_BINDED_SUCESS");
            callback.onAuthResult(AuthCallback.AUTH_ALREADY_BINDED_SUCCESS);
        }
    }



    /**
     * 获取手表当前电量
     *
     *
     * @return 当前电量 0-100
     */
    public static void getBatteryLevel(final SendDataCallback callback,final DeviceConnection conn) {
        if (!isCategoryValid(callback, ServiceCategory.CATEGORY_CONTROL)) {
            return;
        }
        try {
            conn.sendData(new JSONObject().put(JsonProtocolConstant.JSON_CMD, JsonProtocolConstant.JSON_GET),
                    ServiceCategory.CATEGORY_CONTROL, callback);

        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.failAndRemove(ResponseCode.FAIL_JSON);
        }
    }

    /**
     * 从设备获取cuuid

     * @param sCallback
     */
    public static void getCuuidFromBTDevice(final SimpleCallback sCallback, final BluetoothDeviceConnection conn) {
        CKLOG.Debug(TAG, "getCuuidFromBTDevice...");
        try {
            final SendDataCallback mBTCuuidCallback = new SendDataCallback(ServiceCategory.CATEGORY_CUUID) {
                @Override
                public void onSuccess(String data) {
                    try {
                        String cuuid = new JSONObject(data).optJSONObject(JsonProtocolConstant.JSON_CONTENT).optString(
                                JsonProtocolConstant.JSON_CUUID);
                        CKLOG.Debug(TAG, "getCuuidFromBTDevice onsuccess:cuuid=" + cuuid);
                        if (TextUtils.isEmpty(cuuid)) {
                            if (sCallback != null)
                                sCallback.onFail("get cuuid from watch receive empty...");
                            CKLOG.Debug(TAG, "get cuuid from watch receive empty...");
                            return;
                        }

                        if (sCallback != null)
                            sCallback.onSuccess(cuuid);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        failAndRemove(ResponseCode.FAIL_JSON);
                        if (sCallback != null)
                            sCallback.onFail("invalid respond:" + ResponseCode.FAIL_JSON);
                    }
                }

                @Override
                public void onFail(int failCode) {
                    CKLOG.Error(TAG, "getCuuid failed:" + failCode);
                    if (sCallback != null)
                        sCallback.onFail("getCuuid failed:" + failCode);
                }
            };

            conn.sendData(
                    new JSONObject().put(JsonProtocolConstant.JSON_CMD, JsonProtocolConstant.JSON_GET).put(JsonProtocolConstant.JSON_TYPE,
                            JsonProtocolConstant.JSON_CUUID_INFO), mBTCuuidCallback.getCatigory(), mBTCuuidCallback);
        } catch (JSONException e) {
            e.printStackTrace();
            if (sCallback != null)
                sCallback.onFail("JSON EXCEPTION IN GET CUUID FROM BT DEVICE:" + e.toString());
        }
    }


    // ....................begin ............................
    /**
     * 获取手表地址
     *
     * @param callback
     */
    public static void getDeviceAddr(final SendDataCallback callback,DeviceConnection conn) {

        if (!isCategoryValid(callback, ServiceCategory.CATEGORY_SYS_WATCHER)) {
            return;
        }

        JSONObject json_content = new JSONObject();
        try {
            json_content.put("cmd", "get");
            json_content.put("type", "macaddr");
            CKLOG.Debug(TAG, "asyncGetWearAddr: " + json_content.toString());

            conn.sendData(json_content, callback.getCatigory(), callback);

        } catch (JSONException e) {
            e.printStackTrace();
            callback.failAndRemove(ResponseCode.FAIL_JSON);
        }
    }

    public static void getDeviceInfoFromDevice(final DeviceConnection conn, final SendDataCallback callback) {
        ArrayList<String> params = new ArrayList<String>();
        params.add(JsonProtocolConstant.JSON_MODEL);
        params.add(JsonProtocolConstant.JSON_SYS_VER);

        CKLOG.Debug(TAG, "params:" + params);
        getSeveralDeviceInfo(params, conn, new SendDataCallback(ServiceCategory.CATEGORY_SYS_WATCHER) {

            @Override
            public void onSuccess(String data) {
                CKLOG.Debug(TAG, "getDeviceInfoFromDevice success:" + data);
                try {
                    JSONObject jObj = new JSONObject(data);
                    // "external_version" "1.0.0"
                    String internalVersion = jObj.optJSONObject(JsonProtocolConstant.JSON_CONTENT).optJSONObject(JsonProtocolConstant.JSON_SYS_VER)
                            .optString(JsonProtocolConstant.JSON_INTERNAL_VERSION);
                    String version = jObj.optJSONObject(JsonProtocolConstant.JSON_CONTENT).optJSONObject(JsonProtocolConstant.JSON_SYS_VER)
                            .optString(JsonProtocolConstant.JSON_SYS_VERSION);
                    JSONObject modelObj = jObj.optJSONObject(JsonProtocolConstant.JSON_CONTENT).optJSONObject(JsonProtocolConstant.JSON_MODEL);
                    // "device":"watch",
                    String type = modelObj.optString(JsonProtocolConstant.JSON_DEVICE);
                    // "type" "userdebug"
                    String osType = modelObj.optString(JsonProtocolConstant.JSON_TYPE);

                    String internalName = modelObj.optString(JsonProtocolConstant.JSON_INTERNAL_MODEL);

                    // "brand":"Oband",
                    String manufacturer = modelObj.optString(JsonProtocolConstant.JSON_BRAND);
                    // "model":"ObandWatch",
                    String model = modelObj.optString(JsonProtocolConstant.JSON_MODEL);
                    // "name":"obandi200"
                    String deviceModel = modelObj.optString(JsonProtocolConstant.JSON_NAME);

                    if (StringUtils.isOneParamEmpty("in DeviceCommand getDeviceInfoFromDevice",type, manufacturer, model, deviceModel, version, osType, internalVersion, internalName)) {
                        CKLOG.Error(TAG, "one of deviceInfo's variable is null or empty...");
                        callback.onFail(ResponseCode.FAIL_DEV_INFO);
                        return;
                    }

                    DeviceInfo deviceInfo = new DeviceInfo(type, manufacturer, model, deviceModel, internalName, osType, version, internalVersion, "{}");
                    SharedPreferencesUtil.setDeviceInfo(CloudKitProfile.instance().getKp(), conn.getAddress(), deviceInfo);

                    if (callback != null)
                        callback.onSuccess(JsonProtocolConstant.JSON_SUCCESS);

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (callback != null)
                        callback.onFail(ResponseCode.FAIL_JSON);
                }
            }

            @Override
            public void onFail(int failCode) {
                if (callback != null)
                    callback.onFail(failCode);
                CKLOG.Error(TAG, "getDeviceInfoFromDevice failed:" + failCode);
            }
        });
    }
}
