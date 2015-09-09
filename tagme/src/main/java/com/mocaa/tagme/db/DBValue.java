package com.mocaa.tagme.db;

import android.graphics.Bitmap;

public class DBValue {
	
	public static final String DB_NAME = "tag_me.db";
	public static final String AUTO_INCREMENT = "INTEGER PRIMARY KEY AUTOINCREMENT";

	public static class Table_Notifications {
		public static final String TABLE_NAME = "notifications";

		public static String COL_ID = "id";
		public static String COL_MY_ACCOUNT = "my_account";
		public static String COL_USER_ACCOUNT = "user_account";
		public static String COL_USER_NAME = "user_name";
		public static String COL_TYPE = "type";
		public static String COL_TAG_ID = "tag_id";
		public static String COL_TIME = "time";
		
		public static String[] TABLE_COLS = new String [] {
			COL_ID,
			COL_MY_ACCOUNT,
			COL_USER_ACCOUNT,
			COL_USER_NAME,
			COL_TYPE,
			COL_TAG_ID,
			COL_TIME
		};

		static String createSQL(){
			String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
					COL_ID + " INT, " +
					COL_MY_ACCOUNT + " VARCHAR(100), " + 
					COL_USER_ACCOUNT + " VARCHAR(100), " + 
					COL_USER_NAME + " VARCHAR(100), " + 
					COL_TYPE + " INT, " + 
					COL_TAG_ID + " INT, " + 
					COL_TIME + " VARCHAR(20))";
			return sql;
		}
	}
	
	public static class Table_Follows {
		public static final String TABLE_NAME = "follows";
		
		public static String COL_USER_ACCOUNT = "user_account";
		public static String COL_FOLLOWING_ACCOUNT = "following_account";
		public static String[] TABLE_COLS = new String [] {
			COL_FOLLOWING_ACCOUNT
		};

		static String createSQL(){
			String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
					COL_USER_ACCOUNT + " VARCHAR(100), " + 
					COL_FOLLOWING_ACCOUNT + " VARCHAR(100))";
			return sql;
		}
	}
	
	public static class Table_Likes {
		public static final String TABLE_NAME = "likes";
		
		public static String COL_USER_ACCOUNT = "user_account";
		public static String COL_TAG_ID = "tag_id";
		public static String[] TABLE_COLS = new String [] {
			COL_USER_ACCOUNT,
			COL_TAG_ID
		};

		static String createSQL(){
			String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
					COL_USER_ACCOUNT + " VARCHAR(100), " + 
					COL_TAG_ID + " INT)";
			return sql;
		}
	}
	
	public static class Table_User {
		public static final String TABLE_NAME = "user";
		
		public static String COL_ID = "id";
		public static String COL_USER_NAME = "user_name";
		public static String COL_USER_ACCOUNT = "user_account";
		public static String COL_UNIQUE_ID = "unique_id";
		public static String COL_PLACE = "place";
		public static String COL_PORTRAIT_URL = "portrait_url";
		public static String COL_GENDER = "gender";
		
		public static String COL_LIKES = "likes";
		public static String COL_FOLLOWING = "following";
		public static String COL_FOLLOWERS = "follower";
		public static String COL_TAGS = "tags";
		
		public static String[] TABLE_COLS = new String [] {
			COL_ID,
			COL_USER_ACCOUNT,
			COL_USER_NAME,
			COL_UNIQUE_ID,
			COL_PLACE,
			COL_PORTRAIT_URL,
			COL_GENDER,
			COL_LIKES,
			COL_FOLLOWING,
			COL_FOLLOWERS,
			COL_TAGS,
		};
		
		static String createSQL(){
			String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
					COL_ID + " "+AUTO_INCREMENT+", " +
					COL_USER_ACCOUNT + " VARCHAR(100), " + 
					COL_USER_NAME + " VARCHAR(20), " + 
					COL_UNIQUE_ID + " VARCHAR(20), " + 
					COL_PLACE + " VARCHAR(100), " + 
					COL_PORTRAIT_URL + " VARCHAR(200), " + 
					COL_GENDER + " INT, " + 
					COL_LIKES + " INT, " + 
					COL_FOLLOWING + " INT, " + 
					COL_FOLLOWERS + " INT, " + 
					COL_TAGS + " INT)";
			return sql;
		}
	}
	
	public static class Table_Tag {
		public static final String TABLE_NAME = "tag";

		public static String COL_ID = "id";
		public static String COL_USER_ACCOUNT = "user_account";
		public static String COL_TITLE = "title";
		public static String COL_CONTENT = "content";
		public static String COL_TYPE = "type";
		public static String COL_URI = "img_uri";
		public static String COL_URL = "img_url";
		public static String COL_DIRECTION = "direction";
		public static String COL_X = "x";
		public static String COL_Y = "y";
		public static String COL_WIDTH = "width";
		public static String COL_HEIGHT = "height";
		public static String COL_SERVER_ID = "s_id";
		public static String COL_TIME = "time";
		public static String COL_LIKES = "likes";
		public static String COL_PLACE = "place";
		public static String COL_COMMENTS = "comments";
		
		public static String[] TABLE_COLS = new String [] {
			COL_ID,
			COL_USER_ACCOUNT,
			COL_TITLE,
			COL_CONTENT,
			COL_TYPE,
			COL_URI,
			COL_URL,
			COL_DIRECTION,
			COL_X,
			COL_Y,
			COL_WIDTH,
			COL_HEIGHT,
			COL_SERVER_ID,
			COL_TIME,
			COL_LIKES,
			COL_PLACE,
			COL_COMMENTS
		};
		
		static String createSQL(){
			String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
					COL_ID + " "+AUTO_INCREMENT+", " +
					COL_USER_ACCOUNT + " VARCHAR(20), " + 
					COL_TITLE + " VARCHAR(200), " + 
					COL_CONTENT + " VARCHAR(10000), " + 
					COL_TYPE + " INT, " + 
					COL_URI + " VARCHAR(200), " + 
					COL_URL + " VARCHAR(200), " + 
					COL_DIRECTION + " INT, " + 
					COL_X + " INT, " + 
					COL_Y + " INT, " + 
					COL_WIDTH + " INT, " + 
					COL_HEIGHT + " INT, " + 
					COL_SERVER_ID + " INT, " + 
					COL_LIKES + " INT, " + 
					COL_TIME + " VARCHAR(20), " + 
					COL_PLACE + " VARCHAR(200), " + 
					COL_COMMENTS + " INT)";
			return sql;
		}
	}

	public static class Table_Own_Tag {
		public static final String TABLE_NAME = "my_tag";

		public static String COL_ID = "id";
		public static String COL_CHILD_TAG = "oid";
		
		public static String[] TABLE_COLS = new String [] {
			COL_ID,
			COL_CHILD_TAG
		};
		
		static String createSQL(){
			String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
					COL_ID + " INT, " +
					COL_CHILD_TAG + " INT)";
			return sql;
		}
	}
}
