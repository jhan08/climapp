package com.example.climapp.utils;

public class FormatMinuteToHour {
    public static String getHourFormatFromAbsoluteMinutes(float value) {
        int hour = (int) value / 60;
        int minute = (int) value % 60;

        String hourStr = (hour > 9 ? String.valueOf(hour) : ("0" + hour));
        String minuteStr = (minute > 9 ? String.valueOf(minute) : ("0" + minute));
        return  hourStr + ":" + minuteStr;
    }
}
