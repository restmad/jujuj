package com.mocaa.tagme.entity;

import java.io.Serializable;
import java.util.Calendar;

/**
 * It's important: The hour is Duodecimal Number System.
 * That is, 13:00 = 01:00 PM 
 * @author Administrator
 *
 */
public class TimeVo implements Serializable{

	private static final long serialVersionUID = 1L;
	private int hour = 0;
	private int min = 0;
	
	public TimeVo(int hour, int min) {
		this.hour = hour;
		this.min = min;
	}
	public TimeVo(String time){
		hour = Integer.valueOf(time.substring(0, 2));
		min = Integer.valueOf(time.substring(3, 5));
	}
	public TimeVo() {
		Calendar cal = Calendar.getInstance();
		setHour(cal.get(Calendar.HOUR_OF_DAY));
		setMin(cal.get(Calendar.MINUTE));
	}
	public int getHour() {
		return hour;
	}
	public String getHourString(){
		if(hour < 10){
			return "0"+hour;
		}
		return hour+"";
	}
	public String getMinString(){
		if(min < 10){
			return "0"+min;
		}
		return min+"";
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public void setTime(String time){
		hour = Integer.valueOf(time.substring(0, 2));
		if(hour > 12){
			hour -= 12;
		}
		min = Integer.valueOf(time.substring(3, 5));
	}
	
	public boolean isLater(TimeVo time){
		int h = getHour() - time.getHour();
		if(h > 0){
			return true;
		}else if(h < 0){
			return false;
		}
		int m = getMin() - time.getMin();
		if(m > 0){
			return true;
		}else{
			return false;
		}
	}
	public String toString(){
		String time="";
		if(hour < 10){
			time += "0"+hour;
		}else{
			time += hour;
		}
		time += ":";
		if(min < 10){
			time += "0"+min;
		}else{
			time += min;
		}
		return time;
	}
}
