package com.borg.androidemo.ble;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.borg.androidemo.R;
import com.borg.androidemo.common.log.LogHelper;
import com.borg.androidemo.devices.api.BindType;
import com.borg.androidemo.devices.api.Device;
import com.borg.androidemo.devices.api.DeviceConnectListener;
import com.borg.androidemo.devices.api.DeviceConnection;
import com.borg.androidemo.devices.api.DeviceManager;
import com.borg.androidemo.devices.api.callback.SendDataCallback;
import com.borg.androidemo.devices.connection.bluetooth.AliBluetoothManager;
import com.borg.androidemo.devices.init.CloudKitProfile;
import com.borg.androidemo.devices.protocol.JsonProtocolConstant;
import com.borg.androidemo.devices.protocol.ServiceCategory;

import org.json.JSONException;
import org.json.JSONObject;

public class BleTestActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private String mDeviceName;
    private String mDeviceAddress;
    private Device mdevice;
    private TextView tvResult;
    private EditText etData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_test);
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        LogHelper.d("device address=" + mDeviceAddress);
        CloudKitProfile.instance().initCloudKitProfile("", "", "", true, getApplicationContext());
        AliBluetoothManager.instance().init(CloudKitProfile.instance().getContext());

        findViewById(R.id.btn_connect).setOnClickListener(this);
        findViewById(R.id.btn_send).setOnClickListener(this);
        tvResult = (TextView)findViewById(R.id.tv_result);
        etData = (EditText)findViewById(R.id.et_data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_connect:
                connect();
                break;
            case R.id.btn_send:
                send();
                break;
        }
    }
    private static final int AUTH_NOT_ESCAPE = 0;
    private static final int LATENCY_TIME = 15;
    private void send(){


//        if (!isCategoryValid(callback, ServiceCategory.CATEGORY_BIND_AUTH)) {
//            return;
//        }
//        dev.sendData(
//                new JSONObject().put(JsonProtocolConstant.JSON_CMD, JsonProtocolConstant.JSON_SET)
//                        .put(JsonProtocolConstant.JSON_ACTION, action)
//                        .put(JsonProtocolConstant.JSON_ESCAPE, escape)
//                        .put(JsonProtocolConstant.JSON_PHONE_ID, phoneid).toString(),
//                ServiceCategory.CATEGORY_BIND_AUTH, callback);
        try{
            String str="";
            if (TextUtils.isEmpty(etData.getText().toString())){
                JSONObject object = (new JSONObject().put(JsonProtocolConstant.JSON_CMD, JsonProtocolConstant.JSON_SET)
                        .put(JsonProtocolConstant.JSON_ACTION, "auth")
                        .put(JsonProtocolConstant.JSON_ESCAPE, 0)
                        .put(JsonProtocolConstant.JSON_PHONE_ID, "123456"));
                str =object.toString();
            }else{
                str = etData.getText().toString();
            }

            SendDataCallback callbck = new SendDataCallback(ServiceCategory.CATEGORY_BIND_AUTH, 15) {
                @Override
                public void onSuccess(String data) {
                    LogHelper.d("send data onSuccess="+data);
                    tvResult.setText("send data onSuccess="+data);
                }

                @Override
                public void onFail(int responseCode) {
                    LogHelper.d("send data onFail="+responseCode);
                    tvResult.setText("send data onFail=" + responseCode);
                }
            };
            mdevice.getDefaultDeviceConnection().sendData(str,ServiceCategory.CATEGORY_BIND_AUTH,callbck);
        }catch(JSONException e){}

    }
    private void connect(){
        mdevice = DeviceManager.instance().addDevice(mDeviceAddress, BindType.BINDER_BLE);
        mdevice.getDefaultDeviceConnection().connectToDevice(new DeviceConnectListener() {
            @Override
            public void onConnecting(DeviceConnection conn) {
                LogHelper.d("onConnecting");
                tvResult.setText("onConnecting");
            }

            @Override
            public void onDisconnected(DeviceConnection conn) {
                LogHelper.d("onDisconnected");
                tvResult.setText("onDisconnected");
            }

            @Override
            public void onConnected(DeviceConnection conn, int authCode) {
                LogHelper.d("onConnected:"+authCode);
                tvResult.setText("onConnected:"+authCode);
            }
        });
    }
}
