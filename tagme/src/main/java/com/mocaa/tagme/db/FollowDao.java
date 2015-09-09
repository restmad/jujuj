package com.mocaa.tagme.db;

import java.util.HashSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FollowDao {

	private DBUtil dbUtil;
	private SQLiteDatabase db;

	public FollowDao(Context context) {
		dbUtil = new DBUtil(context);
	}
	
	public HashSet<String> getFollowing(String account){
		HashSet<String> list = new HashSet<String> ();
		db = dbUtil.getReadableDatabase();

		String whereClause = DBValue.Table_Follows.COL_USER_ACCOUNT + " = ?";
		String[] whereArgs = {account};
		
		Cursor c = db.query(DBValue.Table_Follows.TABLE_NAME, DBValue.Table_Follows.TABLE_COLS, 
				whereClause, whereArgs, null, null, null);

		while (c.moveToNext()) {
			String following = c.getString(c.getColumnIndex(
					DBValue.Table_Follows.COL_FOLLOWING_ACCOUNT));
			list.add(following);
		}
		
		return list;
	}
	
	public void follow(String account, String following){
		db = dbUtil.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_Follows.COL_FOLLOWING_ACCOUNT, following);
		cv.put(DBValue.Table_User.COL_USER_ACCOUNT, account);

		db.insert(DBValue.Table_Follows.TABLE_NAME, null, cv);
		db.close();
	}
	
	public void cancelFollow(String account, String following){
		db = dbUtil.getWritableDatabase();

		String whereClause = DBValue.Table_Follows.COL_USER_ACCOUNT + " = ? and " +
				DBValue.Table_Follows.COL_FOLLOWING_ACCOUNT + " = ?";
		String[] whereArgs = {account, following};

		db.delete(DBValue.Table_Follows.TABLE_NAME, whereClause, whereArgs);
		db.close();
	}

	private void removeALl(String account){

		String whereClause = DBValue.Table_Follows.COL_USER_ACCOUNT + " = ?";
		String[] whereArgs = {account};

		db.delete(DBValue.Table_Follows.TABLE_NAME, whereClause, whereArgs);
	}
	
	public void syncWithServer(String account, HashSet<String> list){
		db = dbUtil.getWritableDatabase();
		removeALl(account);
		for(String following:list){
			db = dbUtil.getWritableDatabase();
			
			System.out.println("sync friends:"+following);
			ContentValues cv = new ContentValues();
			cv.put(DBValue.Table_Follows.COL_FOLLOWING_ACCOUNT, following);
			cv.put(DBValue.Table_User.COL_USER_ACCOUNT, account);

			db.insert(DBValue.Table_Follows.TABLE_NAME, null, cv);
		}
		db.close();
	}
}
