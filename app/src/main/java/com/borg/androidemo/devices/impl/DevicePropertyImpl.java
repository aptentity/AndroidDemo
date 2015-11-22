package com.borg.androidemo.devices.impl;


import com.borg.androidemo.common.utils.CKLOG;
import com.borg.androidemo.devices.api.DeviceConnection;
import com.borg.androidemo.devices.api.DeviceProperty;
import com.borg.androidemo.devices.api.callback.SendDataCallback;
import com.borg.androidemo.devices.protocol.JsonProtocolConstant;
import com.borg.androidemo.devices.protocol.ResponseCode;
import com.borg.androidemo.devices.protocol.ServiceCategory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wuzonglu on 15/10/20.
 */
public class DevicePropertyImpl implements DeviceProperty {
    private String type;// "watch"
    private String manufacturer;// "Oband"
    private String model;// "ObandWatch"
    private String internalName;// "obandi20019X"
    private String osType;// "userdebug"
    private String deviceModel;// "obandi200"
    // private String cuuid;
    private String version;// "1.0.0-X-20100101.1300"
    private String internalVersion;// "1.0.0"
    private static final String TAG = DeviceProperty.class.getSimpleName();


    public void initProperty(String type, String manufacturer, String model, String deviceModel, String watchVersion, String osType, String externalVersion,
                             String internalName) {
        this.type = type;
        this.manufacturer = manufacturer;
        this.model = model;
        this.deviceModel = deviceModel;
        // this.cuuid = cuuid;
        this.version = watchVersion;
        this.osType = osType;
        this.internalVersion = externalVersion;
        this.internalName = internalName;
    }

    @Override
    public String toString() {
        return "DeviceProperty [type=" + type + ", manufacturer=" + manufacturer + ", model=" + model + ", osType=" + osType + ", deviceModel="
                + deviceModel + ", version=" + version + ", internalVersion" + internalVersion + ", internalName" + internalName + "]";
    }

    public String getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getDeviceModel() {
        return deviceModel;
    }


    public String getInternalVersion() {
        return internalVersion;
    }

    public String getInternalName() {
        return internalName;
    }

    public void RefreshProperty (DeviceConnection conn, final DeviceProperty.PropertyListener listener)
    {
        if (listener == null) {
            CKLOG.Error(TAG, "PropertyListener is null... return");
            return;
        }


        ArrayList<String> params = new ArrayList<String>();
        params.add(JsonProtocolConstant.JSON_MODEL);
        params.add(JsonProtocolConstant.JSON_SYS_VER);

        DeviceCommand.getSeveralDeviceInfo(params, conn, new SendDataCallback(ServiceCategory.CATEGORY_SYS_WATCHER) {
            @Override
            public void onSuccess(String data) {
                CKLOG.Debug(TAG, "getDeviceInfo success:" + data);
                try {
                    JSONObject jObj = new JSONObject(data);
                    // "external_version" "1.0.0"
                    String externalVersion = jObj.optJSONObject(JsonProtocolConstant.JSON_CONTENT).optJSONObject(JsonProtocolConstant.JSON_SYS_VER)
                            .optString(JsonProtocolConstant.JSON_INTERNAL_VERSION);
                    String watchVersion = jObj.optJSONObject(JsonProtocolConstant.JSON_CONTENT).optJSONObject(JsonProtocolConstant.JSON_SYS_VER)
                            .optString(JsonProtocolConstant.JSON_SYS_VERSION);
                    JSONObject modelObj = jObj.optJSONObject(JsonProtocolConstant.JSON_CONTENT).optJSONObject(JsonProtocolConstant.JSON_MODEL);

                    // "device":"watch",
                    type = modelObj.optString(JsonProtocolConstant.JSON_DEVICE);
                    // "type" "userdebug"
                    osType = modelObj.optString(JsonProtocolConstant.JSON_TYPE);

                    internalName = modelObj.optString(JsonProtocolConstant.JSON_INTERNAL_MODEL);

                    // "brand":"Oband",
                    manufacturer = modelObj.optString(JsonProtocolConstant.JSON_BRAND);
                    // "model":"ObandWatch",
                    model = modelObj.optString(JsonProtocolConstant.JSON_MODEL);
                    // "name":"obandi200"
                    deviceModel = modelObj.optString(JsonProtocolConstant.JSON_NAME);

                    listener.onSuccess();
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onFail(ResponseCode.FAIL_JSON);
                }
            }

            @Override
            public void onFail(int responseCode) {
                listener.onFail(responseCode);
            }
        });
    }

}
