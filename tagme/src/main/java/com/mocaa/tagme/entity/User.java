package com.mocaa.tagme.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.mocaa.tagme.download.ImageLoaderImpl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{

	public static final int MALE = 1;
	public static final int FEMALE = 2;
	
	private String userName = "";
	private String userAccount = "";
	
	private String uniqueId = "";
	private String place = "";
	private String portrait = "";
	private int gender;
	
	private int tags = 0;
	private int following = 0;
	private int follower = 0;
	private int likes = 0;
	private boolean loaded = true;
	
	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	private Bitmap portraitThumb = null;
	
	public User(){
		
	}

	public User(String account){
		this.userAccount = account;
		loaded = false;
	}

	public void copy(User target){
		if(target == null){
			return;
		}
		setUserName(target.getUserName());
		setUserAccount(target.getUserAccount());
		setUniqueId(target.getUniqueId());
		setPlace(target.getPlace());
		setPortraitUrl(target.getPortraitUrl());
		setGender(target.getGender());
		setTags(target.getTags());
		setFollower(target.getFollower());
		setFollowing(target.getFollowing());
		setLikes(target.getLikes());
		setLoaded(target.isLoaded());
	}

	/**
	 *
	 String uid = js.getString("unique_id");
	 int gender = js.getInt("gender");
	 int following = js.getInt("following");
	 int follower = js.getInt("follower");
	 int likes = js.getInt("likes");
	 int tags = js.getInt("tags");
	 String portrait = js.getString("portrait");
	 String user_name = js.getString("user_name");
	 String place = js.getString("place");

	 */
	public User(String userName, String userAccount, String uniqueId,
			String place, String portraitUrl, int gender, int tags,
			int following, int follower, int likes) {
		super();
		this.userName = userName;
		this.userAccount = userAccount;
		this.uniqueId = uniqueId;
		this.place = place;
		this.portrait = portraitUrl;
		this.gender = gender;
		this.tags = tags;
		this.following = following;
		this.follower = follower;
		this.likes = likes;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public int getTags() {
		return tags;
	}
	public void setTags(int tags) {
		this.tags = tags;
	}
	public int getFollowing() {
		return following;
	}
	public void setFollowing(int following) {
		this.following = following;
	}
	public int getFollower() {
		return follower;
	}
	public void setFollower(int follower) {
		this.follower = follower;
	}
	public int getLikes() {
		return likes;
	}
	public void setLikes(int likes) {
		this.likes = likes;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getPortraitUrl() {
		return portrait;
	}
	public void setPortraitUrl(String portraitUrl) {
		this.portrait = portraitUrl;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}

	public Bitmap getPortraitThumb() {
		return portraitThumb;
	}

	public Bitmap getPortrait(Context context) {
		return ImageLoaderImpl.getBitmapFromFile(context, portrait);
	}
	
	public void setPortraitThumb(Bitmap bm){
		portraitThumb = bm;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if(portraitThumb != null){
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			portraitThumb.compress(Bitmap.CompressFormat.PNG, 100, stream);  
			byte[] bytes = stream.toByteArray();
			dest.writeInt(bytes.length);
			dest.writeByteArray(bytes);
		}else{ 
			dest.writeInt(0);
		}
		dest.writeString(userName);
		dest.writeString(userAccount);  
		dest.writeString(uniqueId);
		dest.writeString(place);
		dest.writeString(portrait);
		dest.writeInt(gender);
		dest.writeInt(tags);
		dest.writeInt(following);
		dest.writeInt(follower);
		dest.writeInt(likes);
		dest.writeInt(loaded ? 1:-1);
	}

	private User(Parcel source){
		int b = source.readInt();
		if(b > 0){
			byte[] val = new byte[b];
			source.readByteArray(val);
			InputStream is = new ByteArrayInputStream(val);
			portraitThumb = BitmapFactory.decodeStream(is);   
		}
		userName = source.readString();
		userAccount = source.readString();  
		uniqueId = source.readString();
		place = source.readString();
		portrait = source.readString();
		gender = source.readInt();
		tags = source.readInt();
		following = source.readInt();
		follower = source.readInt();
		likes = source.readInt();
		loaded = source.readInt() > 0;
	}
	
	public static final Parcelable.Creator<User> CREATOR = new Creator<User>() {  
		
		@Override  
		public User createFromParcel(Parcel source) {  
			return new User(source);  
		}  
		
		@Override  
		public User[] newArray(int size) {  
			return new User[size];  
		}  
	};  
}
