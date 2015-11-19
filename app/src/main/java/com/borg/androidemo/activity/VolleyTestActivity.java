package com.borg.androidemo.activity;

import android.app.Activity;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.borg.androidemo.R;
import com.borg.androidemo.common.log.LogHelper;

import org.json.JSONObject;
//https://github.com/mcxiaoke/android-volley
public class VolleyTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley_test);
        mQueue =Volley.newRequestQueue(getApplicationContext());
        stringRequest();
        jsonObjectRequest();

    }

    private RequestQueue mQueue;
    private void stringRequest(){
        StringRequest stringRequest = new StringRequest("http://www.baidu.com",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogHelper.d("onResponse:"+response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogHelper.d("onErrorResponse:" + error.getMessage());
                    }
                });
        mQueue.add(stringRequest);
    }

    private void jsonObjectRequest(){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://m.weather.com.cn/data/101010100.html", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogHelper.d("JsonObjectRequest onResponse:"+response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogHelper.d("JsonObjectRequest onErrorResponse:"+error.getMessage());
                    }
        });
        mQueue.add(jsonObjectRequest);
    }
}
