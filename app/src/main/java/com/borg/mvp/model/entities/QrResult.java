package com.borg.mvp.model.entities;

import org.json.JSONObject;

/**
 * Created by Gulliver(feilong) on 16/1/21.
 */
public class QrResult {
    private boolean success;
    private String msg;
    private String url;
    private long time;
    private String at;
    public QrResult(String result){
        try{
            JSONObject object = new JSONObject(result);
            success = object.getBoolean("success");
            msg = object.getString("message");
            url = object.getString("url");
            time = object.getLong("t");
            at = object.getString("at");
        }catch (Exception e){

        }
    }

    public String getAt() {
        return at;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public String getUrl() {
        return url;
    }

    public long getTime() {
        return time;
    }
}
