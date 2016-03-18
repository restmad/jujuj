package com.shinado.tagme.user;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.shinado.tagme.BaseResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * {"id":"27","account":"s@s.s","pwd":"03c7c0ace395d80182db07ae2c30f034",
 * "unique_id":"Dan","gender":"-1","following":"33","follower":"25","likes":"4",
 * "portrait":"http:\/\/tagme-tagme.stor.sinaapp.com\/imgs\/20141113104904656.png","tags":"4","place":"Changsha","user_name":"Danss"}
 */
public class User extends BaseResult implements Parcelable{

	public static final int MALE = 1;
	public static final int FEMALE = 2;

	private int id;

	private String user_name = "";
	private String account = "";
	
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
		this.account = account;
		loaded = false;
	}

	public void copy(User target){
		if(target == null){
			return;
		}
		setUserName(target.getUserName());
		setAccount(target.getAccount());
		setUniqueId(target.getUniqueId());
		setPlace(target.getPlace());
		setPortrait(target.getPortrait());
		setGender(target.getGender());
		setTags(target.getTags());
		setFollower(target.getFollower());
		setFollowing(target.getFollowing());
		setLikes(target.getLikes());
		setLoaded(target.isLoaded());
		setId(target.getId());
	}
	
	public User(String userName, String userAccount, String uniqueId,
			String place, String portraitUrl, int gender, int tags,
			int following, int follower, int likes) {
		super();
		this.user_name = userName;
		this.account = userAccount;
		this.uniqueId = uniqueId;
		this.place = place;
		this.portrait = portraitUrl;
		this.gender = gender;
		this.tags = tags;
		this.following = following;
		this.follower = follower;
		this.likes = likes;
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return user_name;
	}
	public void setUserName(String userName) {
		this.user_name = userName;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
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
	public String getPortrait() {
		return portrait;
	}
	public void setPortrait(String portrait) {
		this.portrait = portrait;
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
		dest.writeString(user_name);
		dest.writeString(account);
		dest.writeString(uniqueId);
		dest.writeString(place);
		dest.writeString(portrait);
		dest.writeInt(gender);
		dest.writeInt(tags);
		dest.writeInt(following);
		dest.writeInt(follower);
		dest.writeInt(likes);
		dest.writeInt(loaded ? 1:-1);
		dest.writeInt(id);
	}

	private User(Parcel source){
		int b = source.readInt();
		if(b > 0){
			byte[] val = new byte[b];
			source.readByteArray(val);
			InputStream is = new ByteArrayInputStream(val);
			portraitThumb = BitmapFactory.decodeStream(is);   
		}
		user_name = source.readString();
		account = source.readString();
		uniqueId = source.readString();
		place = source.readString();
		portrait = source.readString();
		gender = source.readInt();
		tags = source.readInt();
		following = source.readInt();
		follower = source.readInt();
		likes = source.readInt();
		loaded = source.readInt() > 0;
		id = source.readInt();
	}
	
	public static final Creator<User> CREATOR = new Creator<User>() {
		
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
