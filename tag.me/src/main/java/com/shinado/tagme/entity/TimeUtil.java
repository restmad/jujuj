package com.shinado.tagme.entity;

import android.content.Context;

import com.shinado.tagme.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TimeUtil {

    private TimeVo time;
    private DateVo date;

	public TimeUtil(String time){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			formatter.parse(time);

			String strDate = time.substring(0, 10);
			String strTime = time.substring(11, time.length());
            this.time = new TimeVo(strTime);
            this.date = new DateVo(strDate);
		}
		catch (ParseException e) {
            System.out.println("time error:"+time);
		}
	}

	public TimeUtil(){
        this.time = new TimeVo();
        this.date = new DateVo();
	}
	
	public TimeUtil(String time, String date){
		this();
		if(time != null){
            this.time = new TimeVo(time);
		}
		if(date != null){
            this.date = new DateVo(date);
		}
	}

    public TimeVo getTime(){
        return time;
    }

    public DateVo getDate(){
        return date;
    }

    public String getTimeString(){
		return date.toString() + " " + time.toString();
	}
    private final String[] TOKEN = new String[]{"year", "year", "year", "year", "year",
            "month", "month", "month",
            "date", "date", "date",
            "hour", "hour", "hour",
            "min", "min", "min"};

    public String getTimeContent(Context context){
        TimeUtil currentTime = new TimeUtil();
        DateVo cDate = currentTime.getDate();
        String ago = context.getResources().getString(R.string.ago);
        if(cDate.compareTo(date) == 0){
            TimeVo cTime = currentTime.getTime();
            if(cTime.getHour() > time.getHour()){
                int h = cTime.getHour() - time.getHour();
                if(ago.equals(" ago")){
                    if(h > 1){
                        return h + " " + context.getResources().getString(R.string.hour) + "s" + ago;
                    }
                }
                return h + " " + context.getResources().getString(R.string.hour) + ago;
            }else{
                if(cTime.getMin() > time.getMin()){
                    int m = cTime.getMin() - time.getMin();
                    if(ago.equals(" ago")){
                        if(m > 1){
                            return m + " " + context.getResources().getString(R.string.min) + "s" + ago;
                        }
                    }
                    return m + " " + context.getResources().getString(R.string.min) + ago;
                }else{
                    return context.getResources().getString(R.string.now);
                }
            }
        }else{
            if(cDate.getYear() > date.getYear()){
                int y = (cDate.getYear() - date.getYear());
                if(ago.equals(" ago") && y > 1){
                    return y + " " + context.getResources().getString(R.string.year) + "s" + ago;
                }
                return y + " " + context.getResources().getString(R.string.year) + ago;
            }else{
                if(cDate.getMonth() > date.getMonth()){
                    int m = (cDate.getMonth() - date.getMonth());
                    if(ago.equals(" ago") && m > 1){
                        return m + " " + context.getResources().getString(R.string.month) + "s" + ago;
                    }
                    return m + " " + context.getResources().getString(R.string.month) + ago;
                }else{
                    int d = (cDate.getDay() - date.getDay());
                    if(ago.equals(" ago") && d > 1){
                        return d + " " + context.getResources().getString(R.string.day) + "s" + ago;
                    }
                    return d + " " + context.getResources().getString(R.string.day) + ago;
                }
            }
        }
        /*2014-10-13 14:23
          2014-10-12 12:00
          0000-00-01 02:23
         */
    }

}