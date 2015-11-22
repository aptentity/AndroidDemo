package com.borg.androidemo.devices.protocol;

import java.util.ArrayList;

/**
 * 协议的category集合
 * 
 * @author jinyi
 * 
 */
public class ServiceCategory {

    public static final ArrayList<String> categorys = new ArrayList<>();

	public static final int CATEGORY_NOTIFICATION = 0;
	public static final int CATEGORY_PHONECALL = 1;
	public static final int CATEGORY_SYSTEMTIME = 2;
	public static final int CATEGORY_STEPS = 6;
	public static final int CATEGORY_HEARTRATE = 11;
	public static final int CATEGORY_PAY = 5;
	public static final int CATEGORY_CONTROL = 6;
	public static final int CATEGORY_SYS_WATCHER = 8;
	public static final int CATEGORY_WEATHER = 9;
	public static final int CATEGORY_SCHEDULE = 10;
	public static final int CATEGORY_BIND_AUTH = 15;
	public static final int CATEGORY_ALIPAY_STATE = 17;
	public static final int CATEGORY_WATER = 18;
	public static final int CATEGORY_CLOUD_CARD = 19;
	public static final int CATEGORY_CUUID = 8; // debug

    static final public int CATEGORY_NETBINDER = 500;
    static final public int CATEGORY_IDCSERVERPUSH = 600;
    static{
        categorys.add(String.valueOf(CATEGORY_NOTIFICATION));
        categorys.add(String.valueOf(CATEGORY_PHONECALL));
        categorys.add(String.valueOf(CATEGORY_SYSTEMTIME));
        categorys.add(String.valueOf(CATEGORY_STEPS));
        categorys.add(String.valueOf(CATEGORY_HEARTRATE));
        categorys.add(String.valueOf(CATEGORY_PAY));
        categorys.add(String.valueOf(CATEGORY_CONTROL));
        categorys.add(String.valueOf(CATEGORY_SYS_WATCHER));
        categorys.add(String.valueOf(CATEGORY_SCHEDULE));
        categorys.add(String.valueOf(CATEGORY_BIND_AUTH));
        categorys.add(String.valueOf(CATEGORY_ALIPAY_STATE));
        categorys.add(String.valueOf(CATEGORY_WATER));
        categorys.add(String.valueOf(CATEGORY_CLOUD_CARD));
        categorys.add(String.valueOf(CATEGORY_CUUID));
    }
}
