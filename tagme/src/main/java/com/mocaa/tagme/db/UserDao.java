package com.mocaa.tagme.db;

import java.util.ArrayList;

import com.mocaa.tagme.entity.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDao {

	private DBUtil dbUtil;
	private SQLiteDatabase db;

	public UserDao(Context context) {
		dbUtil = new DBUtil(context);
	}
	
	private boolean isUserExist(User user){
		db = dbUtil.getReadableDatabase();

		String whereClause = DBValue.Table_User.COL_USER_ACCOUNT + " = ?";
		String[] whereArgs = {user.getUserAccount()};
		
		Cursor c = db.query(DBValue.Table_User.TABLE_NAME, DBValue.Table_User.TABLE_COLS, 
				whereClause, whereArgs, null, null, null);

		if (c.moveToNext()) {
			return true;
		}
		
		return false;
	}
	
	public void createUser(User user){
		db = dbUtil.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_User.COL_FOLLOWERS, user.getFollower());
		cv.put(DBValue.Table_User.COL_FOLLOWING, user.getFollowing());
		cv.put(DBValue.Table_User.COL_GENDER, user.getGender());
		cv.put(DBValue.Table_User.COL_LIKES, user.getLikes());
		cv.put(DBValue.Table_User.COL_PLACE, user.getPlace());
		cv.put(DBValue.Table_User.COL_PORTRAIT_URL, user.getPortraitUrl());
		cv.put(DBValue.Table_User.COL_TAGS, user.getTags());
		cv.put(DBValue.Table_User.COL_UNIQUE_ID, user.getUniqueId());
		cv.put(DBValue.Table_User.COL_USER_ACCOUNT, user.getUserAccount());
		cv.put(DBValue.Table_User.COL_USER_NAME, user.getUserName());
		
		db.insert(DBValue.Table_User.TABLE_NAME, null, cv);
	}

	public void signInUser(User user){
		if(isUserExist(user)){
			updateUser(user);
		}else{
			createUser(user);
		}
	}
	
	public void updateUser(User user){
		db = dbUtil.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_User.COL_FOLLOWERS, user.getFollower());
		cv.put(DBValue.Table_User.COL_FOLLOWING, user.getFollowing());
		cv.put(DBValue.Table_User.COL_GENDER, user.getGender());
		cv.put(DBValue.Table_User.COL_LIKES, user.getLikes());
		cv.put(DBValue.Table_User.COL_PLACE, user.getPlace());
		cv.put(DBValue.Table_User.COL_PORTRAIT_URL, user.getPortraitUrl());
		cv.put(DBValue.Table_User.COL_TAGS, user.getTags());
		cv.put(DBValue.Table_User.COL_UNIQUE_ID, user.getUniqueId());
		cv.put(DBValue.Table_User.COL_USER_NAME, user.getUserName());

		String whereClause = DBValue.Table_User.COL_USER_ACCOUNT + " = ?";
		db.update(DBValue.Table_User.TABLE_NAME, cv, 
				whereClause, new String[]{user.getUserAccount()});
		
		db.close();
	}

	public void updateFollows(User user){
		db = dbUtil.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_User.COL_FOLLOWING, user.getFollowing());

		String whereClause = DBValue.Table_User.COL_USER_ACCOUNT + " = ?";
		db.update(DBValue.Table_User.TABLE_NAME, cv, 
				whereClause, new String[]{user.getUserAccount()});
		
		db.close();
	}

	public void updateLikes(User user){
		db = dbUtil.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_User.COL_LIKES, user.getTags());

		String whereClause = DBValue.Table_User.COL_USER_ACCOUNT + " = ?";
		db.update(DBValue.Table_User.TABLE_NAME, cv, 
				whereClause, new String[]{user.getUserAccount()});
		
		db.close();
	}

	public void updateTag(User user){
		db = dbUtil.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_User.COL_TAGS, user.getTags());

		String whereClause = DBValue.Table_User.COL_USER_ACCOUNT + " = ?";
		db.update(DBValue.Table_User.TABLE_NAME, cv, 
				whereClause, new String[]{user.getUserAccount()});
		
		db.close();
	}

	public void updateUid(User user){
		db = dbUtil.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_User.COL_UNIQUE_ID, user.getUniqueId());

		String whereClause = DBValue.Table_User.COL_USER_ACCOUNT + " = ?";
		db.update(DBValue.Table_User.TABLE_NAME, cv, 
				whereClause, new String[]{user.getUserAccount()});
		
		db.close();
	}

	public void updatePortraitUrl(User user){
		db = dbUtil.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_User.COL_PORTRAIT_URL, user.getPortraitUrl());

		String whereClause = DBValue.Table_User.COL_USER_ACCOUNT + " = ?";
		db.update(DBValue.Table_User.TABLE_NAME, cv, 
				whereClause, new String[]{user.getUserAccount()});
		
		db.close();
	}
	
	public ArrayList<User> getAllUsers(){
		ArrayList<User> allUsers = new ArrayList<User>();
		db = dbUtil.getReadableDatabase();

		Cursor c = db.query(DBValue.Table_User.TABLE_NAME, DBValue.Table_User.TABLE_COLS, 
				null, null, null, null, null);

		while (c.moveToNext()) {

			User user = new User();
			user.setUserAccount(c.getString(c.getColumnIndex(DBValue.Table_User.COL_USER_ACCOUNT)));
			user.setFollower(c.getInt(c.getColumnIndex(DBValue.Table_User.COL_FOLLOWERS)));
			user.setFollowing(c.getInt(c.getColumnIndex(DBValue.Table_User.COL_FOLLOWING)));
			user.setGender(c.getInt(c.getColumnIndex(DBValue.Table_User.COL_GENDER)));
			user.setLikes(c.getInt(c.getColumnIndex(DBValue.Table_User.COL_LIKES)));
			user.setPlace(c.getString(c.getColumnIndex(DBValue.Table_User.COL_PLACE)));
			user.setPortraitUrl(c.getString(c.getColumnIndex(DBValue.Table_User.COL_PORTRAIT_URL)));
			user.setTags(c.getInt(c.getColumnIndex(DBValue.Table_User.COL_TAGS)));
			user.setUniqueId(c.getString(c.getColumnIndex(DBValue.Table_User.COL_UNIQUE_ID)));
			user.setUserName(c.getString(c.getColumnIndex(DBValue.Table_User.COL_USER_NAME)));
			allUsers.add(user);
		}

		return allUsers;
	}

	public User getUserByAccount(String account){
		db = dbUtil.getReadableDatabase();

		String whereClause = DBValue.Table_User.COL_USER_ACCOUNT + " = ?";
		String[] whereArgs = {account};
		
		Cursor c = db.query(DBValue.Table_User.TABLE_NAME, DBValue.Table_User.TABLE_COLS, 
				whereClause, whereArgs, null, null, null);

		if (c.moveToNext()) {

			User user = new User();
			user.setUserAccount(account);
			user.setFollower(c.getInt(c.getColumnIndex(DBValue.Table_User.COL_FOLLOWERS)));
			user.setFollowing(c.getInt(c.getColumnIndex(DBValue.Table_User.COL_FOLLOWING)));
			user.setGender(c.getInt(c.getColumnIndex(DBValue.Table_User.COL_GENDER)));
			user.setLikes(c.getInt(c.getColumnIndex(DBValue.Table_User.COL_LIKES)));
			user.setPlace(c.getString(c.getColumnIndex(DBValue.Table_User.COL_PLACE)));
			user.setPortraitUrl(c.getString(c.getColumnIndex(DBValue.Table_User.COL_PORTRAIT_URL)));
			user.setTags(c.getInt(c.getColumnIndex(DBValue.Table_User.COL_TAGS)));
			user.setUniqueId(c.getString(c.getColumnIndex(DBValue.Table_User.COL_UNIQUE_ID)));
			user.setUserName(c.getString(c.getColumnIndex(DBValue.Table_User.COL_USER_NAME)));
			
			return user;
		}

		return null;
	}
	
}
