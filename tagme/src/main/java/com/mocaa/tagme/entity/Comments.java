package com.mocaa.tagme.entity;

import android.content.Context;
import com.mocaa.tagme.util.TimeUtil;

public class Comments implements Comparable<Comments>{

	public int getTagId() {
		return tagId;
	}
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime(Context context) {
        return time.getTimeContent(context);
	}
    public TimeUtil getTime(){
        return time;
    }
	public void setTime(TimeUtil time) {
		this.time = time;
	}
	public String getReplyAccount() {
		return replyAccount;
	}
	public void setReplyAccount(String replyAccount) {
		this.replyAccount = replyAccount;
	}
	
	
	private int tagId;
	private String userAccount;
	private String content;
	private TimeUtil time;
	private String replyAccount;
	private String tagUserAccount;
	
	public Comments(int tagId, String tagUserAccount, String userAccount, String content, 
			TimeUtil time, String replyAccount){
		this.tagId = tagId;
		this.tagUserAccount = tagUserAccount;
		this.userAccount = userAccount;
		this.content = content;
		this.time = time;
		this.replyAccount = replyAccount;
	}
	
	public Comments(int tagId, String tagUserAccount, String userAccount, String content, 
			String replyAccount){
		this.tagId = tagId;
		this.tagUserAccount = tagUserAccount;
		this.userAccount = userAccount;
		this.content = content;
		this.replyAccount = replyAccount;
		time = new TimeUtil();
	}
	
	@Override
	public int compareTo(Comments another) {
		return another.getTime().getTimeString().compareTo(time.getTimeString());
	}
	
	public String getTagUserAccount() {
		return tagUserAccount;
	}
}
