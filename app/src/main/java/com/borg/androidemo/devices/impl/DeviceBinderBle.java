package com.borg.androidemo.devices.impl;


import com.borg.androidemo.common.utils.CKLOG;
import com.borg.androidemo.devices.api.DeviceConnection;
import com.borg.androidemo.devices.api.callback.BindingListener;
import com.borg.androidemo.devices.api.callback.SendDataCallback;
import com.borg.androidemo.devices.device.DeviceInfo;
import com.borg.androidemo.devices.protocol.JsonProtocolConstant;
import com.borg.androidemo.devices.protocol.ResponseCode;
import com.borg.androidemo.devices.protocol.ServiceCategory;
import com.borg.androidemo.devices.api.callback.SimpleCallback;

/**
 * Created by yiping.cyp on 2015/9/15.
 */
public class DeviceBinderBle extends DeviceBinderImplBase {

    private static final String TAG = DeviceBinderBle.class.getSimpleName();
    private SendDataCallback mBTCuuidCallback;

    public DeviceBinderBle(DeviceImpl device) {
        super(device);
    }

    protected void doBind (final DeviceConnection conn, final BindingListener listener) {

        CKLOG.Debug(TAG, "starting bindDevice process...");

        // 1.拿设备信息
        DeviceCommand.getDeviceInfoFromDevice(conn, new SendDataCallback(ServiceCategory.CATEGORY_SYS_WATCHER) {
            @Override
            public void onSuccess(String data) {
                if (JsonProtocolConstant.JSON_SUCCESS.equalsIgnoreCase(data)) {
                    CKLOG.Debug(TAG, "get device info success:" + data);
                    getOrGenerateCuuid((BluetoothDeviceConnection)conn, listener);
                } else {
                    CKLOG.Debug(TAG, "get device info failed:" + data);
                    listener.onFail(ResponseCode.FAIL_DEV_INFO);
                }
            }

            @Override
            public void onFail(int failCode) {
                CKLOG.Debug(TAG, "get device info failed:" + failCode);
                listener.onFail(ResponseCode.FAIL_DEV_INFO);
            }
        });
    }

    protected void doUnBind (final DeviceConnection conn, final DeviceInfo info,final BindingListener listener){
        bindDeviceToCloud(conn, mDevice.getDeviceInfo(), listener, "logout");
    }

    /**
     * 拿到cuuid，初次获取可能失败
     *
     * @param listener
     */
    private void getOrGenerateCuuid(final BluetoothDeviceConnection conn, final BindingListener listener) {
        CKLOG.Debug(TAG, "syncGetCuuid...");

        try {
            DeviceCommand.getCuuidFromBTDevice(new SimpleCallback() {

                @Override
                public void onSuccess(String data) {
                    BluetoothDeviceConnection conImpl = (BluetoothDeviceConnection) conn;
                    conImpl.setCuuid(data);
                    bindDeviceToCloud(conn, mDevice.getDeviceInfo(), listener, "login");
                }

                @Override
                public void onFail(String msg) {
                    CKLOG.Debug(TAG, "getCuuidFromDevice failed, begin the cuuid initialazation...");
//                    DeviceIDManager.instance().initCUUID(conn, new SimpleCallback() {
//
//                        @Override
//                        public void onSuccess(String data) {
//                            BluetoothDeviceConnection conImpl = (BluetoothDeviceConnection) conn;
//                            conImpl.setCuuid(data);
//                            bindDeviceToCloud(conn, mDevice.getDeviceInfo(), listener, "login");
//                        }
//
//                        @Override
//                        public void onFail(String msg) {
//                            listener.onFail(ResponseCode.FAIL_INIT_CUUID);
//                        }
//                    });
                }
            }, conn);
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFail(ResponseCode.FAIL_OTHER);
        }
    }
}
