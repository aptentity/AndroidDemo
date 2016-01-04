package com.borg.androidemo.aes;

import android.app.Activity;
import android.os.Bundle;

import com.borg.androidemo.R;
import com.borg.mvp.utils.LogHelper;
import com.borg.androidemo.common.utils.AESCipher;
public class AesTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aes_test);
        String src = "1234567890";
        String text = AESEncrypt(src);
        String text1 = AESDecrypt(text);
        LogHelper.d("src="+src);
        LogHelper.d("AESEncrypt="+text);
        LogHelper.d("AESDecrypt="+text1);

    }

    private String key = "a2c7d449f84e013e";

    private String AESEncrypt(String src){
        try{
            return AESCipher.encrypt(key,src);
        }catch (Exception e){return "";}
    }

    private String AESDecrypt(String src){
        try{
            return  AESCipher.decrypt(key,src);
        }catch (Exception e){
            return "";
        }
    }
}
