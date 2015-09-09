package com.mocaa.tagme.db;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LikeDao {

	private DBUtil dbUtil;
	private SQLiteDatabase db;

	public LikeDao(Context context) {
		dbUtil = new DBUtil(context);
	}
	
	public HashSet<Integer> getLikes(String account){
		HashSet<Integer> list = new HashSet<Integer> ();
		db = dbUtil.getReadableDatabase();

		String whereClause = DBValue.Table_Likes.COL_USER_ACCOUNT + " = ?";
		String[] whereArgs = {account};
		
		Cursor c = db.query(DBValue.Table_Likes.TABLE_NAME, DBValue.Table_Likes.TABLE_COLS, 
				whereClause, whereArgs, null, null, null);

		while (c.moveToNext()) {
			int id = c.getInt(c.getColumnIndex(DBValue.Table_Likes.COL_TAG_ID));
			list.add(id);
		}
		
		return list;
	}
	
	public void like(String account, int id){
		db = dbUtil.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_Likes.COL_TAG_ID, id);
		cv.put(DBValue.Table_User.COL_USER_ACCOUNT, account);

		db.insert(DBValue.Table_Likes.TABLE_NAME, null, cv);
		db.close();
	}
	
	public void cancelLike(String account, int id){
		db = dbUtil.getWritableDatabase();

		String whereClause = DBValue.Table_Likes.COL_USER_ACCOUNT + " = ? and " +
				DBValue.Table_Likes.COL_TAG_ID + " = ?";
		String[] whereArgs = {account, ""+id};

		db.delete(DBValue.Table_Likes.TABLE_NAME, whereClause, whereArgs);
		db.close();
	}

	private void removeALl(String account){

		String whereClause = DBValue.Table_Likes.COL_USER_ACCOUNT + " = ?";
		String[] whereArgs = {account};

		db.delete(DBValue.Table_Likes.TABLE_NAME, whereClause, whereArgs);
	}
	
	public void syncWithServer(String account, HashSet<Integer> list){
		db = dbUtil.getWritableDatabase();
		removeALl(account);
		for(int id:list){
			db = dbUtil.getWritableDatabase();
			
			System.out.println("sync likes:"+id);
			ContentValues cv = new ContentValues();
			cv.put(DBValue.Table_Likes.COL_TAG_ID, id);
			cv.put(DBValue.Table_User.COL_USER_ACCOUNT, account);

			db.insert(DBValue.Table_Likes.TABLE_NAME, null, cv);
		}
		db.close();
	}
}
