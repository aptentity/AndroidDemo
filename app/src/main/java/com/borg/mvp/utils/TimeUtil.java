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
}
