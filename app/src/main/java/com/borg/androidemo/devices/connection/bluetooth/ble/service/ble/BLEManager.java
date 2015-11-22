package com.borg.androidemo.devices.connection.bluetooth.ble.service.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.borg.androidemo.common.utils.CKLOG;
import com.borg.androidemo.devices.connection.bluetooth.ble.callback.IAliBLESendStateCallback;
import com.borg.androidemo.devices.connection.bluetooth.ble.service.BLEService;
import com.borg.androidemo.devices.connection.bluetooth.ble.service.characteristic.CharacteristicMsgObject;
import com.borg.androidemo.devices.connection.bluetooth.ble.utils.BLEUtils;
import com.borg.androidemo.devices.connection.bluetooth.ble.utils.Utils;
import com.borg.androidemo.devices.connection.bluetooth.ble.uuid.AliBLEUuid;
import com.borg.androidemo.devices.connection.bluetooth.ble.uuid.AliBLEUuidUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BLEManager {

	private static final String TAG = "BLEManager";

	private BLEManager() {
	}

	private static BLEManager sInstance;

	private Context mContext;
	private Handler mInternalHandler;

	private BluetoothAdapter mBluetoothAdapter;

	private final Object mScannedBLEDevicesLock = new Object();
	private final HashMap<String, BluetoothDevice> mScannedBLEDevices = new HashMap<String, BluetoothDevice>();

	private final Object mConnectedBLEGattsLock = new Object();
	private final HashMap<String, BluetoothGatt> mConnectedBLEGatts = new HashMap<String, BluetoothGatt>();

	private final LeScanCallback mLeScanCallback = new LeScanCallback() {
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			CKLOG.Info(TAG, "onLeScan() : device scanned , address - " + device.getAddress());
			addScannedBLEDevice(device);
		}
	};

	private static final int SCAN_BLE_TIMEOUT = 20000;
	private boolean mIsScanningBLE = false;
	private long mScanningStartTime;

	public static String logstring(byte[] data) {
		if (data != null && data.length > 0) {
			final StringBuilder stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data)
				if ((byteChar >= 32 && byteChar <= 126))
					stringBuilder.append(String.format("%c ", byteChar));

			return stringBuilder.toString();
		}
		return "null bytes";
	}

	private final BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			CKLOG.Info(TAG, "onConnectionStateChange() : status - " + status + " , newState - " + newState);
			if (BluetoothGatt.GATT_SUCCESS != status) {
				CKLOG.Error(TAG, "onConnectionStateChange() : status not success , return.");
				return;
			}
			if (null == gatt) {
				CKLOG.Error(TAG, "onConnectionStateChange() : gatt null , return.");
				return;
			}
			final BluetoothDevice device = gatt.getDevice();
			if (null == device) {
				CKLOG.Error(TAG, "onConnectionStateChange() : device of gatt null , return.");
				return;
			}
			if (BluetoothProfile.STATE_CONNECTED == newState) {
				CKLOG.Info(TAG, "onConnectionStateChange() : try to connect to GATT server.");
				gatt.discoverServices();
				// record recent connected gatt device address
				String address = gatt.getDevice().getAddress();
			} else if (BluetoothProfile.STATE_DISCONNECTED == newState) {
				CKLOG.Info(TAG, "onConnectionStateChange() : disconnected from GATT server.");
				removeConnectedBLEGatt(gatt);
				removeReadCharacteristicTask(device);
				removeWriteCharacteristicTask(device);
				gatt.close();
				onBLEDeviceDisconnected(gatt.getDevice());
			} else if (BluetoothProfile.STATE_CONNECTING == newState) {
				CKLOG.Info(TAG, "onConnectionStateChange() : connecting to GATT server.");
				onBLEDeviceConnecting(device);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (BluetoothGatt.GATT_SUCCESS != status) {
				CKLOG.Error(TAG, "onServicesDiscovered() : status not success , return.");
				return;
			}
			if (null == gatt) {
				CKLOG.Error(TAG, "onServicesDiscovered() : gatt null , return.");
				return;
			}
			final BluetoothDevice device = gatt.getDevice();
			if (null == device) {
				CKLOG.Error(TAG, "onServicesDiscovered() : device of gatt null , return.");
				return;
			}
			CKLOG.Info(TAG, "onServicesDiscovered() : gatt server discovered success, means connected finally.");
			addConnectedBLEGatt(gatt);
			enableNotificationFeedback(gatt);
			onBLEDeviceConnected(device);
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if (BluetoothGatt.GATT_SUCCESS != status) {
				CKLOG.Info(TAG, "onCharacteristicRead() : status not GATT_SUCCESS , status - " + status);
				return;
			}
			CKLOG.Info(TAG, "onCharacteristicRead() : characteristic - " + characteristic.toString());
			final CharacteristicMsgObject obj = new CharacteristicMsgObject();
			obj.device = gatt.getDevice();
			obj.serviceUuid = characteristic.getService().getUuid().toString();
			obj.characteristicUuid = characteristic.getUuid().toString();
			obj.characteristicValue = Utils.clonebytes(characteristic.getValue());
			obj.status = status;
			CKLOG.Info(TAG, "onCharacteristicRead() : device address - " + obj.device.getAddress() + " , service uuid - " + obj.serviceUuid
					+ " , characteristic uuid - " + obj.characteristicUuid + " , characteristic content - " + new String(obj.characteristicValue));
			if (null != mInternalHandler) {
				mInternalHandler.obtainMessage(BLEService.INTERNAL_COMMAND_ON_CHARACTERISTIC_READ, obj).sendToTarget();
			}
			onReadCharacteristic();
		}

		@Override
		public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
			CKLOG.Info(TAG, "onCharacteristicWrite() : characteristic - " + characteristic.toString());
			final CharacteristicMsgObject obj = new CharacteristicMsgObject();
			obj.device = gatt.getDevice();
			obj.serviceUuid = characteristic.getService().getUuid().toString();
			obj.characteristicUuid = characteristic.getUuid().toString();
			obj.characteristicValue = Utils.clonebytes(characteristic.getValue());
			obj.status = status;
			CKLOG.Info(TAG, "onCharacteristicWrite() : device address - " + obj.device.getAddress() + " , service uuid - " + obj.serviceUuid
					+ " , characteristic uuid - " + obj.characteristicUuid + " , characteristic content - " + new String(obj.characteristicValue)
					+ " , status - " + obj.status);
			if (null != mInternalHandler) {
				mInternalHandler.obtainMessage(BLEService.INTERNAL_COMMAND_ON_CHARACTERISTIC_WRITE, obj).sendToTarget();
			}
			onWriteCharacteristic();
		}

		private byte[] mReadValue;
		private int mReadNum;
		private int mAlreadRead = 0;
		private int mAlreadReadByte = 0;
		private int mReadValueLength = 0;

		private int seqId = 0;

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

			CKLOG.Debug(TAG, "AliBleService onCharacteristicChanged: " + logstring(characteristic.getValue()) + ",length="
					+ characteristic.getValue().length);
			final CharacteristicMsgObject obj = new CharacteristicMsgObject();

			// obj.seqId = seqId;
			if (mAlreadRead == 0) {
				// int seqId = -1;
				byte[] head = characteristic.getValue();
				CKLOG.Debug(TAG, "frame 0 :" + logstring(head));
				// TODO 没做容错head[0]检测
				mReadNum = head[1];
				CKLOG.Debug(TAG, "onChangedCharacteristic mReadNum =" + mReadNum);
				int high = (head[3] << 8) & 0x0FFFF;
				int low = head[2] & 0x0FF;
				int length = high + low;
				CKLOG.Debug(TAG, "onChangedCharacteristic high = " + high + ", low = " + low);
				CKLOG.Debug(TAG, "onChangedCharacteristic length =" + length + ", head[3]=" + head[3] + ", head[2]=" + head[2]);

				// int highSeqId = (head[5] << 8) & 0x0FFFF;
				// int lowSeqId = head[4] & 0x0FF;
				int highSeqId = ((head[7] << 24) & 0x0FFFFFFFF) + ((head[6] << 16) & 0x0FFFFFF);
				int lowSeqId = ((head[5] << 8) & 0x0FFFF) + (head[4] & 0x0FF);
				seqId = highSeqId + lowSeqId;
				obj.seqId = seqId;
				CKLOG.Error(TAG, "onChangedCharacteristic highSeqId = " + highSeqId + ", lowSeqId = " + lowSeqId + ",seqId = " + seqId);

				mReadValue = new byte[length];
				mReadValueLength = length;
				++mAlreadRead;
				mAlreadReadByte = 0;
				return;
			}

			if (mAlreadRead > 0 && mAlreadRead <= mReadNum) {
				byte[] value = characteristic.getValue();
				CKLOG.Debug(TAG, "onChangedCharacteristic valuelength =" + value.length + ", seq=" + value[0]);
				for (int i = 1; i < value.length && mAlreadReadByte < mReadValueLength; ++i) {
					mReadValue[mAlreadReadByte++] = value[i];
				}
				if (mAlreadRead == mReadNum) {
					int high = (mReadValue[1] << 8) & 0x0FFFF;
					int low = mReadValue[0] & 0x0FF;
					int clientId = high + low;
					CKLOG.Debug(TAG, "onChangedCharacteristic clientId=" + clientId);
					mAlreadRead = 0;

					obj.device = gatt.getDevice();
					obj.serviceUuid = characteristic.getService().getUuid().toString();
					obj.characteristicUuid = characteristic.getUuid().toString();
					// byte[] b = new byte[mReadValue.length - 2];
					// for (int i = 0; i < b.length; i++) {
					// b[i] = mReadValue[i + 2];
					// }
					// String info = new String(b);
					String info = new String(mReadValue, 2, mReadValueLength - 2);
					String info2 = new String(mReadValue);
					CKLOG.Debug(TAG, info);
					CKLOG.Debug(TAG, info2);
					obj.characteristicValue = Utils.clonebytes(info.getBytes());
					obj.clientId = clientId;

					obj.seqId = seqId;
					CKLOG.Error(TAG, "onChangedCharacteristic highSeqId = " + "seqId = " + seqId);
					// obj.seqId = seqId;

					if (null != mInternalHandler) {
//						if (AliBLEUuidUtils.isNotificationFeedbackCharacteristicUuid(obj.characteristicUuid)) {
//							mInternalHandler.obtainMessage(BLEService.INTERNAL_COMMAND_ON_NOTIFICATION_FEEDBACK, obj).sendToTarget();
//						}
                        // CKLOG.Error(TAG,"到了这里...");
						mInternalHandler.obtainMessage(BLEService.INTERNAL_COMMAND_ON_CHARACTERISTIC_CHANGED, obj).sendToTarget();
					}
				} else {
					// readCharacteristicFromBLEDevice(obj.device,
					// obj.serviceUuid, obj.characteristicUuid);
					++mAlreadRead;
				}
			}
		}
	};

	public synchronized static BLEManager instance() {
		if (null == sInstance) {
			sInstance = new BLEManager();
		}
		return sInstance;
	}

	public void init(final Context context, final Handler handler) {
		CKLOG.Info(TAG, "init() ... ");
		mContext = context.getApplicationContext();
		mInternalHandler = handler;
		mBluetoothAdapter = ((BluetoothManager) sInstance.mContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
	}

	public void destroy() {
		CKLOG.Info(TAG, "destroy() ... ");
		clearAllDevices();
		clearReadCharacteristicTaskQueue();
		clearWriteCharacteristicTaskQueue();
		mContext = null;
		mInternalHandler = null;
		mBluetoothAdapter = null;
	}

	private void addScannedBLEDevice(final BluetoothDevice device) {
		synchronized (mScannedBLEDevicesLock) {
			if (null == device) {
				CKLOG.Info(TAG, "addScannedBLEDevice() : device is null , return. ");
				return;
			}
			final String add = device.getAddress();
			CKLOG.Info(TAG, "addScannedBLEDevice() : address - " + add);
			if (TextUtils.isEmpty(add)) {
				CKLOG.Info(TAG, "addScannedBLEDevice() : device address is empty , return. ");
				return;
			}
			if (mScannedBLEDevices.containsKey(add)) {
				CKLOG.Info(TAG, "addScannedBLEDevice() : device of this address existed , remove it firstly.");
				mScannedBLEDevices.remove(add);
			}
			mScannedBLEDevices.put(add, device);
			onScannedBLEDeviceUpdated();
		}
	}

	private void clearScannedBLEDevices() {
		synchronized (mScannedBLEDevicesLock) {
			CKLOG.Info(TAG, "clearScannedBLEDevices() ... ");
			if (!mScannedBLEDevices.isEmpty()) {
				mScannedBLEDevices.clear();
				onScannedBLEDeviceUpdated();
			}
		}
	}

	private void clearAllDevices() {
		CKLOG.Info(TAG, "clearAllDevices() ... ");
		clearScannedBLEDevices();
		clearConnectedBLEGatts();
	}

	public void onBluetoothStateChanged() {
		CKLOG.Info(TAG, "onBluetoothStateChanged() ... ");
		if (!isBluetoothEnabled()) {
			clearAllDevices();
		}
	}

	private void onScannedBLEDeviceUpdated() {
		if (null != mInternalHandler) {
			mInternalHandler.sendEmptyMessage(BLEService.INTERNAL_COMMAND_SCANNED_BLE_UPDATED);
		}
	}

	public void scanBLEDevices() {
		if (!isBluetoothEnabled()) {
			CKLOG.Info(TAG, "scanBLEDevices() : bluetooth not enabled, return.");
			return;
		}
		if (mIsScanningBLE) {
			CKLOG.Info(TAG, "scanBLEDevices() : scanning ...");
			mScanningStartTime = System.currentTimeMillis();
			CKLOG.Info(TAG, "scanBLEDevices() : mScanningStartTime - " + mScanningStartTime);
			// Re-call scan, means need to re-post time out runnable.
			if (null != mInternalHandler) {
				mInternalHandler.postDelayed(new ScanTimeOutRunnable(), SCAN_BLE_TIMEOUT);
			}
			return;
		}
		CKLOG.Info(TAG, "scanBLEDevices() ... ");
		clearScannedBLEDevices();
		mIsScanningBLE = true;
		mScanningStartTime = System.currentTimeMillis();
		CKLOG.Info(TAG, "scanBLEDevices() : mScanningStartTime - " + mScanningStartTime);
		if (null != mInternalHandler) {
			mInternalHandler.postDelayed(new ScanTimeOutRunnable(), SCAN_BLE_TIMEOUT);
		}
		mBluetoothAdapter.startLeScan(mLeScanCallback);
		if (null != mInternalHandler) {
			mInternalHandler.sendEmptyMessage(BLEService.INTERNAL_COMMAND_SCAN_BLE_START);
		}
	}

	private class ScanTimeOutRunnable implements Runnable {

		private long mPostTime;

		public ScanTimeOutRunnable() {
			mPostTime = System.currentTimeMillis();
			CKLOG.Info(TAG, "ScanTimeOutRunnable() : mPostTime - " + mPostTime);
		}

		@Override
		public void run() {
			if (mScanningStartTime > mPostTime) {
				CKLOG.Info(TAG, "ScanTimeOutRunnable.run() : new scan exist, no need to stop.");
				return;
			}
			CKLOG.Info(TAG, "ScanTimeOutRunnable.run() : try to stop scan for timeout, maybe no need to stop.");
			stopScanBLEDevices();
		}

	}

	public void stopScanBLEDevices() {
		if (!isBluetoothEnabled()) {
			CKLOG.Info(TAG, "stopScanBLEDevices() : bluetooth not enabled, return.");
			return;
		}
		if (!mIsScanningBLE) {
			CKLOG.Info(TAG, "stopScanBLEDevices() : not scanning , return.");
			return;
		}
		CKLOG.Info(TAG, "stopScanBLEDevices() ... ");
		mBluetoothAdapter.stopLeScan(mLeScanCallback);
		mIsScanningBLE = false;
		mScanningStartTime = 0;
		CKLOG.Info(TAG, "stopScanBLEDevices() : mScanningStartTime - " + mScanningStartTime);
		if (null != mInternalHandler) {
			mInternalHandler.sendEmptyMessage(BLEService.INTERNAL_COMMAND_SCAN_BLE_STOP);
		}
	}

	public List<BluetoothDevice> getScannedBLEDevices() {
		synchronized (mScannedBLEDevicesLock) {
			if (mScannedBLEDevices.isEmpty()) {
				return null;
			}
			final List<BluetoothDevice> ret = new ArrayList<BluetoothDevice>();
			ret.addAll(mScannedBLEDevices.values());
			return ret;
		}
	}

	public List<BluetoothDevice> getScannedBLEWatches() {
		synchronized (mScannedBLEDevicesLock) {
			if (mScannedBLEDevices.isEmpty()) {
				return null;
			}
			final List<BluetoothDevice> ret = new ArrayList<BluetoothDevice>();
			final Collection<BluetoothDevice> values = mScannedBLEDevices.values();
			for (BluetoothDevice d : values) {
				if (BLEUtils.isWatch(d)) {
					ret.add(d);
				}
			}
			return ret.isEmpty() ? null : ret;
		}
	}

	private void addConnectedBLEGatt(final BluetoothGatt gatt) {
		synchronized (mConnectedBLEGattsLock) {
			if (null == gatt) {
				CKLOG.Info(TAG, "addConnectedBLEDevice() : gatt null , return.");
				return;
			}
			final String add = gatt.getDevice().getAddress();
			if (TextUtils.isEmpty(add)) {
				CKLOG.Info(TAG, "addConnectedBLEDevice() : device address empty , return.");
				return;
			}
			if (mConnectedBLEGatts.containsKey(add)) {
				CKLOG.Info(TAG, "addConnectedBLEDevice() : device address in map already , rm it.");
				mConnectedBLEGatts.remove(add);
			}
			CKLOG.Info(TAG, "addConnectedBLEDevice() : put it into map , address - " + add);
			mConnectedBLEGatts.put(add, gatt);
		}
	}

	private void removeConnectedBLEGatt(final BluetoothGatt gatt) {
		synchronized (mConnectedBLEGattsLock) {
			if (null == gatt) {
				CKLOG.Info(TAG, "removeConnectedBLEDevice() : device is null , return. ");
				return;
			}
			final String add = gatt.getDevice().getAddress();
			CKLOG.Info(TAG, "removeConnectedBLEDevice() : address - " + add);
			if (TextUtils.isEmpty(add)) {
				CKLOG.Info(TAG, "removeConnectedBLEDevice() : device address is empty , return. ");
				return;
			}
			mConnectedBLEGatts.remove(add);
		}
	}

	private void clearConnectedBLEGatts() {
		synchronized (mConnectedBLEGattsLock) {
			if (!mConnectedBLEGatts.isEmpty()) {
				final Collection<BluetoothGatt> connectedGatts = mConnectedBLEGatts.values();
				for (BluetoothGatt gatt : connectedGatts) {
					if (null != gatt) {
						gatt.disconnect();
					}
				}
				mConnectedBLEGatts.clear();
			}
		}
	}

	private boolean isConnectedBLEDevice(final BluetoothDevice device) {
		synchronized (mConnectedBLEGattsLock) {
			if (null == device || TextUtils.isEmpty(device.getAddress())) {
				return false;
			}
			return mConnectedBLEGatts.containsKey(device.getAddress());
		}
	}

	private BluetoothGatt getConnectedBLEGatt(final String deviceAddress) {
		synchronized (mConnectedBLEGattsLock) {
			return mConnectedBLEGatts.get(deviceAddress);
		}
	}

	public boolean setBLEDeviceCharacteristicNotification(final BluetoothDevice device, final String serviceUuid, final String characteristicUuid,
			final boolean enable) {
		synchronized (mConnectedBLEGattsLock) {
			if (null == device) {
				CKLOG.Info(TAG, "setBLEDeviceCharacteristicNotification() : device null , return false.");
				return false;
			}
			if (!AliBLEUuidUtils.isValidUuidString(serviceUuid) || !AliBLEUuidUtils.isValidUuidString(characteristicUuid)) {
				CKLOG.Info(TAG, "setBLEDeviceCharacteristicNotification() : uuid invalid , return false.");
				return false;
			}
			final BluetoothGatt gatt = getConnectedBLEGatt(device.getAddress());
			if (null == gatt) {
				CKLOG.Info(TAG, "setBLEDeviceCharacteristicNotification() : no gatt , return false.");
				return false;
			}
			final BluetoothGattService service = gatt.getService(UUID.fromString(serviceUuid));
			if (null == service) {
				CKLOG.Info(TAG, "setBLEDeviceCharacteristicNotification() : no service , return false.");
				return false;
			}
			final BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUuid));
			if (null == characteristic) {
				CKLOG.Info(TAG, "setBLEDeviceCharacteristicNotification() : no characteristic , return false.");
				return false;
			}
			CKLOG.Info(TAG, "setBLEDeviceCharacteristicNotification() : device address - " + device.getAddress() + " , service uuid - " + serviceUuid
					+ " , characteristic uuid - " + characteristicUuid + " , enable - " + enable);
			gatt.setCharacteristicNotification(characteristic, enable);
			return true;
		}
	}

	public List<BluetoothDevice> getConnectedBLEDevices() {
		synchronized (mConnectedBLEGattsLock) {
			if (mConnectedBLEGatts.isEmpty()) {
				return null;
			}
			final List<BluetoothDevice> ret = new ArrayList<BluetoothDevice>();
			final Collection<BluetoothGatt> connectedGatts = mConnectedBLEGatts.values();
			for (BluetoothGatt gatt : connectedGatts) {
				if (null == gatt || null == gatt.getDevice()) {
					continue;
				}
				ret.add(gatt.getDevice());
			}
			return ret.isEmpty() ? null : ret;
		}
	}

	public List<BluetoothDevice> getConnectedBLEWatches() {
		synchronized (mConnectedBLEGattsLock) {
			final List<BluetoothDevice> list = getConnectedBLEDevices();
			if (null == list || list.isEmpty()) {
				return null;
			}
			final List<BluetoothDevice> ret = new ArrayList<BluetoothDevice>();
			for (BluetoothDevice d : list) {
				if (BLEUtils.isWatch(d)) {
					ret.add(d);
				}
			}
			return ret.isEmpty() ? null : ret;
		}
	}

	private void onBLEDeviceConnecting(final BluetoothDevice device) {
		if (null != mInternalHandler) {
			mInternalHandler.sendMessage(mInternalHandler.obtainMessage(BLEService.INTERNAL_COMMAND_BLE_CONNECTING, device));
		} else {
			CKLOG.Info(TAG, "onBLEDeviceConnecting() : mInternalHandler null.");
		}
	}

	private void onBLEDeviceConnected(final BluetoothDevice device) {
		if (null != mInternalHandler) {
			mInternalHandler.sendMessage(mInternalHandler.obtainMessage(BLEService.INTERNAL_COMMAND_BLE_CONNECTED, device));
		} else {
			CKLOG.Info(TAG, "onBLEDeviceConnected() : mInternalHandler null.");
		}
	}

	private void enableNotificationFeedback(final BluetoothGatt gatt) {
		if (!BLEUtils.isWatch(gatt.getDevice())) {
			return;
		}
		final BluetoothGattService service = gatt.getService(UUID.fromString(AliBLEUuid.NOTIFICATION_FEEDBACK_SERVICE));
		if (null == service) {
			return;
		}
		final BluetoothGattCharacteristic characteristic = service
				.getCharacteristic(UUID.fromString(AliBLEUuid.NOTIFICATION_FEEDBACK_CHARACTERISTIC));
		if (null == characteristic) {
			return;
		}
		gatt.setCharacteristicNotification(characteristic, true);
	}

	private void onBLEDeviceDisconnected(final BluetoothDevice device) {
		if (null != mInternalHandler) {
			mInternalHandler.sendMessage(mInternalHandler.obtainMessage(BLEService.INTERNAL_COMMAND_BLE_DISCONNECTED, device));
		} else {
			CKLOG.Info(TAG, "onBLEDeviceDisconnected() : mInternalHandler null.");
		}
	}

	public void connectToBLEDevice(final BluetoothDevice device, final boolean autoConnect) {
		if (!isBluetoothEnabled()) {
			CKLOG.Info(TAG, "connectToBTDevice() : by device, bluetooth not enabled, return.");
			return;
		}
		if (null == device) {
			CKLOG.Info(TAG, "connectToBTDevice() : by device, device is null, return.");
			return;
		}
		if (isConnectedBLEDevice(device)) {
			CKLOG.Info(TAG, "connectToBTDevice() : by device, device has connected already, return.");
			onBLEDeviceConnected(device);
			return;
		}
		CKLOG.Info(TAG, "connectToBTDevice() : by device, device address - " + device.getAddress() + ", autoConnect - " + autoConnect);
		device.connectGatt(mContext, autoConnect, mBluetoothGattCallback);
	}

	public void connectToBLEDevice(final String deviceAddress, final boolean autoConnect) {
		if (!isBluetoothEnabled()) {
			CKLOG.Info(TAG, "connectToBTDevice() : by address, bluetooth not enabled, return.");
			return;
		}
		if (!BluetoothAdapter.checkBluetoothAddress(deviceAddress)) {
			CKLOG.Info(TAG, "connectToBTDevice() : by address, not valid device address, return.");
			return;
		}
		final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
		if (null == device) {
			CKLOG.Info(TAG, "connectToBTDevice() : by address, no such device, return.");
			return;
		}
		CKLOG.Info(TAG, "connectToBTDevice() : by address, device address - " + deviceAddress + ", autoConnect - " + autoConnect);
		device.connectGatt(mContext, autoConnect, mBluetoothGattCallback);
	}

	public void disconnectFromBLEDevice(final String deviceAddress) {
		if (!isBluetoothEnabled()) {
			CKLOG.Info(TAG, "disconnectFromBLEDevice() : bluetooth not enabled, return.");
			return;
		}
		if (TextUtils.isEmpty(deviceAddress)) {
			CKLOG.Info(TAG, "disconnectFromBLEDevice() : deviceAddress empty, return.");
			return;
		}

		final BluetoothGatt connectedGatt = getConnectedBLEGatt(deviceAddress);
		if (null == connectedGatt) {
			CKLOG.Info(TAG, "disconnectFromBLEDevice() : device not connected, return.");
			onBLEDeviceDisconnected(connectedGatt.getDevice());
			return;
		}
		connectedGatt.disconnect();
	}

	private final Object mReadCharacteristicTaskQueueLock = new Object();
	private final ArrayList<ReadCharacteristicTask> mReadCharacteristicTaskQueue = new ArrayList<ReadCharacteristicTask>();

	private class ReadCharacteristicTask {
		private BluetoothGatt mBluetoothGatt;
		BluetoothGattCharacteristic mBluetoothGattCharacteristic;

		public ReadCharacteristicTask(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			mBluetoothGatt = gatt;
			mBluetoothGattCharacteristic = characteristic;
		}

		public void run() {
			if (null == mBluetoothGatt) {
				CKLOG.Info(TAG, "ReadCharacteristicTask.run() : gatt null , return.");
				return;
			}
			if (null == mBluetoothGattCharacteristic) {
				CKLOG.Info(TAG, "ReadCharacteristicTask.run() : characteristic null , return.");
				return;
			}
			CKLOG.Info(TAG, "ReadCharacteristicTask.run() : device address - " + mBluetoothGatt.getDevice().getAddress() + " , chara uuid - "
					+ mBluetoothGattCharacteristic.getUuid());
			mBluetoothGatt.readCharacteristic(mBluetoothGattCharacteristic);
		}

		public void clear() {
			mBluetoothGatt = null;
			mBluetoothGattCharacteristic = null;
		}
	}

	private void excuteReadCharacteristic(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		synchronized (mReadCharacteristicTaskQueueLock) {
			final ReadCharacteristicTask task = new ReadCharacteristicTask(gatt, characteristic);
			final boolean canExcuteNow = mReadCharacteristicTaskQueue.isEmpty();
			mReadCharacteristicTaskQueue.add(task);
			if (canExcuteNow) {
				CKLOG.Info(TAG, "excuteReadCharacteristic() : task run now.");
				task.run();
			} else {
				CKLOG.Info(TAG, "excuteReadCharacteristic() : task in waiting queue now.");
			}
		}
	}

	public void onReadCharacteristic() {
		synchronized (mReadCharacteristicTaskQueueLock) {
			CKLOG.Info(TAG, "onReadCharacteristic() ... ");
			if (mReadCharacteristicTaskQueue.isEmpty()) {
				CKLOG.Info(TAG, "onReadCharacteristic() : read task queue empty, return.");
				return;
			}
			// remove task from queue and clear it.
			mReadCharacteristicTaskQueue.remove(0).clear();
			if (mReadCharacteristicTaskQueue.isEmpty()) {
				CKLOG.Info(TAG, "onReadCharacteristic() : no one in read task queue, return.");
				return;
			}
			mReadCharacteristicTaskQueue.get(0).run();
		}
	}

	private void clearReadCharacteristicTaskQueue() {
		synchronized (mReadCharacteristicTaskQueueLock) {
			CKLOG.Info(TAG, "clearReadCharacteristicTaskQueue() ... ");
			mReadCharacteristicTaskQueue.clear();
		}
	}

	private void removeReadCharacteristicTask(final BluetoothDevice device) {
		synchronized (mReadCharacteristicTaskQueueLock) {
			CKLOG.Info(TAG, "removeReadCharacteristicTask() : remove tasks to device address - " + device.getAddress());
			if (mReadCharacteristicTaskQueue.isEmpty()) {
				CKLOG.Info(TAG, "removeReadCharacteristicTask() : task queue empty itself, return.");
				return;
			}
			final String address = device.getAddress();
			final ArrayList<ReadCharacteristicTask> toRemove = new ArrayList<ReadCharacteristicTask>(mReadCharacteristicTaskQueue.size());
			for (ReadCharacteristicTask task : mReadCharacteristicTaskQueue) {
				if (address.equals(task.mBluetoothGatt.getDevice().getAddress())) {
					toRemove.add(task);
				}
			}
			if (toRemove.isEmpty()) {
				CKLOG.Info(TAG, "removeReadCharacteristicTask() : no task for this device, return.");
				return;
			}
			CKLOG.Info(TAG, "removeReadCharacteristicTask() : count to remove - " + toRemove.size());
			mReadCharacteristicTaskQueue.removeAll(toRemove);
		}
	}

	private final Object mWriteCharacteristicTaskQueueLock = new Object();
	private final ArrayList<WriteCharacteristicTask> mWriteCharacteristicTaskQueue = new ArrayList<WriteCharacteristicTask>();

	private class WriteCharacteristicTask {
		private BluetoothGatt mBluetoothGatt;
		private BluetoothGattCharacteristic mBluetoothGattCharacteristic;
		private byte[] mContent;

		public WriteCharacteristicTask(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] content) {
			mBluetoothGatt = gatt;
			mBluetoothGattCharacteristic = characteristic;
			mContent = Utils.clonebytes(content);
		}

		public void run() {
			if (null == mBluetoothGatt) {
				CKLOG.Info(TAG, "WriteCharacteristicTask.run() : gatt null , return.");
				return;
			}
			if (null == mBluetoothGattCharacteristic) {
				CKLOG.Info(TAG, "WriteCharacteristicTask.run() : characteristic null , return.");
				return;
			}
			// CKLOG.Info(TAG, "WriteCharacteristicTask.run() : device address - " +
			// mBluetoothGatt.getDevice().getAddress()
			// + " , chara uuid - " + mBluetoothGattCharacteristic.getUuid() +
			// " , content - "
			// + new String(mContent));
			CKLOG.Info(TAG, "sending package: " + (mContent[0] & 0xff));
			mBluetoothGattCharacteristic.setValue(mContent);
			mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
		}

		public void clear() {
			mBluetoothGatt = null;
			mBluetoothGattCharacteristic = null;
			mContent = null;
		}
	}

	private void excuteWriteCharacteristic(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final byte[] value) {
		synchronized (mWriteCharacteristicTaskQueueLock) {
			final WriteCharacteristicTask task = new WriteCharacteristicTask(gatt, characteristic, value);
			final boolean canExcuteNow = mWriteCharacteristicTaskQueue.isEmpty();
			mWriteCharacteristicTaskQueue.add(task);
			if (canExcuteNow) {
				CKLOG.Info(TAG, "excuteWriteCharacteristic() : task run now.");
				task.run();
			} else {
				CKLOG.Info(TAG, "excuteWriteCharacteristic() : task in waiting queue now.");
			}
		}
	}

	public void onWriteCharacteristic() {
		synchronized (mWriteCharacteristicTaskQueueLock) {
			CKLOG.Info(TAG, "onWriteCharacteristic() ... ");
			if (mWriteCharacteristicTaskQueue.isEmpty()) {
				CKLOG.Info(TAG, "onWriteCharacteristic() : write task queue empty, return.");
				return;
			}
			// remove task from queue and clear it.
			mWriteCharacteristicTaskQueue.remove(0).clear();
			if (mWriteCharacteristicTaskQueue.isEmpty()) {
				CKLOG.Info(TAG, "onWriteCharacteristic() : no one in write task queue, return.");
				return;
			}
			mWriteCharacteristicTaskQueue.get(0).run();
		}
	}

	private void clearWriteCharacteristicTaskQueue() {
		synchronized (mWriteCharacteristicTaskQueueLock) {
			mWriteCharacteristicTaskQueue.clear();
		}
	}

	private void removeWriteCharacteristicTask(final BluetoothDevice device) {
		synchronized (mWriteCharacteristicTaskQueueLock) {
			CKLOG.Info(TAG, "removeWriteCharacteristicTask() : remove tasks to device address - " + device.getAddress());
			if (mWriteCharacteristicTaskQueue.isEmpty()) {
				CKLOG.Info(TAG, "removeWriteCharacteristicTask() : task queue empty itself, return.");
				return;
			}
			final String address = device.getAddress();
			final ArrayList<WriteCharacteristicTask> toRemove = new ArrayList<WriteCharacteristicTask>(mWriteCharacteristicTaskQueue.size());
			for (WriteCharacteristicTask task : mWriteCharacteristicTaskQueue) {
				if (address.equals(task.mBluetoothGatt.getDevice().getAddress())) {
					toRemove.add(task);
				}
			}
			if (toRemove.isEmpty()) {
				CKLOG.Info(TAG, "removeWriteCharacteristicTask() : no task for this device, return.");
				return;
			}
			CKLOG.Info(TAG, "removeWriteCharacteristicTask() : count to remove - " + toRemove.size());
			mWriteCharacteristicTaskQueue.removeAll(toRemove);
		}
	}

	public void sendNotificationToBLEDevice(final String address, final String serviceUuid, final String characteristicUuid,
			final String messageContent, final IAliBLESendStateCallback callback) {
		if (!isBluetoothEnabled()) {
			CKLOG.Info(TAG, "sendMessgeToBluetoothDevice() : bluetooth not enabled, return.");
			excuteSendNotificationCallbackFail(callback, address, messageContent, IAliBLESendStateCallback.FAIL_CODE_BT_OFF);
			return;
		}
		if (TextUtils.isEmpty(messageContent)) {
			CKLOG.Info(TAG, "sendMessgeToBluetoothDevice() : message empty, return.");
			return;
		}
		if (null == address) {
			CKLOG.Info(TAG, "sendMessgeToBluetoothDevice() : address null, return.");
			return;
		}
		if (!AliBLEUuidUtils.isValidUuidString(serviceUuid)) {
			CKLOG.Info(TAG, "sendMessgeToBluetoothDevice() : serviceUuid invalid, return.");
			return;
		}
		if (!AliBLEUuidUtils.isValidUuidString(characteristicUuid)) {
			CKLOG.Info(TAG, "sendMessgeToBluetoothDevice() : messageUuid invalid, return.");
			return;
		}
		final String deviceAddress = address;
		if (TextUtils.isEmpty(deviceAddress)) {
			CKLOG.Info(TAG, "sendMessgeToBluetoothDevice() : deviceAddress empty, return.");
			return;
		}
		final BluetoothGatt connectedGatt = getConnectedBLEGatt(deviceAddress);
		if (null == connectedGatt) {
			CKLOG.Info(TAG, "sendMessgeToBluetoothDevice() : gatt null, return.");
			excuteSendNotificationCallbackFail(callback, address, messageContent, IAliBLESendStateCallback.FAIL_CODE_DEVICE_NOT_CONNECTED);
			return;
		}

		final BluetoothGattService gattService = connectedGatt.getService(UUID.fromString(serviceUuid));
		if (null == gattService) {
			CKLOG.Info(TAG, "sendMessgeToBluetoothDevice() : gatt service " + serviceUuid + " not exist, return.");
			return;
		}

		final BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(characteristicUuid));
		if (null == characteristic) {
			CKLOG.Info(TAG, "sendMessgeToBluetoothDevice() : characteristic " + characteristicUuid + " not exist, return.");
			return;
		}

		CKLOG.Info(TAG, "sendMessgeToBluetoothDevice() : address address - " + address + " , serviceUuid - " + serviceUuid
				+ " , messageUuid - " + characteristicUuid + " , messageContent - " + messageContent);

		writeNotificationCharacteristic(connectedGatt, characteristic, messageContent, callback);
	}

	private void writeNotificationCharacteristic(final BluetoothGatt bluetoothGatt, final BluetoothGattCharacteristic characteristic,
			final String notification, final IAliBLESendStateCallback callback) {

		characteristic.setValue(notification);
		CKLOG.Info(TAG, "writeNotificationCharacteristic() : begin write.");
		final byte size = 15;
		byte[] value = characteristic.getValue();
		CKLOG.Info(TAG, "writeNotificationCharacteristic() : value length - " + value.length);
		final StringBuilder stringBuilders = new StringBuilder(value.length);
		for (byte byteChar : value)
			stringBuilders.append(String.format("%02X ", byteChar));
		// CKLOG.Info(TAG, "writeNotificationCharacteristic() : origin value - " +
		// stringBuilders);
		// CKLOG.Info(TAG, "writeNotificationCharacteristic() : wear - " +
		// characteristic.getStringValue(0));
		byte[] temp = new byte[size + 2];
		int num = value.length / size + ((value.length % size) > 0 ? 1 : 0);
		CKLOG.Info(TAG, "total packages: " + num + "  value.leng = " + value.length);
		int i = 0;
		byte[] head = new byte[4];
		head[0] = (byte) i;
		head[1] = (byte) num;
		short length = (short) value.length;
		head[2] = (byte) (length & 0xff);
		head[3] = (byte) ((length >> 8) & 0xff);
		// characteristic.setValue(head);
		// bluetoothGatt.writeCharacteristic(characteristic);
		excuteWriteCharacteristic(bluetoothGatt, characteristic, head);
		CKLOG.Info(TAG, "writeNotificationCharacteristic() : wear send - " + characteristic.getStringValue(0));

		while (true) {
			++i;
			if (i <= num) {
				clearByte(temp);
				temp[0] = (byte) i;
				byte tempSize;
				if ((value.length - (i - 1) * size) > size) {
					tempSize = size;
				} else {
					tempSize = (byte) (value.length - (i - 1) * size);
				}
				temp[1] = tempSize > size ? size : tempSize;
				for (byte j = 0; j < temp[1]; j++) {
					temp[j + 2] = value[(i - 1) * size + j];
					if (j == 0) {
						CKLOG.Info(TAG, "writeNotificationCharacteristic() : " + value[(i - 1) * size + j] + ":" + temp[j + 2]);
					}
				}
				// characteristic.setValue(temp);
				final StringBuilder stringBuilder = new StringBuilder(temp.length);
				for (byte byteChar : temp) {
					stringBuilder.append(String.format("%02X ", byteChar));
				}
				CKLOG.Info(TAG, "writeNotificationCharacteristic() : wear : send----" + i + " " + characteristic.getStringValue(0) + "\n" + stringBuilder);
				// bluetoothGatt.writeCharacteristic(characteristic);
				excuteWriteCharacteristic(bluetoothGatt, characteristic, temp);
			} else {
				break;
			}

		}

		CKLOG.Info(TAG, "writeNotificationCharacteristic() : all write finished ... ");
		excuteSendNotificationCallbackSuccess(callback, bluetoothGatt.getDevice(), notification);
	}

	private void excuteSendNotificationCallbackSuccess(final IAliBLESendStateCallback callback, final BluetoothDevice device,
			final String notification) {
		if (null == callback) {
			CKLOG.Info(TAG, "excuteSendNotificationCallbackSuccess() : callback null, return.");
			return;
		}
		CKLOG.Info(TAG, "excuteSendNotificationCallbackSuccess() ... ");
		callback.onSendMessageCompleted(device.getAddress(), notification);
	}

	private void excuteSendNotificationCallbackFail(final IAliBLESendStateCallback callback, final String address,
			final String notification, final int failCode) {
		if (null == callback) {
			CKLOG.Info(TAG, "excuteSendNotificationCallbackFail() : callback null, return.");
			return;
		}
		CKLOG.Info(TAG, "excuteSendNotificationCallbackFail() : fail code - " + failCode);
		callback.onSendMessageFailed(address, notification, failCode);
	}

	private void clearByte(byte[] temp) {
		CKLOG.Info(TAG, "clearByte() ... ");
		for (int i = 0; i < temp.length; i++) {
			temp[i] = 0;
		}
	}

	public void writeCharacteristicToBLEDevice(final BluetoothDevice device, final String serviceUuid, final String characteristicUuid,
			final String content) {
		if (!isBluetoothEnabled()) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : bluetooth not enabled, return.");
			return;
		}
		if (null == device) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : device null, return.");
			return;
		}
		if (TextUtils.isEmpty(content)) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : content empty, return.");
			return;
		}
		if (!AliBLEUuidUtils.isValidUuidString(serviceUuid)) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : serviceUuid invalid, return.");
			return;
		}
		if (!AliBLEUuidUtils.isValidUuidString(characteristicUuid)) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : messageUuid invalid, return.");
			return;
		}
		final String deviceAddress = device.getAddress();
		if (TextUtils.isEmpty(deviceAddress)) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : deviceAddress empty, return.");
			return;
		}
		final BluetoothGatt connectedGatt = getConnectedBLEGatt(deviceAddress);
		if (null == connectedGatt) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : gatt null, return.");
			return;
		}

		CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : device address - " + device.getAddress() + " , serviceUuid - " + serviceUuid
				+ " , characteristicUuid - " + characteristicUuid + " , content - " + content);

		final BluetoothGattService service = connectedGatt.getService(UUID.fromString(serviceUuid));
		if (null == service) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : BluetoothGattService null, return.");
			return;
		}
		final BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUuid));
		if (null == characteristic) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : BluetoothGattCharacteristic null, return.");
			return;
		}

		excuteWriteCharacteristic(connectedGatt, characteristic, content.getBytes());

	}

	public void writeCharacteristicToBLEDevice(final BluetoothDevice device, final String serviceUuid, final String characteristicUuid,
			final byte[] content) {
		if (!isBluetoothEnabled()) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : bluetooth not enabled, return.");
			return;
		}
		if (null == device) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : device null, return.");
			return;
		}
		if (null == content) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : content null, return.");
			return;
		}
		if (!AliBLEUuidUtils.isValidUuidString(serviceUuid)) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : serviceUuid invalid, return.");
			return;
		}
		if (!AliBLEUuidUtils.isValidUuidString(characteristicUuid)) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : messageUuid invalid, return.");
			return;
		}
		final String deviceAddress = device.getAddress();
		if (TextUtils.isEmpty(deviceAddress)) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : deviceAddress empty, return.");
			return;
		}
		final BluetoothGatt connectedGatt = getConnectedBLEGatt(deviceAddress);
		if (null == connectedGatt) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : gatt null, return.");
			return;
		}

		CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : device address - " + device.getAddress() + " , serviceUuid - " + serviceUuid
				+ " , characteristicUuid - " + characteristicUuid + " , content - " + content);

		final BluetoothGattService service = connectedGatt.getService(UUID.fromString(serviceUuid));
		if (null == service) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : BluetoothGattService null, return.");
			return;
		}
		final BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUuid));
		if (null == characteristic) {
			CKLOG.Info(TAG, "writeCharacteristicToBLEDevice() : BluetoothGattCharacteristic null, return.");
			return;
		}

		excuteWriteCharacteristic(connectedGatt, characteristic, content);

	}

	public void readCharacteristicToBLEDevice(final BluetoothDevice device, final String serviceUuid, final String characteristicUuid) {
		if (!isBluetoothEnabled()) {
			CKLOG.Info(TAG, "readCharacteristicToBLEDevice() : bluetooth not enabled, return.");
			return;
		}
		if (null == device) {
			CKLOG.Info(TAG, "readCharacteristicToBLEDevice() : device null, return.");
			return;
		}
		if (!AliBLEUuidUtils.isValidUuidString(serviceUuid)) {
			CKLOG.Info(TAG, "readCharacteristicToBLEDevice() : serviceUuid invalid, return.");
			return;
		}
		if (!AliBLEUuidUtils.isValidUuidString(characteristicUuid)) {
			CKLOG.Info(TAG, "readCharacteristicToBLEDevice() : messageUuid invalid, return.");
			return;
		}
		final String deviceAddress = device.getAddress();
		if (TextUtils.isEmpty(deviceAddress)) {
			CKLOG.Info(TAG, "readCharacteristicToBLEDevice() : deviceAddress empty, return.");
			return;
		}
		final BluetoothGatt connectedGatt = getConnectedBLEGatt(deviceAddress);
		if (null == connectedGatt) {
			CKLOG.Info(TAG, "readCharacteristicToBLEDevice() : gatt null, return.");
			return;
		}

		CKLOG.Info(TAG, "readCharacteristicToBLEDevice() : device address - " + device.getAddress() + " , serviceUuid - " + serviceUuid
				+ " , characteristicUuid - " + characteristicUuid);

		final BluetoothGattService service = connectedGatt.getService(UUID.fromString(serviceUuid));
		if (null == service) {
			CKLOG.Info(TAG, "readCharacteristicToBLEDevice() : BluetoothGattService null, return.");
			return;
		}
		final BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUuid));
		if (null == characteristic) {
			CKLOG.Info(TAG, "readCharacteristicToBLEDevice() : BluetoothGattCharacteristic null, return.");
			return;
		}
		// connectedGatt.readCharacteristic(characteristic);
		excuteReadCharacteristic(connectedGatt, characteristic);
	}

	public boolean isBluetoothEnabled() {
		return null != mBluetoothAdapter && mBluetoothAdapter.isEnabled();
	}
}
