package com.vivavu.dream.util;

import android.util.Log;

import com.vivavu.dream.model.bucket.option.OptionDDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by yuja on 14. 1. 9.
 */
public class DateUtils {
    public static Long getRemainDay(Date endDate){
        Calendar nowCal = Calendar.getInstance();
        nowCal.set(Calendar.HOUR_OF_DAY, 0);
        nowCal.set(Calendar.MINUTE, 0);
        nowCal.set(Calendar.SECOND, 0);
        nowCal.set(Calendar.MILLISECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);

        Long now = nowCal.getTimeInMillis();
        Long end = endCal.getTimeInMillis();
        Long remain = (end-now)/1000/60/60/24;

        return remain;
    }

    public static Long getRemainDay(Date startDate, Date endDate){
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);

        Long now = startCal.getTimeInMillis();
        Long end = endCal.getTimeInMillis();
        Long remain = (end-now)/1000/60/60/24;

        return remain;
    }

    public static String getRemainDayInString(Date deadline){
        if(deadline != null){
            Long remain = DateUtils.getRemainDay(deadline);
            if(remain > 0 ){
                return String.format("remain %,d Days", remain);
            }else if(remain < 0){
                return String.format("over %,d Days", Math.abs(remain));
            }else{
                return "Today!!!";
            }

        }else{
            return "in my life";
        }
    }

    public static int getProgress(Date startDate, Date endDate){
        Long totalTime = getRemainDay(startDate, endDate);
        Long remainTime = getRemainDay(endDate);
        int percentage = 100;
        if(totalTime > 0){
            int l = (int) ((1 - (remainTime / (double)totalTime)) * 100);
            percentage = Math.max(l, 0);
        }
        return percentage;
    }

    public static List<OptionDDay> getUserDdays(Date birthday){
        Calendar cal = Calendar.getInstance();
        List<OptionDDay> ddays = new ArrayList<OptionDDay>();

        if(birthday != null){
            int ageInFull = getAgeInFull(birthday);
            cal.setTime(birthday);
            int period = ageInFull - (ageInFull%10);
            cal.add(Calendar.YEAR, period);
            cal.add(Calendar.DATE, -1);
            for(int i = 0; i < 6; i++){
                cal.add(Calendar.YEAR, 10 );
                ddays.add(new OptionDDay( (period + i*10) +"대" , cal.getTime()));
            }
        } else {
            cal.add(Calendar.DATE, -1);
            for(int i = 0; i < 6; i++){
                cal.add(Calendar.YEAR, 10 );
                ddays.add(new OptionDDay( (i+1)*10 +"년 후" , cal.getTime()));
            }
        }

        return ddays;
    }

    public static int getAgeInFull(Date birthday){

        Calendar cal = Calendar.getInstance();
        int nowYear = cal.get(Calendar.YEAR);
        int nowMmdd = (cal.get(Calendar.MONTH)+1) * 100 + cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthday);

        int birthYear = cal.get(Calendar.YEAR);
        int birthMmdd = (cal.get(Calendar.MONTH)+1) * 100 + cal.get(Calendar.DAY_OF_MONTH);

        int ageInFull = nowYear - birthYear;
        if(nowMmdd > birthMmdd){
            ageInFull--;
        }
        return ageInFull;
    }

    public static String getDefaultStyleDate(Date date){
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static String getDateString(Date date, String pattern) {
        return getDateString(date, pattern, "");
    }

    public static String getDateString(Date date, String pattern, String defaultValue) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        String result = defaultValue;
        try{
            result = dateFormat.format(date);
        } catch (NullPointerException e){
            Log.e("dream", e.toString());
        }
        return result;
    }

    public static Date getDateFromString(String dateStr, String pattern, Date defaultValue){
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date result = defaultValue;
        try{
            result = dateFormat.parse(dateStr);
        } catch (NullPointerException e){
            Log.e("dream", e.toString());
        } catch (ParseException e) {
            Log.e("dream", e.toString());
        }
        return result;
    }

    public static Date getDate(Long date){
        Date result = new Date();
        result.setTime(date);
        return result;
    }

    public static Date getDate(int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, dayOfMonth, 0, 0);
        return cal.getTime();
    }

    public static Date add(Date date, int filed, int value){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(filed, value);
        return cal.getTime();
    }
}
