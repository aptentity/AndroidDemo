package com.borg.androidemo.devices.device;

import android.util.Log;

/**
 * @author yiping.cyp
 * @ClassName: DeviceInfo
 * @Version 1.0.0
 * @date 2015-8-16 下午7:37:08
 * @description 初始化时需要保存的设备信息类
 */
public class DeviceInfo {

	private static final String TAG = DeviceInfo.class.getSimpleName();
	private String type;// "watch"
	private String manufacturer;// "Oband"
	private String model;// "ObandWatch"
	private String deviceModel;// "obandi200"
	private String internalName;// "obandi20019X"
	private String osType;// "userdebug"
	// private String cuuid;
	private String version;// "1.0.0-X-20100101.1300"
	private String internalVersion;// "1.0.0"
	private String detail;

	public DeviceInfo(String type, String manufacturer, String model, String deviceModel, String internalName, String osType, String version, String internalVersion,
			String detail) {
		this.type = type;
		this.manufacturer = manufacturer;
		this.model = model;
		this.deviceModel = deviceModel;
		// this.cuuid = cuuid;
		this.version = version;
		this.osType = osType;
		this.internalVersion = internalVersion;
		this.internalName = internalName;
		this.detail = detail;
	}

	@Override
	public String toString() {
		return "DeviceInfo [type=" + type + ", manufacturer=" + manufacturer + ", model=" + model + ", osType=" + osType + ", deviceModel="
				+ deviceModel + ", version=" + version + ", internalVersion" + internalVersion + ", internalName" + internalName + "]";
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public String getVersion() {
		Log.e(TAG, "getVersion:" + version);
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getInternalVersion() {
		return internalVersion;
	}

	public void setInternalVersion(String internalVersion) {
		this.internalVersion = internalVersion;
	}

	public String getInternalName() {
		return internalName;
	}

	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}

	public String getDetail () {return  detail != null? detail : "{}";}
}
