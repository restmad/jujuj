package com.mocaa.tagme.db;

import java.util.HashSet;
import java.util.TreeSet;

import com.mocaa.tagme.entity.Notification;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NotiDao {

	private DBUtil dbUtil;
	private SQLiteDatabase db;
	private Context context;

	public NotiDao(Context context) {
		dbUtil = new DBUtil(context);
		this.context = context;
	}
	
	public TreeSet<Notification> getAll(){
		
		TreeSet<Notification> list = new TreeSet<Notification> ();
		db = dbUtil.getReadableDatabase();

		String whereClause = DBValue.Table_Notifications.COL_MY_ACCOUNT + " = ?";
		String[] whereArgs = {UserPref.getUserAccount(context)};
		
		Cursor c = db.query(DBValue.Table_Notifications.TABLE_NAME, DBValue.Table_Notifications.TABLE_COLS, 
				whereClause, whereArgs, null, null, null);

		while (c.moveToNext()) {
			int id = c.getInt(c.getColumnIndex(
					DBValue.Table_Notifications.COL_ID));
			String userAccount = c.getString(c.getColumnIndex(
					DBValue.Table_Notifications.COL_USER_ACCOUNT));
			String userName = c.getString(c.getColumnIndex(
					DBValue.Table_Notifications.COL_USER_NAME));
			String time = c.getString(c.getColumnIndex(
					DBValue.Table_Notifications.COL_TIME));
			int tag_id = c.getInt(c.getColumnIndex(
					DBValue.Table_Notifications.COL_TAG_ID));
			int type = c.getInt(c.getColumnIndex(
					DBValue.Table_Notifications.COL_TYPE));
			list.add(new Notification(id, userAccount, userName, time, tag_id, type));
		}
		
		return list;
	}

	public void addAll(TreeSet<Notification> list){
		db = dbUtil.getWritableDatabase();
		
		for(Notification noti:list){
			add(noti);
		}
		
		db.close();
	}
	
	private void add(Notification noti){
		
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_Notifications.COL_ID, noti.getId());
		cv.put(DBValue.Table_Notifications.COL_MY_ACCOUNT, UserPref.getUserAccount(context));
		cv.put(DBValue.Table_Notifications.COL_USER_ACCOUNT, noti.getUserAccount());
		cv.put(DBValue.Table_Notifications.COL_USER_NAME, noti.getUserName());
		cv.put(DBValue.Table_Notifications.COL_TYPE, noti.getType());
		cv.put(DBValue.Table_Notifications.COL_TAG_ID, noti.getTagId());
		cv.put(DBValue.Table_Notifications.COL_TIME, noti.getTime());

		db.insert(DBValue.Table_Notifications.TABLE_NAME, null, cv);
	}
	
	public void delete(Notification noti){
		db = dbUtil.getWritableDatabase();

		String whereClause = DBValue.Table_Notifications.COL_ID + " = ?";
		String[] whereArgs = {noti.getId()+""};

		db.delete(DBValue.Table_Notifications.TABLE_NAME, whereClause, whereArgs);
		db.close();
	}

}
