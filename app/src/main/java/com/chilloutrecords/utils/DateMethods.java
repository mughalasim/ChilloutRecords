package com.chilloutrecords.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class DateMethods {

    private static DateFormat old_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);

    public static String returnTimeFromDateString(String date_string) {
        try {
            Date old_date = old_format.parse(date_string);
            old_format.setTimeZone(TimeZone.getTimeZone("UTC"));
            DateFormat new_format = new SimpleDateFormat("HH:mm", Locale.UK);
            new_format.setTimeZone(TimeZone.getDefault());
            return new_format.format(Objects.requireNonNull(old_date));

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String returnDateFromDateString(String date_string) {
        try {
            Date old_date = old_format.parse(date_string);
            old_format.setTimeZone(TimeZone.getTimeZone("UTC"));
            DateFormat new_format = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
            new_format.setTimeZone(TimeZone.getDefault());
            return new_format.format(Objects.requireNonNull(old_date));

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Calendar returnCalenderFromDateString(String date_string) {
        Calendar cal = Calendar.getInstance();
        try {
            Date date = old_format.parse(date_string);
            old_format.setTimeZone(TimeZone.getTimeZone("UTC"));
            cal.setTime(Objects.requireNonNull(date));
            cal.setTimeZone(TimeZone.getDefault());
            return cal;
        } catch (ParseException e) {
            e.printStackTrace();
            return cal;
        }
    }

    public static Date addHoursToDate (Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hours);
        return calendar.getTime();
    }


}
