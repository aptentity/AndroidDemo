package com.borg.androidemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.borg.androidemo.R;
import com.borg.androidemo.bean.UserInfo;
import com.borg.androidemo.common.log.LogHelper;

public class FastjsonTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fastjson_test);

        UserInfo user = new UserInfo();
        user.setMid("");
        user.setHeadimgurl("http");
        user.setSex("m");
        user.setPlatform("weibo");
        user.setUid("1234567890");
        user.setNickname("haha");
        user.setCountry("中国");
        user.setProvince("北京");
        user.setDeviceid("11111111");
        user.setDevicemodel("meteorite");
        user.setDevicevendor("abc");

        String jsonString = JSON.toJSONString(user);
        LogHelper.d(jsonString);

        UserInfo temp = JSON.parseObject(jsonString, UserInfo.class);
        LogHelper.d(temp.toString());
    }
}
