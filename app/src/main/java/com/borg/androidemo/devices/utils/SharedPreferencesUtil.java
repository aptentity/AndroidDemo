package com.borg.androidemo.devices.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.borg.androidemo.common.utils.CKLOG;
import com.borg.androidemo.devices.api.BindType;
import com.borg.androidemo.devices.device.DeviceInfo;
import com.borg.androidemo.devices.init.CloudKitProfile;
import com.borg.androidemo.devices.protocol.JsonProtocolConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


class Device {
    public String deviceAddr;
    public BindType addrType;
    public String deviceToken;
    public String packageName;
    public String cuuid;
    public DeviceInfo info;
    public boolean isBinded;
    // 添加一个字段用于保存二维码中的json
    public String qrcodeJson;

    @Override
    public String toString() {
        return "Device{" +
                "deviceAddr='" + deviceAddr + '\'' +
                ", addrType=" + addrType +
                ", deviceToken='" + deviceToken + '\'' +
                ", packageName='" + packageName + '\'' +
                ", cuuid='" + cuuid + '\'' +
                ", info=" + info +
                ", isBinded=" + isBinded +
                '}';
    }
}

class Account {
    public String kp;
    public ArrayList<Device> devices = new ArrayList<Device>();
}

public class SharedPreferencesUtil {

    private static final String TAG = SharedPreferencesUtil.class.getSimpleName();
    public static final String IS_BIND = "IS_BIND";
    private static final String LAST_ADDR = "LAST_ADDR";
    private static final String account_file = "cloudkit_account_devices";
    private static final String params_file = "cloudkit_params";
    private static final String key_active_account = "active_account";
    private static final String key_device_type = "device_type";
    private static final String key_os_type = "os_type";
    private static final String key_model = "model";
    private static final String key_manufacturer = "manufacturer";
    private static final String key_device_model = "device_model";
    private static final String key_version = "version";
    private static final String key_internal_version = "version";
    private static final String key_internal_name = "name";
    private static final String key_detail_name = "detail";
    private static final String key_cuuid = "cuuid";
    private static final String key_device_token = "device_token";
    private static final String key_package_name = "package_name";
    private static final String key_addr = "addr";
    private static final String key_addr_type = "addr_type";
    private static final String key_qrcode_json = "qrcode_json";

    //if kp == null or "", then active account will be clear
    public static synchronized void addAccount(String kp) {
        if (kp == null || kp.equals("") || !findKp(kp).equals("")) {
            return;
        }

        writeAccount(kp, new Account());
    }

    public static synchronized void removeAccount(String kp) {

    }

