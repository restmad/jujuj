package com.mocaa.tagme.global;

import android.os.Environment;
import android.util.Log;

public class GlobalDefs {  

	public static int IMAGE_WIDTH = 400;
	private static final String TAG = "Tag.me";
	public static void o(String s){
		Log.v(TAG, s);
	}
	
	private static int width;
	private static int height;
	private static float density;
	
	public static void setScreen(int w, int h, float d){
		width = w;
		height = h;
		density = d;
	}

	public static int getScreenWidth(){
		return width;
	}
	public static int getScreenHeight(){
		return height;
	}
	public static float getScreenDensity(){
		return density;
	}
	
	public final static String FILE_PATH = Environment.getExternalStorageDirectory().getPath() 
			+ "/" + "tagme";
	public final static String DOWNLOAD_PATH = FILE_PATH + "/" + "tag.me.apk";

	public final static String EXTRA_NOTI = "noti";
	public final static String EXTRA_TAG = "tag";
	public final static String EXTRA_TYPE = "type";
	public final static String EXTRA_USER = "user";
	public final static String EXTRA_USER_ACCOUNT = "user_account";
	public final static String EXTRA_PARENT_ID = "parent";
//	public final static String EXTRA_LIKES = "likes";
	public final static String EXTRA_COMMENTING = "commenting";
	public final static String EXTRA_FOLLOW = "follow";
	public final static String EXTRA_NOTIFICATIONS = "notifications";
	public final static String ACTION_NOTIFICATIONS = "com.mocaa.tagme.notifications";

//	public final static String EXTRA_MODIFY = "modify";
//	public final static int MODIFY_NEW = 1;
//	public final static int MODIFY_EDIT = 2;
//	public final static int MODIFY_DELETE = 3;
}
