package com.wofi.utils;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class TimeUtils {
    public static String getFormatDate(String t)
    {
        long time= Long.parseLong(t);
        Date date=new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }
}