    public static synchronized void setActiveKp(final String kp) {
        if (kp != null && !kp.equals("") && findKp(kp).equals("")) {
            return;
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(key_active_account, kp);
        writeToLocal(params_file, params, Context.MODE_PRIVATE);
    }

    public static synchronized String getActiveKp() {
        Context ctx = CloudKitProfile.instance().getContext();
        SharedPreferences sp = ctx.getSharedPreferences(params_file, Context.MODE_PRIVATE);
        return sp.getString(key_active_account, "");
    }

    public static synchronized ArrayList<String> getDeviceList(final String kp) {
        Account acc = readAccount(kp);
        if (acc != null) {
            ArrayList<String> result = new ArrayList<String>();
            for (Device device : acc.devices) {
                result.add(device.deviceAddr);
            }

            return result;
        }
        return null;
    }

    public static synchronized void addDevice(final String kp, final String addr, BindType type) {
        Account acc = readAccount(kp);
        if (acc != null) {
            for (Device device : acc.devices) {
                if (device.deviceAddr.equals(addr) && device.addrType == type) {
                    return;
                }
            }

            Device dev = new Device();
            dev.deviceAddr = addr;
            dev.addrType = type;
            dev.isBinded = false;
            acc.devices.add(dev);
            writeAccount(kp, acc);
        }
    }

    public static synchronized void removeDevice(final String kp, final String addr, BindType type) {
        Account acc = readAccount(kp);
        if (acc != null) {
            for (Device device : acc.devices) {
                if (device.deviceAddr.equals(addr) && device.addrType == type) {
                    acc.devices.remove(device);
                    writeAccount(kp, acc);
                    break;
                }
            }
        }
    }

    public static synchronized void removeDevice(final String kp, final String cuuid) {
        Account acc = readAccount(kp);
        if (acc != null) {
            for (Device device : acc.devices) {
                if (device.cuuid.equals(cuuid)) {
                    acc.devices.remove(device);
                    writeAccount(kp, acc);
                    break;
                }
            }
        }
    }

    public static synchronized void setCuuid(final String kp, final String addr, final String cuuid) {
        Account acc = readAccount(kp);
        if (acc != null) {
            for (Device device : acc.devices) {
                if (device.deviceAddr.equals(addr)) {
                    device.cuuid = cuuid;
                    writeAccount(kp, acc);
                    return;
                }
            }
        }
    }

    public static synchronized String getCuuid(final String kp, final String addr) {
        Account acc = readAccount(kp);
        if (acc != null) {
            for (Device device : acc.devices) {
                if (device.deviceAddr.equals(addr)) {
                    return device.cuuid;
                }
            }
        }

        return null;
    }

    public static synchronized BindType getDeviceType(final String kp, final String addr) {
        Account acc = readAccount(kp);
        if (acc != null) {
            for (Device device : acc.devices) {
                if (device.deviceAddr.equals(addr)) {
                    return device.addrType;
                }
            }
        }

        return BindType.BINDER_BT;
    }

    /**
     * 写设备信息到本地
     *
     * @param deviceinfo
     * @date 2015-8-25 下午1:21:15
     * @return: void
     */
    public static synchronized void setDeviceInfo(final String kp, final String addr, final DeviceInfo deviceinfo) {
        Account acc = readAccount(kp);
        if (acc != null) {
            for (Device device : acc.devices) {
                if (device.deviceAddr.equals(addr)) {
                    device.info = deviceinfo;
                    writeAccount(kp, acc);
                    break;
                }
            }
        }
    }

    public static synchronized DeviceInfo getDeviceInfo(final String kp, final String addr) {
        Account acc = readAccount(kp);
        if (acc != null) {
            for (Device device : acc.devices) {
                if (device.deviceAddr.equals(addr)) {
                    return device.info;
                }
            }
        }

        return null;
    }

    public static synchronized void setQrcodejson(final String kp, final String addr, final String json) {
        Account acc = readAccount(kp);
        if (acc != null) {
            for (Device device : acc.devices) {
                if (device.deviceAddr.equals(addr)) {
                    device.qrcodeJson = json;
                    writeAccount(kp, acc);
                    break;
                }
            }
        }
    }

    public static synchronized String getQrcodejson(final String kp, final String addr) {
        Account acc = readAccount(kp);
        if (acc != null) {
            for (Device device : acc.devices) {
                if (device.deviceAddr.equals(addr)) {
                    return device.qrcodeJson;
                }
            }
        }

        return null;
    }

    /**
     * 拿deviceToken
     *
     * @return
     * @date 2015-8-25 下午1:22:17
     * @return: String
     */
    public static synchronized String getDeviceToken(String kp, String cuuid) {
        Account acc = readAccount(kp);
        if (acc != null) {
            for (Device device : acc.devices) {
                if (device.cuuid.equals(cuuid)) {
                    return device.deviceToken;
                }
            }
        }
        return null;
    }

    /**
     * 写deviceToken到本地
     *
     * @param deviceToken
     * @date 2015-8-25 下午1:20:48
     * @return: void
     */
    public static synchronized void setDeviceToken(String kp, String cuuid, String deviceToken) {
        Account acc = readAccount(kp);

        if (acc != null) {
            for (Device device : acc.devices) {
                if (device.cuuid.equals(cuuid)) {
                    device.deviceToken = deviceToken;
                    writeAccount(kp, acc);
                    break;
                }
            }
        }
    }

    /**
     * 拿packageName
     *
     * @return
     * @date 2015-8-25 下午1:22:17
     * @return: String
     */
    public static synchronized String getPackageName(String kp, String cuuid) {
        Account acc = readAccount(kp);
        if (acc != null) {
            for (Device device : acc.devices) {
                if (device.cuuid.equals(cuuid)) {
                    return device.packageName;
                }
            }
        }
        return null;
    }


    /**
     * 写packageName到本地
     *
     * @param packageName
     * @date 2015-8-25 下午1:20:48
     * @return: void
     */
    public static synchronized void setPackageName(String kp, String cuuid, String packageName) {
        Account acc = readAccount(kp);

        if (acc != null) {
            for (Device device : acc.devices) {
                if (device.cuuid.equals(cuuid)) {
                    device.packageName = packageName;
                    writeAccount(kp, acc);
                    break;
                }
            }
        }
    }

    /**
     * 写绑定情况
     *
     * @param isBinded
     * @date 2015-8-25 下午1:20:48
     * @return: void
     */
    public static synchronized void setDeviceBindInfo(String kp, String addr, boolean isBinded) {
        Account acc = readAccount(kp);
        if (acc != null) {
            for (Device device : acc.devices) {
                if (device.deviceAddr.equals(addr)) {
                    device.isBinded = isBinded;
                    writeAccount(kp, acc);
                    break;
                }
            }
        }
    }

    public static synchronized boolean getDeviceBindInfo(String kp, String addr) {
        Account acc = readAccount(kp);
        if (acc != null) {
            for (Device device : acc.devices) {
                if (device.deviceAddr.equals(addr)) {
                    return device.isBinded;
                }
            }
        }

        return false;
    }


    static String findKp(String kp) {
        Context ctx = CloudKitProfile.instance().getContext();
        SharedPreferences sp = ctx.getSharedPreferences(account_file, Context.MODE_PRIVATE);
        return sp.getString(kp, "");
    }

    private static Account readAccount(String kp) {
        String v = findKp(kp);
        if (v.equals("")) {
            return null;
        }

        Account acc = new Account();
        acc.kp = kp;
        try {
            JSONTokener jsonParser = new JSONTokener(v);
            JSONObject jsonobj = (JSONObject) jsonParser.nextValue();
            JSONArray array = jsonobj.getJSONArray("devices");
            for (int i = 0; i < array.length(); ++i) {
                JSONObject obj = (JSONObject) array.get(i);
                Device dev = new Device();
                dev.info = new DeviceInfo(obj.optString(key_device_type),
                        obj.optString(key_manufacturer),
                        obj.optString(key_model),
                        obj.optString(key_device_model),
                        obj.optString(key_internal_name),
                        obj.optString(key_os_type),
                        obj.optString(key_version),
                        obj.optString(key_internal_version),
                        obj.optString(key_detail_name)
                );
                dev.cuuid = obj.optString(key_cuuid);
                dev.deviceToken = obj.optString(key_device_token);
                dev.packageName = obj.optString(key_package_name);
                dev.addrType = BindType.valueOf(obj.optString(key_addr_type));
                dev.deviceAddr = obj.optString(key_addr);
                dev.isBinded = obj.optBoolean(IS_BIND);
                dev.qrcodeJson = obj.optString(key_qrcode_json);
                acc.devices.add(dev);
            }
        } catch (JSONException ex) {
            // 异常处理代码
        }
        return acc;
    }

    static void writeAccount(String kp, Account acc) {
        JSONObject top = new JSONObject();

        try {

            JSONArray array = new JSONArray();

            for (Device device : acc.devices) {
                JSONObject devobj = new JSONObject();

                if (device.info != null) {
                    devobj.put(key_device_type, device.info.getType().toString());
                    devobj.put(key_os_type, device.info.getOsType());
                    devobj.put(key_manufacturer, device.info.getManufacturer());
                    devobj.put(key_model, device.info.getModel());
                    devobj.put(key_device_model, device.info.getDeviceModel());
                    devobj.put(key_version, device.info.getVersion());
                    devobj.put(key_internal_version, device.info.getInternalVersion());
                    devobj.put(key_internal_name, device.info.getInternalName());
                    devobj.put(key_detail_name, device.info.getDetail());
                    devobj.put(key_qrcode_json,device.qrcodeJson);
                }

                if (device.cuuid != null) {
                    devobj.put(key_cuuid, device.cuuid);
                }
                if (device.deviceToken != null) {
                    devobj.put(key_device_token, device.deviceToken);
                }
                if (device.packageName != null) {
                    devobj.put(key_package_name, device.packageName);
                }
                if (device.deviceAddr != null) {
                    devobj.put(key_addr, device.deviceAddr);
                }
                if (device.addrType != null) {
                    devobj.put(key_addr_type, device.addrType.toString());
                }
                devobj.put(IS_BIND, device.isBinded);

                array.put(devobj);
            }

            top.put("devices", array);

        } catch (JSONException ex) {
            // 键为null或使用json不支持的数字格式(NaN, infinities)
            throw new RuntimeException(ex);
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(kp, top.toString());
        writeToLocal(account_file, params, Context.MODE_PRIVATE);
    }

    /**
     * 拿cmns的deviceId
     *
     * @return
     * @date 2015-8-25 下午1:21:34
     * @return: String
     */
    public static synchronized String getAppDeviceToken() {
        Context ctx = CloudKitProfile.instance().getContext();
        SharedPreferences sp = ctx.getSharedPreferences(params_file, Context.MODE_PRIVATE);
        return sp.getString(JsonProtocolConstant.JSON_CMNS_DEVICE_ID, "");
    }

    /**
     * 写deviceId到本地
     *
     * @param deviceToken
     * @date 2015-8-25 下午1:20:19
     * @return: void
     */
    public static synchronized void setAppDeviceToken(String deviceToken) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(JsonProtocolConstant.JSON_CMNS_DEVICE_ID, deviceToken);
        writeToLocal(params_file, params, Context.MODE_PRIVATE);
    }

    /**
     * 写上次卡片更新的时间戳
     *
     * @param lastRefreshTimeStamp
     * @date 2015-8-25 下午1:20:11
     * @return: void
     */
    public static synchronized void writeLastRefreshTimeStamp(long lastRefreshTimeStamp) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(JsonProtocolConstant.JSON_LAST_UPTIME, lastRefreshTimeStamp);
        writeToLocal(params_file, params, Context.MODE_PRIVATE);
    }

