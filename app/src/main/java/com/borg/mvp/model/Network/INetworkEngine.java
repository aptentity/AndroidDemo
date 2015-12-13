package com.borg.mvp.model.Network;

/**
 * Created by gulliver on 15/12/13.
 */

import com.borg.mvp.model.DataStructure.NameValuePair;

import java.util.List;

public interface INetworkEngine {
    public void post(String url,List<NameValuePair> params,INetworkCallback reponse);
    public void get(String url,List<NameValuePair> params,INetworkCallback reponse);
}
