package com.shinado.tagme.entity;

import java.io.Serializable;
import java.util.Calendar;

public class DateVo implements Serializable, Comparable<DateVo>{

    @Override
    public int compareTo(DateVo dateVo) {
        return toString().compareTo(dateVo.toString());
    }

    public int getYear() {
		return year;
	}
	public String getYearString() {
		return year+"";
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}
	public String getMonthString() {
		if(month < 10){
			return "0"+month;
		}else{
			return month+"";
		}
	}
	
	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}
	public String getDayString() {
		if(day < 10){
			return "0"+day;
		}else{
			return day+"";
		}
	}
	

	public void setDay(int day) {
		this.day = day;
	}

	private int year;
	private int month;
	private int day;
	
	public DateVo(int year, int month, int day){
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	public DateVo(String str){
		String date = str.split(" ")[0];
		String dateValue[] = date.split("-");
		year = Integer.parseInt(dateValue[0]);
		month = Integer.parseInt(dateValue[1]);
		day = Integer.parseInt(dateValue[2]);
	}
	
	public String toString(){
		String ret = year+"-";
		if(month < 10){
			ret += "0";
		}
		ret += month+"-";
		if(day < 10){
			ret += "0";
		}
		ret += day;
		return ret;
	}

	public DateVo(Calendar date) {
		this.year = date.get(Calendar.YEAR);
		this.month = date.get(Calendar.MONTH)+1;
		this.day = date.get(Calendar.DAY_OF_MONTH);
	}

    public DateVo() {
        this(Calendar.getInstance());
    }

}