    /**
     * 拿上次卡片更新的时间戳
     *
     * @return
     * @date 2015-8-25 下午1:19:56
     * @return: long
     */
    public static synchronized long getLastRefreshTimeStamp() {
        Context ctx = CloudKitProfile.instance().getContext();
        SharedPreferences sp = ctx.getSharedPreferences(params_file, Context.MODE_PRIVATE);
        return Long.parseLong(sp.getString(JsonProtocolConstant.JSON_LAST_UPTIME, "-1"));
    }

    /**
     * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs)
     *
     * @date 2015-8-25 下午1:19:19
     * @return: void
     */
    public static void cleanSharedPreference() {
        Context ctx = CloudKitProfile.instance().getContext();
        deleteFilesByDirectory(new File(ctx.getFilesDir().getPath() + "/shared_prefs"));
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理
     *
     * @param directory
     * @date 2015-8-25 下午1:19:26
     * @return: void
     */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    /**
     * 更新本次连接成功的设备的mac地址
     *
     * @param addr
     * @date 2015-8-25 下午1:18:42
     * @return: void
     */
    public static synchronized void writeLastAddr(String addr) {
        if (addr == null) {
            return;
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LAST_ADDR, addr);
        writeToLocal(params_file, params, Context.MODE_PRIVATE);
    }

    /**
     * 拿上一次连接成功的设备的mac地址
     *
     * @return
     * @date 2015-8-25 下午1:18:27
     * @return: String
     */
    public static synchronized String getLastAddr() {
        Context ctx = CloudKitProfile.instance().getContext();
        SharedPreferences sp = ctx.getSharedPreferences(params_file, Context.MODE_PRIVATE);
        String result = sp.getString(LAST_ADDR, "");
        return result;
    }

    /**
     * 写数据到本地
     *
     * @param spName
     * @param params
     * @param mode
     * @date 2015-8-25 下午1:22:29
     * @return: void
     */
    static void writeToLocal(String spName, Map<String, Object> params, int mode) {
        Context ctx = CloudKitProfile.instance().getContext();

        SharedPreferences sp = ctx.getSharedPreferences(spName, mode);
        Editor edit = sp.edit();
        for (String key : params.keySet()) {
            String p = String.valueOf(params.get(key));
            CKLOG.Debug(TAG, "key=" + key + ",value=" + p);
            edit.putString(key, p);
        }
        edit.commit();
    }
}
