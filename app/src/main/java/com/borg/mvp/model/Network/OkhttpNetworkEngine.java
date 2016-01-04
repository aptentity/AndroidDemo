package com.borg.mvp.model.Network;

import com.borg.mvp.utils.LogHelper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import com.borg.mvp.model.DataStructure.NameValuePair;

import java.io.IOException;
import java.util.List;

/**
 * Created by gulliver on 15/12/13.
 */
public class OkhttpNetworkEngine implements INetworkEngine{
    private final OkHttpClient client = new OkHttpClient();
    @Override
    public void post(String url, List<NameValuePair> params, INetworkCallback reponse) {

    }

    @Override
    public void get(String url, List<NameValuePair> params, INetworkCallback reponse) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        if (params!=null){
            for (NameValuePair p: params) {
                builder.add(p.getName(),p.getValue());
            }
        }
        RequestBody formBody = builder.build();
        Request request = new Request.Builder().url(
                url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LogHelper.d("Unexpected code" + response);
                }

                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    System.out.println(responseHeaders.name(i) + ""
                            + responseHeaders.value(i));
                }
                LogHelper.d("-----------------------result-------------------------");
                LogHelper.d(response.body().string());
            }

            @Override
            public void onFailure(Request arg0, IOException arg1) {
                LogHelper.d("onFailure");
            }
        });
    }
}
