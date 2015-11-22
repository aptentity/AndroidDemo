package com.borg.androidemo.devices.protocol;

/**
 * Created by yiping.cyp on 2015/9/21.
 */
public interface ConnectionResponseCode {
    /**
     * 超时失败
     */
    public static final int FAIL_TIME_OUT = -1;
    /**
     * BT请求发送失败
     */
    public static final int FAIL_SENDING_BT_REQ = -2;
    /**
     * BT连接断开
     */
    public static final int FAIL_BT_CONNECTION = -3;
    /**
     * JSON异常失败
     */
    public static final int FAIL_JSON = -5;
}
