package com.mocaa.tagme.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeSet;

import com.mocaa.tagme.download.ImageLoaderImpl;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.util.BitmapUtil;
import com.mocaa.tagme.util.TimeUtil;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Tag implements Parcelable, Comparable<Tag>{

	public static final int TYPE_TEXT = 1;
	public static final int TYPE_IMAGE = 2;
	public static final int TYPE_LOCATION = 3;
	public static final int TYPE_ROOT = 10;
	public static final int DIR_RIGHT = -1;
	public static final int DIR_LEFT = 1;
	
	public int resId;
	private int pId;
	
	public int getPId() {
		return pId;
	}

	public void setPId(int pId) {
		this.pId = pId;
	}
	private int likes;
	private int comments;

	private int serverId;
	private int id;
	private String userAccount = "";
	private String title = "";
	private String content = "";
	private String place = "";
	private int type;
	private int direction = DIR_RIGHT;
	private int locX;
	private int locY;
	private int width;
	private int height;
	private String time;
	private ArrayList<Tag> tags = new ArrayList<Tag>();
	private String imgUri = "";
	private String imgUrl = "";
	private boolean loaded = true;
//	private Bitmap imgBm = null;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public Tag(int id){
		this();
		this.serverId = id;
		loaded = false;
	}
	
	public Tag(int id, String userAccount, String title, String content, int type,
			int x, int y, int width, int height, String time, int likes, int comments,
			String img_url, int direction, ArrayList<Tag> tags, String place){
		this.serverId = id;
		this.userAccount = userAccount;
		this.title = title;
		this.content = content;
		this.type = type;
		this.locX = x;
		this.locY = y;
		this.width = width;
		this.height = height;
		this.time = time;
		this.likes = likes;
		this.comments = comments;
		this.imgUrl = img_url;
		this.direction = direction;
		this.tags = tags;
		this.place = place;
		for(Tag tag:tags){
			tag.setPId(serverId);
		}
	}

	public Tag(int type, int width){
		this.width = width;
		this.height = width;
		time = new TimeUtil().getTimeString();
		this.type = type;
	}
	
	public Tag(int type, String userAccount){
		this();
		this.type = type;
		this.userAccount = userAccount;
	}
	
	public Tag(){
		width = GlobalDefs.getScreenWidth();
		height = width;
		time = new TimeUtil().getTimeString();
	}
	
	public void copy(Tag target){
		setServerId(target.getServerId());
		setContent(target.getContent());
		setDirection(target.getDirection());
		setId(target.getId());
		setImgUri(target.imgUri);
		setImgUrl(target.imgUrl);
		setLocation(target.getLocation());
		setTags(target.getTags());
		setTitle(target.getTitle());
		setType(target.getType());
		setWidth(target.getWidth());
		setHeight(target.getHeight());
		setUserAccount(target.getUserAccount());
		setTime(target.getTime());
		setPlace(target.getPlace());
		setLikes(target.getLikes());
		setComments(target.getComments());
		setLoaded(target.isLoaded());
		setPId(target.getPId());
	}
	
	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public Point getLocation(){
		return new Point(locX, locY);
	}
	public void setLocation(Point pt){
		this.locX = pt.x;
		this.locY = pt.y;
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
//	private Bitmap imgBm;
//	public void setImg(Bitmap img){
//		this.imgBm = img;
//	}
	public Bitmap getImg(Context context) {
//		if(imgBm == null){
			Bitmap imgBm = null;
			if(imgUrl != null){
				//local
				imgBm = ImageLoaderImpl.getBitmapFromFile(context, imgUrl);
				if(imgBm == null){
					if(imgUri != null){
						//local
						imgBm = ImageLoaderImpl.getBitmapFromFile(context, imgUri);
					}
				}
			}
//		}else{
//			GlobalDefs.o("get image:"+title);
//		}
		return imgBm;
	}
	public ArrayList<Tag> getTags() {
		return tags;
	}
	public void setTags(ArrayList<Tag> tags) {
		this.tags = tags;
		for(Tag tag:tags){
			tag.setPId(serverId);
		}
	}

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public void changeDirection(){
		direction = -direction;
	}
	public void setImgUri(String imgUri) {
		this.imgUri = imgUri;
	}
	public String getImgUrl(){
		return imgUrl;
	}
	public String getImgUri(){
		return imgUri;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	private Tag(Parcel source){
//		int b = source.readInt();
//		if(b > 0){
//			byte[] val = new byte[b];
//			source.readByteArray(val);
//			InputStream is = new ByteArrayInputStream(val);
//			imgBm = BitmapFactory.decodeStream(is);   
//		}
		serverId = source.readInt();
		id = source.readInt();  
		content = source.readString();
		title = source.readString();
		type = source.readInt();
		direction = source.readInt();
		locX = source.readInt();
		locY = source.readInt();
		imgUri = source.readString();
		imgUrl = source.readString();
		source.readTypedList(tags, Tag.CREATOR);
		width = source.readInt();
		height = source.readInt();
		userAccount = source.readString();
		time = source.readString();
		likes = source.readInt();
		place = source.readString();
		comments = source.readInt();
		loaded = source.readInt() > 0;
		pId = source.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
//		if(imgBm != null){
//			ByteArrayOutputStream stream = new ByteArrayOutputStream();
//			imgBm.compress(Bitmap.CompressFormat.PNG, 100, stream);  
//			byte[] bytes = stream.toByteArray();
//			dest.writeInt(bytes.length);
//			dest.writeByteArray(bytes);
//		}else{ 
//			dest.writeInt(0);
//		}
		dest.writeInt(serverId);
		dest.writeInt(id);
		dest.writeString(content);
		dest.writeString(title);
		dest.writeInt(type);
		dest.writeInt(direction);
		dest.writeInt(locX);
		dest.writeInt(locY);
		dest.writeString(imgUri);
		dest.writeString(imgUrl);
		dest.writeTypedList(tags);
		dest.writeInt(width);
		dest.writeInt(height);
		dest.writeString(userAccount);
		dest.writeString(time);
		dest.writeInt(likes);
		dest.writeString(place);
		dest.writeInt(comments);
		dest.writeInt(loaded ? 1:-1);
		dest.writeInt(pId);
	}

	
	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
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
	public static final Parcelable.Creator<Tag> CREATOR = new Creator<Tag>() {  
		
		@Override  
		public Tag createFromParcel(Parcel source) {  
			return new Tag(source);  
		}  
		
		@Override  
		public Tag[] newArray(int size) {  
			return new Tag[size];  
		}  
	};
	
	@Override
	public int compareTo(Tag another) {
		return another.getServerId() - serverId;
//		return another.getTime().compareTo(time);
	}  
	

}
