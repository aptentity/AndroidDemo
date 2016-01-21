package com.borg.mvp.model.Network;

/**
 * Created by gulliver on 15/12/13.
 */
public interface INetworkCallback {
    public void onSuccess(String result);
    public void onFail(int code,String result);
}
