package com.borg.mvp.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.borg.androidemo.R;
import com.borg.mvp.utils.LogHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class SettingsActivity extends RoboActivity implements View.OnClickListener{
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static final int FALLBACK_REQUEST = 101;

    @InjectView(R.id.btn_lockscren)
    private Button mBtnLockSreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mBtnLockSreen.setOnClickListener(this);
        isPatternEnable();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_lockscren:
                openPatterLockScreen();
                break;
        }
    }

    /**
     * 打开图案解锁ui
     * 由于AliChooseLockPattern的exported属性为false，所以需要修改setting的代码
     */
    private void openPatterLockScreen(){
        Intent i = createIntent(false, false, false);
        startActivityForResult(i,FALLBACK_REQUEST);
    }
    public final static String LOCKSCREEN_BIOMETRIC_WEAK_FALLBACK = "lockscreen.biometric_weak_fallback";
    public static final String CONFIRM_CREDENTIALS = "confirm_credentials";
    public static final String EXTRA_REQUIRE_PASSWORD = "extra_require_password";
    public static Intent createIntent(final boolean isFallback,boolean requirePassword, boolean confirmCredentials) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings","com.android.settings.aliyun.AliChooseLockPattern");
        intent.putExtra("key_lock_method", "pattern");
        intent.putExtra(CONFIRM_CREDENTIALS, confirmCredentials);
        intent.putExtra(LOCKSCREEN_BIOMETRIC_WEAK_FALLBACK, isFallback);
        intent.putExtra(EXTRA_REQUIRE_PASSWORD, requirePassword);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogHelper.d(TAG,"onActivityResult requestCode="+requestCode+" ;resultCode="+resultCode);
    }

    /**
     * 图案锁屏是否开启
     * 使用反射方式获取
     */
    private boolean isPatternEnable(){
        try{
            String className="com.android.internal.widget.LockPatternUtils";
            Class clazz=Class.forName(className);
            Constructor constructor=clazz.getConstructor(Context.class);
            Object object=constructor.newInstance(getApplicationContext());
            Method isLockPatternEnabled=clazz.getMethod("isLockPatternEnabled");
            Boolean isPatternEnable = (Boolean)isLockPatternEnabled.invoke(object);
            if (isPatternEnable){
                LogHelper.d(TAG,"isLockPatternEnabled is on");
            }else {
                LogHelper.d(TAG,"isLockPatternEnabled is off");
            }

//            Method savedPatternExists = clazz.getMethod("savedPatternExists");
//            if ((Boolean)savedPatternExists.invoke(object)){
//                LogHelper.d(TAG,"savedPatternExists is on");
//            }else {
//                LogHelper.d(TAG,"savedPatternExists is off");
//            }
            return  isPatternEnable;
        }catch (ClassNotFoundException e){
            LogHelper.d(TAG,"ClassNotFoundException:"+e.getMessage());
        }catch (NoSuchMethodException e){
            LogHelper.d(TAG,"NoSuchMethodException:"+e.getMessage());
        }catch (IllegalAccessException e){
            LogHelper.d(TAG,"IllegalAccessException:"+e.getMessage());
        }catch (InstantiationException e){
            LogHelper.d(TAG,"InstantiationException:"+e.getMessage());
        }catch (InvocationTargetException e){
            LogHelper.d(TAG,"InvocationTargetException:"+e.getMessage());
        }
        return false;
    }
}
