package com.shinado.tagme.entity;


import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 "id": "1911",
 "userAccount": "s@s.s",
 "title": "",
 "content": "",
 "type": "10",
 "imgUrl": "http://tagme-tagme.stor.sinaapp.com/imgs/20141030104457415.png",
 "uploadTime": "2015-03-30 15:00:20",
 "likes": "0",
 "comments": "0",
 "place": ""
 */
public class Tag implements Parcelable{

	private int likes;
	private int comments;

	private int id;
	private String userAccount = "";

	private String userPortrait = "";

	private String userName = "";
	private String title = "";
	private String content = "";
	private String place = "";
	private int type;
	private String uploadTime;
	private String imgUrl = "";

	public String getTime() {
		return uploadTime;
	}

	public void setTime(String time) {
		this.uploadTime = time;
	}

	public Tag(int type, String userAccount){
		this();
		this.type = type;
		this.userAccount = userAccount;
	}
	
	public Tag(){
		uploadTime = new TimeUtil().getTimeString();
	}
	
	public void copy(Tag target){
		setContent(target.getContent());
		setId(target.getId());
		setImgUrl(target.imgUrl);
		setTitle(target.getTitle());
		setType(target.getType());
		setUserAccount(target.getUserAccount());
		setTime(target.getTime());
		setPlace(target.getPlace());
		setLikes(target.getLikes());
		setComments(target.getComments());
		setUserName(target.getUserName());
	}

	public String getUserPortrait() {
		return userPortrait;
	}

	public void setUserPortrait(String userPortrait) {
		this.userPortrait = userPortrait;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getImgUrl(){
		return imgUrl;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	private Tag(Parcel source){
		id = source.readInt();
		content = source.readString();
		title = source.readString();
		type = source.readInt();
		imgUrl = source.readString();
		userAccount = source.readString();
		uploadTime = source.readString();
		likes = source.readInt();
		place = source.readString();
		comments = source.readInt();
		userName = source.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(content);
		dest.writeString(title);
		dest.writeInt(type);
		dest.writeString(imgUrl);
		dest.writeString(userAccount);
		dest.writeString(uploadTime);
		dest.writeInt(likes);
		dest.writeString(place);
		dest.writeInt(comments);
		dest.writeString(userName);
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

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}
	public static final Creator<Tag> CREATOR = new Creator<Tag>() {
		
		@Override  
		public Tag createFromParcel(Parcel source) {  
			return new Tag(source);  
		}  
		
		@Override  
		public Tag[] newArray(int size) {  
			return new Tag[size];  
		}  
	};


}
