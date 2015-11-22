package com.borg.androidemo.devices.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;

import com.borg.androidemo.common.utils.CKLOG;
import com.borg.androidemo.devices.init.CloudKitProfile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StringUtils {

    private static final String TAG = StringUtils.class.getSimpleName();

    /**
     * 检测string数组中有没有一个为null或空串
     *
     * @param params string数组
     * @return
     */
    public static boolean isOneParamEmpty(String caller, String... params) {
        for (int i = 0; i < params.length; i++) {
            CKLOG.Debug(TAG, caller + " calls method isOneParamsEmpty : params[" + i + "]=" + params[i]);
            if ((null == params[i] || params[i].isEmpty())) {
                return true;
            }
        }
        if (TextUtils.isEmpty(caller)) {
            CKLOG.Error("caller is empty...");
            return true;
        } else {
            CKLOG.Debug(LogConst.TAG_CLOUDKIT, "caller is:" + caller);
        }
        return false;
    }

    /**
     * 检测string数组中是否所有成员都为null或空串
     *
     * @param params string数组
     * @return
     */
    public static boolean isParamsAllEmpty(String... params) {
        for (String p : params) {
            if (!TextUtils.isEmpty(p)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检测字符串是否符合数字规范
     *
     * @param num
     * @return
     */
    public static boolean isNumber(String num) {
        Boolean strResult = num.matches("-?[0-9]+.*[0-9]*");
        return strResult;
    }

    /**
     * 根据资源的id拿到对应的资源字符串
     *
     * @param id
     * @return
     */
    public static String getResourceString(int id) {
        return CloudKitProfile.instance().getContext().getResources().getString(id);
    }

    public static boolean isStringWearMacAddr(String mac) {
        return mac.matches("^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$");
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean isEmpty(Editable s) {
        return s == null || isEmpty(s.toString());
    }

    public static String join(long... args) {
        StringBuilder builder = new StringBuilder();
        for (long i : args) {
            builder.append(String.valueOf(i)).append('_');
        }
        int len = builder.length();
        if (len > 0) {
            builder.setLength(len - 1);
        }
        return builder.toString();
    }

    public static long[] split(String values) {
        if (values == null) {
            return null;
        }
        String[] vals = values.split("_");
        int len = vals.length;
        long[] rets = new long[len];
        for (int i = 0; i < len; ++i) {
            rets[i] = Long.valueOf(vals[i]);
        }
        return rets;
    }

    public static Set<String> splitToSet(String values, String sep) {
        if (values != null) {
            Set<String> set = new HashSet<String>();
            if (sep == null) {
                sep = ",";
            }
            String[] vals = values.split(sep);
            for (String str : vals) {
                set.add(str);
            }
            return set;
        }
        return null;
    }

    public static String join(Set<String> set, String sep) {
        if (set == null) {
            return null;
        }
        if (sep == null) {
            sep = ",";
        }
        int size = set.size();
        if (size == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String key : set) {
            builder.append(key).append(sep);
        }
        builder.setLength(builder.length() - sep.length());
        return builder.toString();
    }

    public static String join(List<String> list, String sep) {
        if (list == null) {
            return null;
        }
        if (sep == null) {
            sep = ",";
        }
        int len = list.size();
        if (len == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(list.get(0));
        for (int i = 1; i < len; ++i) {
            builder.append(sep);
            builder.append(list.get(i));
        }
        return builder.toString();
    }

    public static String join(String[] array, String sep) {
        if (array == null) {
            return null;
        }
        int len = array.length;
        if (len == 0) {
            return "";
        }
        if (sep == null) {
            sep = ",";
        }
        StringBuilder build = new StringBuilder();
        build.append(array[0]);
        for (int i = 1; i < len; ++i) {
            build.append(sep);
            build.append(array[i]);
        }
        return build.toString();
    }

    public static String[] split(String value, String sep) {
        if (value == null) {
            return null;
        }
        if (sep == null) {
            sep = ",";
        }
        return value.split(sep);
    }

    public static String join(String key, String sep, int count) {
        if (key == null || count <= 0) {
            return null;
        }
        if (sep == null) {
            sep = ",";
        }
        StringBuilder builder = new StringBuilder();
        int i = 0;
        builder.append(key);
        while (++i < count) {
            builder.append(sep);
            builder.append(key);
        }
        return builder.toString();
    }

    public static int compareTwo(String a, String b) {
        if (a == null || b == null) {
            return -1;
        }
        String at = a.trim();
        String bt = b.trim();
        return at.compareTo(bt);
    }

    public static String urlencode(String url, String charset) {
        if (charset == null) {
            charset = "UTF-8";
        }
        try {
            String str = URLEncoder.encode(url, charset);
            return str;
        } catch (UnsupportedEncodingException e) {
        }
        return url;
    }

    public static String removeImportantInfo(String info) {
        String ret = null;
        if (info != null) {
            int end = info.length();
            int mid = 0;
            int index = info.indexOf("@");
            if (index > 0) {
                mid = index - 3;
            } else {
                mid = end - 3;
            }
            if (mid < 0) {
                end = mid = 0;
            }
            ret = info.substring(0, mid) + "***";
            if (mid < end) {
                ret = ret + info.substring(mid + 3, end);
            }
        }
        return ret;
    }

    public static void travelMap(Map map) {
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            CKLOG.Debug(TAG, "key=" + key + ",val=" + val);
        }
    }
}
