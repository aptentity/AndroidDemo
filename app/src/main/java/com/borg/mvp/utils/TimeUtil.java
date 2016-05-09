package com.borg.mvp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Gulliver(feilong) on 16/2/24.
 */
public class TimeUtil {
    public static int daysOfTwo(Date originalDate, Date compareDateDate) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(originalDate);
        int originalDay = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(compareDateDate);
        int compareDay = aCalendar.get(Calendar.DAY_OF_YEAR);

        return originalDay - compareDay;
    }

    public static String FriendlyDate(Date compareDate) {
        Date nowDate = new Date();
        int dayDiff = daysOfTwo(nowDate, compareDate);

        if (dayDiff <= 0)
            return "今日";
        else if (dayDiff == 1)
            return "昨日";
        else if (dayDiff == 2)
            return "前日";
        else
            return new SimpleDateFormat("M月d日 E").format(compareDate);
    }

    /**
     *  判断一个时间段 比如9:00-12:00
     * @param beginHour
     * @param beginMin
     * @param endHour
     * @param endMin
     * @return
     */
    public static boolean isIn(int beginHour,int beginMin,int endHour,int endMin){
        Calendar cal = Calendar.getInstance();// 当前日期
        int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
        int minute = cal.get(Calendar.MINUTE);// 获取分钟
        int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
        final int start = beginHour * 60 + beginMin;// 起始时间 17:20的分钟数
        final int end = endHour * 60 +endMin;// 结束时间 19:00的分钟数
        if (minuteOfDay >= start && minuteOfDay <= end) {
            return true;
        } else {
            return false;
        }
    }
}
