package com.mocaa.tagme.db;

import java.io.UTFDataFormatException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBUtil extends SQLiteOpenHelper{

	private Context context;
	
	public DBUtil(Context context) {
		super(context, DBValue.DB_NAME, null, 1);
		this.context = context;
	}
	
	public DBUtil(Context context,int version) {
		this(context, DBValue.DB_NAME, null, version);
	}
	
	public DBUtil(Context context, String name,int version) {
		this(context, name, null, version);
	}
	
	public DBUtil(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("onCreate");
		
		db.execSQL(DBValue.Table_Tag.createSQL());
		db.execSQL(DBValue.Table_Own_Tag.createSQL());
		db.execSQL(DBValue.Table_User.createSQL());
		db.execSQL(DBValue.Table_Likes.createSQL());
		db.execSQL(DBValue.Table_Follows.createSQL());
		db.execSQL(DBValue.Table_Notifications.createSQL());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("onUpgrade");
	}
	
	
	public void resetAll(){
		SQLiteDatabase db = getWritableDatabase();
		db.delete(DBValue.Table_Own_Tag.TABLE_NAME, null, null);
		db.delete(DBValue.Table_Tag.TABLE_NAME, null, null);
		
	}
}
