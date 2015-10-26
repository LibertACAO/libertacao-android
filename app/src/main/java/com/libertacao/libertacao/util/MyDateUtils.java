package com.libertacao.libertacao.util;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyDateUtils {
    public static Date getYesterdayDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    // PretyTime
    private static PrettyTime prettyTime;

    public static PrettyTime getPrettyTime() {
        if(prettyTime == null) {
            prettyTime = new PrettyTime();
        }
        return prettyTime;
    }

    // DateFormat
    // Event
    private static DateFormat dateMonthHourMinuteDateFormat;

    private static DateFormat getDateMonthHourMinuteDateFormat() {
        if (dateMonthHourMinuteDateFormat == null) {
            dateMonthHourMinuteDateFormat = new SimpleDateFormat("dd/MM' - 'HH:mm", new Locale("pt", "BR"));
        }
        return dateMonthHourMinuteDateFormat;
    }

    public static String getDateMonthHourMinuteDateToUser(Date date) {
        if(date != null) {
            return getDateMonthHourMinuteDateFormat().format(date);
        } else {
            return "";
        }
    }

    // Minimal event
    private static DateFormat hourMinuteDateFormat;

    private static DateFormat getHourMinuteDateFormat() {
        if (hourMinuteDateFormat == null) {
            hourMinuteDateFormat = new SimpleDateFormat("HH:mm", new Locale("pt", "BR"));
        }
        return hourMinuteDateFormat;
    }

    public static String getHourMinuteDateToUser(Date date) {
        if(date != null) {
            return getHourMinuteDateFormat().format(date);
        } else {
            return "";
        }
    }

    // Day/month
    private static DateFormat dayMonthDateFormat;

    private static DateFormat getDayMonthDateFormat() {
        if (dayMonthDateFormat == null) {
            dayMonthDateFormat = new SimpleDateFormat("dd/MM", new Locale("pt", "BR"));
        }
        return dayMonthDateFormat;
    }

    public static String getDayMonthDateToUser(Date date) {
        if(date != null) {
            return getDayMonthDateFormat().format(date);
        } else {
            return "";
        }
    }
}
