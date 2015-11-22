package com.borg.androidemo.devices.connection.bluetooth.ble.utils;


import com.borg.androidemo.common.utils.CKLOG;
import com.borg.androidemo.devices.protocol.JsonProtocolConstant;

import org.json.JSONException;
import org.json.JSONObject;


public class NotificationUtils {

    private static final String TAG = "NotificationUtils";

    // public static String generateNotification(final String catigory,
    // final String titleText, final String titleIcon,
    // final String content, final String background) {
    //
    // final JSONObject json = new JSONObject();
    // try {
    // json.put("catigory", catigory);
    // json.put("title_text", titleText);
    // json.put("title_icon", titleIcon);
    // json.put("text", content);
    // json.put("bkground", background);
    // } catch (JSONException e) {
    // Log.e(TAG,
    // "generateNotification() : JSONException - "
    // + e.getMessage(), e);
    // return null;
    // }
    // return json.toString();
    // }

    public static JSONObject generateNotification(final String category,
                                                  final JSONObject content) {

        final JSONObject jObj = new JSONObject();
        try {
            jObj.put(JsonProtocolConstant.JSON_CATIGORY, category);
            jObj.put(JsonProtocolConstant.JSON_CONTENT, content);
            return jObj;
        } catch (JSONException e) {
            CKLOG.Error(TAG,
                    "generateNotification() : JSONException - "
                            + e.getMessage());
        }
        return null;
    }

}
