package com.mocaa.tagme.entity;

import java.io.Serializable;

import com.mocaa.tagme.R;
import com.mocaa.tagme.util.TimeUtil;

import android.content.Context;

public class Notification implements Comparable<Notification>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Notification(int id, String userAccount, String userName, String time,
			int tagId, int type) {
		this.id = id;
		this.userAccount = userAccount;
		this.userName = userName;
		this.time = time;
		this.tagId = tagId;
		this.type = type;
	}
	
	private int id;
	private String userAccount;
	private String userName;
	private String time;

	public String getTime(){
		return time.toString();
	}

	public String getTime(Context context) {
		return new TimeUtil(time).getTimeContent(context);
	}

	public void setTime(String time) {
		this.time = time;
	}
	private int tagId; 
	private int type;
	
//	public Notification(){
//		this.type = TYPE_FOLLOW;
//	}
	
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public int getTagId() {
		return tagId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	public static final int TYPE_FOLLOW = 1;
	public static final int TYPE_LIKE = 2;
	public static final int TYPE_COMMENT = 3;

	public String getContent(Context context){
		String content = "";
		switch(type){
		case TYPE_FOLLOW:
			content = userName+" "+context.getResources().getString(R.string.noti_follow);
			break;
		case TYPE_LIKE:
			content = userName+" "+context.getResources().getString(R.string.noti_like);
			break;
		case TYPE_COMMENT:
		default:
			content = userName+" "+context.getResources().getString(R.string.noti_comment);
			break;
		}
		return content;
	}

	@Override
	public int compareTo(Notification another) {
		return another.getTime().compareTo(time.toString());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
