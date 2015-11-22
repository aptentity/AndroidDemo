package com.borg.androidemo.devices.api;

/**
 * Created by wuzonglu on 15/11/5.
 */
public enum BindType {
    /**
     * 手机通过蓝牙BT方式做绑定
     */
    BINDER_BT,

    /**
     * 手机通过蓝牙BLE方式做绑定
     */
    BINDER_BLE,

    /**
     * inwatch手表的绑定
     */
    BINDER_INWATCH,

    /**二维码扫描结果是json格式的，用这种类型来绑定
     *
     */
    BINDER_JSONINFO
}
