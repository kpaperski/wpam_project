package com.example.wpam;

import java.util.Calendar;
import java.util.Date;

public class TimeFunction {
    public static String addZero(String time, int numTime){
        if (numTime<10)
            time = time + "0" + Integer.toString(numTime);
        else
            time = time + Integer.toString(numTime);
        return time;
    }
    public static String getTime() {
        Date currentTime = Calendar.getInstance().getTime();
        String time = Integer.toString(currentTime.getYear());
        int month = currentTime.getMonth();
        time = addZero(time, month);
        int day = currentTime.getDay();
        time = addZero(time, day);
        int hour = currentTime.getHours();
        time = addZero(time, hour);
        int min = currentTime.getMinutes();
        time = addZero(time, min);
        int sec = currentTime.getSeconds();
        time = addZero(time, sec);
        return time;
    }

}
