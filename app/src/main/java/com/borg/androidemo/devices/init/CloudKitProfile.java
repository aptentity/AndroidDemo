package com.borg.androidemo.devices.init;

import android.content.Context;
import android.os.Handler;

import com.borg.androidemo.devices.utils.SharedPreferencesUtil;


/**
 * Created by junxu on 15/9/11.
 */

/**
    用于保存SDK中需要公共使用的数据
 */

public class CloudKitProfile {
    private static CloudKitProfile profile = null;
    private String deviceToken;

    private CloudKitProfile(){

    }

    public static CloudKitProfile instance(){
        if( profile == null ) {
            synchronized(CloudKitProfile.class){
                if( profile == null )
                    profile = new CloudKitProfile();
            }
        }
        return profile;
    }

    private String yunos_appkey_;
    private String yunos_appSecret_;
    private String baicun_appkey_;
    private boolean showdebug_ = false;
    private Context context_ = null;
    //private DeviceLog device_log_ = null;
    //private UTMini utmini_ = null;
    private Handler cmns_handler_ = null;
    //use test evn for rpc
    private static final int ALIBABASDK_ONLINE_ENV = 1;
    private static final int ALIBABASDK_TEST_ENV = 0;
    private static final int ALIBABASDK_PRE_ENV = 2;//预发
    //
    private static final String[] VERSION = { "1.0.0.daily", "1.0.0" ,"1.0.0"};
    private static final String[] CARD_TARGET = { "yunos-cloudcard-usercard-getcardlist-v3", "yunos-cloudcard-usercard-getcardlist" };

    public static final String MOVIE_CARD = "CXA8B16D6GRJFQ3H2IEK";
    public static final String LOGISTICS_CARD = "CWQB09HSM260NMO1LKXV";
    public static final int REQUEST_SCAN_CODE = 202;
    public static final int REQUEST_SCAN_CODE_FIRST_BIND = 203;
    public static final String SCANRESULT = "SCANRESULT";

    public static final int ENV_INDEX = ALIBABASDK_PRE_ENV;

    public void initCloudKitProfile(final String yunoskey,
                                    final String taekey,
                                    final String yunosAppSecret,
                                    boolean turnondebug,
                                    final Context context){
        yunos_appkey_ = yunoskey;
        baicun_appkey_ = taekey;
        yunos_appSecret_ = yunosAppSecret;
        context_ = context;
        showdebug_ = turnondebug;

        //device_log_ = DeviceLog.getInstance();

        //init WDM,utmini,初始化失败了好像不知道？？？
        //utmini_ = new UTMini();
        //utmini_.initUTmini(context_);

    }

    public String getYunOSAppKey(){
        return yunos_appkey_;
    }

    public String getBaichuanAppKey(){
        return baicun_appkey_;
    }

    public boolean isDebugMode() {
        return showdebug_;
    }

    public Context getContext(){
        return context_.getApplicationContext();
    }

    public static String getVersion() {
        return VERSION[ENV_INDEX];
    }

    public static String getCardVersion() {
        return CARD_TARGET[ENV_INDEX];
    }

    public static String getKp()
    {
        return SharedPreferencesUtil.getActiveKp();
    }

    public static synchronized void setKp (final String kp)
    {
        SharedPreferencesUtil.addAccount(kp);
        SharedPreferencesUtil.setActiveKp(kp);
    }

    public static String getDeviceId(){
        return "";
    }
}


