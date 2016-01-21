package com.borg.mvp.model.entities;

import org.json.JSONObject;

/**
 * Created by Gulliver(feilong) on 16/1/21.
 * 格式：{"code":"10004","message":"QRCode expired!code=1, msg=data not exist","success":true}
 * {"code":"10006","message":"success","success":true,"token":"123sdjr74832sdefr876d423kr432se3","nick":"test"}
 * 状态码
 * LOGIN_START(10000, "login start state"),
 * LOGIN_SCAN_SUCCESS(10001, "mobile scan QRCode success"),
 * LOGIN_SCAN_FIELD(10002, "mobile scan QRCode failed"),
 * LOGIN_CANCLE(10003, "mobile login cancel"),
 * LOGIN_EXPIRED(10004, "QRCode expired"),
 * LOGIN_FAILED(10005, "login failed"),
 * LOGIN_SUCCESS(10006, "login success"),
 * LOGIN_WAITTING_SURE(10007,"waitting pc sure to login mobile");
 * 详见：http://baike.corp.taobao.com/index.php/%E4%BA%8C%E7%BB%B4%E7%A0%81%E7%99%BB%E5%BD%95
 */
public class QrLoginResult {
    private boolean success;
    private String code;
    private String msg;
    private String token;
    private String nick;

    public QrLoginResult(String result){
        try{
            JSONObject object = new JSONObject(result);
            success = object.getBoolean("success");
            code = object.getString("code");
            msg = object.getString("message");
            token = object.optString("token");
            nick = object.optString("nick");
        }catch (Exception e){}
    }

    public boolean isSuccess() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getToken() {
        return token;
    }

    public String getNick() {
        return nick;
    }

    public static final String LOGIN_START = "10000";
    public static final String LOGIN_SCAN_SUCCESS = "10001";
    public static final String LOGIN_SCAN_FIELD = "10002";
    public static final String LOGIN_CANCLE = "10003";
    public static final String LOGIN_EXPIRED = "10004";
    public static final String LOGIN_FAILED = "10005";
    public static final String LOGIN_SUCCESS = "10006";
    public static final String LOGIN_WAITTING_SURE = "10007";
}
