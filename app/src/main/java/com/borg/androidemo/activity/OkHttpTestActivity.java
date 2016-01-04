package com.borg.androidemo.activity;

import android.app.Activity;
import android.os.Bundle;

import com.borg.androidemo.R;
import com.borg.mvp.utils.LogHelper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class OkHttpTestActivity extends Activity {
    private final OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http_test);

        String url="http://www.baidu.com";
        RequestBody formBody = new FormEncodingBuilder()
                .add("platform", "android").add("name", "bug")
                .add("subject", "XXXXXXXXXXXXXXX").build();
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
