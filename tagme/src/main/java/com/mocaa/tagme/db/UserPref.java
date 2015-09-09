package com.mocaa.tagme.db;

import com.mocaa.tagme.entity.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserPref {
	
	public static final String ACCOUNT_DEFAULT = "DEFAULT_ACCOUNT";
	private static final String KEY_VERSION_SERVER = "s_version";
	private static final String USER_ACCOUNT = "user_account";
	private static final String SIGN_IN = "sign_in";
	private static final String NAME = "tag.me";
	private static final String FIRST_TIME = "first_time";
	private static final String QZONE_OID = "qzong_oid";
	private static final String QZONE_TOKEN = "qzong_token";
	private static final String QZONE_IN = "qzong_in";
	private static final String QZONE_LOGIN = "qzong_login";
	private static final String NOTIFICATION = "notification";
	private SharedPreferences shares;
	private UserDao userDao;
	
	public UserPref(Context context){
		shares = context.getSharedPreferences(NAME, 0);
		userDao = new UserDao(context);
	}
	
	public void switchNotification(){
		Editor editor = shares.edit();
		editor.putBoolean(NOTIFICATION, !isNotificating());
		editor.commit();
	}

	public boolean isNotificating(){
		return shares.getBoolean(NOTIFICATION, true);
	}

	public static boolean signedIn(Context context){
		return context.getSharedPreferences(NAME, 0).getBoolean(SIGN_IN, false);
	}

	public void signIn(){
		Editor editor = shares.edit();
		editor.putBoolean(SIGN_IN, true);
		editor.commit();
	}

	public void signOut(){
		Editor editor = shares.edit();
		editor.putBoolean(SIGN_IN, false);
		editor.commit();
	}
	
	public static String getUserAccount(Context context){
		return context.getSharedPreferences(NAME, 0).getString(USER_ACCOUNT, ACCOUNT_DEFAULT);
	}
	
	public void setUserAccount(String name){
		String oldName = getUesrAccount();
		if(oldName.equals(name)){
			return ;
		}
		
		Editor editor = shares.edit();
		editor.putString(USER_ACCOUNT, name);
		editor.commit();
	}

	public boolean isFirstTime(){
		boolean b = shares.getBoolean(FIRST_TIME, true);
		return b;
	}
	
	public void firstTime(){
		Editor editor = shares.edit();
		editor.putBoolean(FIRST_TIME, false);
		editor.commit();
	}

	public String getUesrAccount(){
		return shares.getString(USER_ACCOUNT, ACCOUNT_DEFAULT);
	}

	public User getMyProfile(){
		User user = userDao.getUserByAccount(getUesrAccount());
		return user;
	}

	public void setServerVersion(String value){
		Editor editor = shares.edit();
		editor.putString(KEY_VERSION_SERVER, value);
		editor.commit();
	}
	public String getServerVersion(String version){
		String v = shares.getString(KEY_VERSION_SERVER, version);
		return v;
	}

	public void setQzoneOId(String value){
		Editor editor = shares.edit();
		editor.putString(QZONE_OID, value);
		editor.commit();
	}
	public String getQzoneOId(){
		String v = shares.getString(QZONE_OID, "");
		return v;
	}

	public void setQzoneToken(String value){
		Editor editor = shares.edit();
		editor.putString(QZONE_TOKEN, value);
		editor.commit();
	}
	public String getQzoneToken(){
		String v = shares.getString(QZONE_TOKEN, "");
		return v;
	}

	public void setQzoneIN(String value){
		Editor editor = shares.edit();
		editor.putString(QZONE_IN, value);
		editor.commit();
	}
	public String getQzoneIN(){
		String v = shares.getString(QZONE_IN, "");
		return v;
	}

	public void loginQzone(){
		Editor editor = shares.edit();
		editor.putBoolean(QZONE_LOGIN, true);
		editor.commit();
	}
	public boolean isQzoneLogin(){
		return shares.getBoolean(QZONE_LOGIN, false);
	}
}
