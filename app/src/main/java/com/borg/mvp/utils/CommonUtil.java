package com.borg.mvp.utils;

import java.util.regex.Pattern;

/**
 * Created by Gulliver(feilong) on 15/12/14.
 */
public class CommonUtil {
    public static boolean isMobileNumber(String mobiles) {
        return Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[^1^4,\\D]))\\d{8}").matcher(mobiles).matches();
    }
}
