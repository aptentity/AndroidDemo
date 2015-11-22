package com.borg.androidemo.devices.protocol;

/**
 *
 * @author yiping.cyp
 * @ClassName: ResponseCode
 * @Version 1.0
 * @date 2015-8-14 上午9:54:53
 * @description response code for callback and listeners
 */
public interface ResponseCode extends ConnectionResponseCode {

    /**
     * callback category错误
     */
    public static final int FAIL_TIMEOUT = -1;

    public static final int FAIL_CATEGORY = -6;
    /**
     * HTTP请求失败
     */
    public static final int FAIL_HTTP_REQ = -7;
    /**
     * 日程为空
     */
    public static final int FAIL_EMPTY_CALENDAR = -8;
    /**
     * 参数错误
     */
    public static final int FAIL_PARAM = -10;
    /**
     * cmns错误
     */
    public static final int FAIL_CMNS = -11;
    /**
     * 远程异常失败
     */
    public static final int FAIL_REMOTE_EXCEPTION = -12;
    /**
     * BT返回内容错误
     */
    public static final int FAIL_BT_RESPONSE_ERROR = -13;
    /**
     * CUUID两次请求失败，表明CUUID拿不到
     */
    public static final int FAIL_GET_CUUID_TWICE = -14;
    /**
     * CUUID初始化失败
     */
    public static final int FAIL_INIT_CUUID = -15;
    /**
     * 从手表拿CUUID失败
     */
    public static final int FAIL_GET_CUUID = -16;
    /**
     * 拿deviceId失败
     */
    public static final int FAIL_GET_DEVICE_ID = -17;
    /**
     * 从手表拿设备信息失败
     */
    public static final int FAIL_DEV_INFO = -18;
    /**
     * 其他原因或异常导致的失败
     */
    public static final int FAIL_OTHER = -19;

    /**
     * 失败，由于没有登录
     */
    public static final int FAIL_UNLOGINED = -25;

    /**
     * 失败由于没有拿到deviceToken
     */
    public static final int FAIL_GET_DEVICE_TOKEN = -31;

    /**
     * 成功
     */
    public static final int SUCCESS = 0;

}
