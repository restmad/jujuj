package com.shinado.tagme.common;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.activeandroid.query.Select;
import com.shinado.tagme.user.User;

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

	public UserPref(Context context){
		shares = context.getSharedPreferences(NAME, 0);
	}
	
	public void switchNotification(){
		Editor editor = shares.edit();
		editor.putBoolean(NOTIFICATION, !isNotificating());
		editor.apply();
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
		editor.apply();
	}

	public void signOut(){
		Editor editor = shares.edit();
		editor.putBoolean(SIGN_IN, false);
		editor.apply();
	}
	
	public static String getUserAccount(Context context){
		return context.getSharedPreferences(NAME, 0).getString(USER_ACCOUNT, ACCOUNT_DEFAULT);
	}
	
	public void setUserAccount(String account){
		String oldName = getUesrAccount();
		if(oldName.equals(account)){
			return ;
		}
		
		Editor editor = shares.edit();
		editor.putString(USER_ACCOUNT, account);
		editor.apply();
	}

	public boolean isFirstTime(){
		return shares.getBoolean(FIRST_TIME, true);
	}
	
	public void firstTime(){
		Editor editor = shares.edit();
		editor.putBoolean(FIRST_TIME, false);
		editor.apply();
	}

	public String getUesrAccount(){
		return shares.getString(USER_ACCOUNT, ACCOUNT_DEFAULT);
	}

	public User getMyProfile(){
		return new Select().from(User.class).where("account = ", getUesrAccount()).executeSingle();
	}

	public void setServerVersion(String value){
		Editor editor = shares.edit();
		editor.putString(KEY_VERSION_SERVER, value);
		editor.apply();
	}
	public String getServerVersion(String version){
		return shares.getString(KEY_VERSION_SERVER, version);
	}

}
