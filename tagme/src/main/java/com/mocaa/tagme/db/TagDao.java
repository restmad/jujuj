package com.mocaa.tagme.db;

import java.util.ArrayList;
import java.util.TreeSet;

import com.mocaa.tagme.entity.Point;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.transport.GetTagTransport;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TagDao {
	
	private DBUtil dbUtil;
	private SQLiteDatabase db;
	
	public TagDao(Context context) {
		dbUtil = new DBUtil(context);
	}
	
	public void deleteTagWithChildren(Tag tag){
		deleteTag(tag);
		for(Tag child:tag.getTags()){
			if(child != null){
				deleteTagWithChildren(child);
			}
		}
	}

	public void deleteTagByServerId(Tag tag){
		db = dbUtil.getWritableDatabase();
		System.err.println("delete tag by sid:"+tag.getServerId());
		
		String whereClause = DBValue.Table_Tag.COL_SERVER_ID + " = ?";
		int result = db.delete(DBValue.Table_Tag.TABLE_NAME, whereClause, new String[]{tag.getServerId()+""});
		System.out.println("delete result:"+result);
		db.close();
	}
	
	public void deleteTag(Tag tag){
		db = dbUtil.getWritableDatabase();
		System.err.println("delete tag:"+tag.getId());
		
		String whereClause = DBValue.Table_Tag.COL_ID + " = ?";
		db.delete(DBValue.Table_Tag.TABLE_NAME, whereClause, new String[]{tag.getId()+""});

		//delete own table
		String where = DBValue.Table_Own_Tag.COL_CHILD_TAG + " = ?";
		db.delete(DBValue.Table_Own_Tag.TABLE_NAME, where, new String[]{tag.getId()+""});
		
		db.close();
	}
	
	private void editTagLocation(Tag tag){
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_Tag.COL_X, tag.getLocation().x);
		cv.put(DBValue.Table_Tag.COL_Y, tag.getLocation().y);
		cv.put(DBValue.Table_Tag.COL_DIRECTION, tag.getDirection());

		String whereClause = DBValue.Table_Tag.COL_ID + " = ?";
		db.update(DBValue.Table_Tag.TABLE_NAME, cv, 
				whereClause, new String[]{tag.getId()+""});
	}
	
	public void editTag(Tag tag){
		editMyTag(tag);
		db.close();
	}
	
	public void connectWithServer(Tag tag){
		db = dbUtil.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_Tag.COL_SERVER_ID, tag.getServerId());
		cv.put(DBValue.Table_Tag.COL_URL, tag.getImgUrl());
		
		System.out.println("connect with server:"+tag.getServerId() + " " +tag.getId());
		
		String whereClause = DBValue.Table_Tag.COL_ID + " = ?";
		db.update(DBValue.Table_Tag.TABLE_NAME, cv, 
				whereClause, new String[]{tag.getId()+""});
		
		db.close();
	}
	
	private void editMyTag(Tag tag){
		db = dbUtil.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_Tag.COL_USER_ACCOUNT, tag.getUserAccount());
		cv.put(DBValue.Table_Tag.COL_TITLE, tag.getTitle());
		cv.put(DBValue.Table_Tag.COL_CONTENT, tag.getContent());
		cv.put(DBValue.Table_Tag.COL_TYPE, tag.getType());
		cv.put(DBValue.Table_Tag.COL_URI, tag.getImgUri());
		cv.put(DBValue.Table_Tag.COL_URL, tag.getImgUrl());
		cv.put(DBValue.Table_Tag.COL_DIRECTION, tag.getDirection());
		cv.put(DBValue.Table_Tag.COL_X, tag.getLocation().x);
		cv.put(DBValue.Table_Tag.COL_Y, tag.getLocation().y);
		cv.put(DBValue.Table_Tag.COL_WIDTH, tag.getWidth());
		cv.put(DBValue.Table_Tag.COL_HEIGHT, tag.getHeight());
		cv.put(DBValue.Table_Tag.COL_TIME, tag.getTime());
		cv.put(DBValue.Table_Tag.COL_PLACE, tag.getPlace());
		
		String whereClause = DBValue.Table_Tag.COL_ID + " = ?";
		db.update(DBValue.Table_Tag.TABLE_NAME, cv, 
				whereClause, new String[]{tag.getId()+""});
		
		ArrayList<Tag> tags = tag.getTags();
		for(Tag child:tags){
			editTagLocation(child);
		}
		db.close();
	}

	public void updateTagInfo(Tag tag){
		db = dbUtil.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_Tag.COL_COMMENTS, tag.getComments());
		cv.put(DBValue.Table_Tag.COL_LIKES, tag.getLikes());
		
		String whereClause = DBValue.Table_Tag.COL_ID + " = ?";
		db.update(DBValue.Table_Tag.TABLE_NAME, cv, 
				whereClause, new String[]{tag.getId()+""});
		db.close();
	}
	
	public void setTagLocation(Tag tag, Point pt){
		db = dbUtil.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_Tag.COL_X, pt.x);
		cv.put(DBValue.Table_Tag.COL_Y, pt.y);
		
		String whereClause = DBValue.Table_Tag.COL_ID + " = ?";
		db.update(DBValue.Table_Tag.TABLE_NAME, cv, 
				whereClause, new String[]{tag.getId()+""});
		
		db.close();
	}
	
	public void saveTagWithId(Tag tag, boolean bClose){
		db = dbUtil.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_Tag.COL_USER_ACCOUNT, tag.getUserAccount());
		cv.put(DBValue.Table_Tag.COL_SERVER_ID, tag.getServerId());
		cv.put(DBValue.Table_Tag.COL_TITLE, tag.getTitle());
		cv.put(DBValue.Table_Tag.COL_CONTENT, tag.getContent());
		cv.put(DBValue.Table_Tag.COL_TYPE, tag.getType());
		cv.put(DBValue.Table_Tag.COL_URI, tag.getImgUri());
		cv.put(DBValue.Table_Tag.COL_URL, tag.getImgUrl());
		cv.put(DBValue.Table_Tag.COL_DIRECTION, tag.getDirection());
		cv.put(DBValue.Table_Tag.COL_X, tag.getLocation().x);
		cv.put(DBValue.Table_Tag.COL_Y, tag.getLocation().y);
		cv.put(DBValue.Table_Tag.COL_WIDTH, tag.getWidth());
		cv.put(DBValue.Table_Tag.COL_HEIGHT, tag.getHeight());
		cv.put(DBValue.Table_Tag.COL_TIME, tag.getTime());
		cv.put(DBValue.Table_Tag.COL_LIKES, tag.getLikes());
		cv.put(DBValue.Table_Tag.COL_PLACE, tag.getPlace());
		
		db.insert(DBValue.Table_Tag.TABLE_NAME, null, cv);
		
		tag.setId(getTagId());
		if(bClose){
			db.close();
		}
	}

	private int getTagId(){
		Cursor c = db.rawQuery("select last_insert_rowid() from " + 
				DBValue.Table_Tag.TABLE_NAME, null);          
		int id = -1;    
		if(c.moveToFirst()){
			id = c.getInt(0); 
		}

		c.close();
		return id;
	}
	
	public void saveTagWithParent(int pId, Tag tag){
		if(tag == null){
			return;
		}
		saveTagWithId(tag, false);
		
		saveChildrenTag(pId, tag.getId());
		db.close();
	}
	
	public void saveTagAll(Tag tag){
		saveTagWithId(tag, false);

		ArrayList<Tag> tags = tag.getTags();
		for(Tag vo:tags){
			saveTagAll(vo);
			saveChildrenTag(tag.getId(), vo.getId());
		}
		db.close();
	}
	
	private void saveChildrenTag(int id, int childId){
		db = dbUtil.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_Own_Tag.COL_ID, id);
		cv.put(DBValue.Table_Own_Tag.COL_CHILD_TAG, childId);
		db.insert(DBValue.Table_Own_Tag.TABLE_NAME, null, cv);
	}
	
	private void getChildrenTags(Tag tag){
		
		String whereClause = DBValue.Table_Own_Tag.COL_ID + " = ?";
		String[] whereArgs = {tag.getId()+""};
		
		Cursor c = db.query(DBValue.Table_Own_Tag.TABLE_NAME, DBValue.Table_Own_Tag.TABLE_COLS, 
				whereClause, whereArgs, null, null, null);

		ArrayList<Tag> children = new ArrayList<Tag>();
		while (c.moveToNext()) {
			int oid = c.getInt(c.getColumnIndex(DBValue.Table_Own_Tag.COL_CHILD_TAG));
			Tag child = getTagById(oid);
//			getChildrenTags(child);
			children.add(child);
		}
		tag.setTags(children);
		c.close();
	}
	
	public Tag getTagByServerId(int id){
		Tag tag = new Tag();
		
		db = dbUtil.getReadableDatabase();

		String whereClause = DBValue.Table_Tag.COL_SERVER_ID + " = ?";
		String[] whereArgs = {id+""};
		
		Cursor c = db.query(DBValue.Table_Tag.TABLE_NAME, DBValue.Table_Tag.TABLE_COLS, 
				whereClause, whereArgs, null, null, null);

		if (c.moveToNext()) {
			tag = getTagByCursor(c);
			getChildrenTags(tag);
		}
		db.close();

		c.close();
		return tag;
	}
	
	private Tag getTagById(int id){
		Tag tag = new Tag();

		String whereClause = DBValue.Table_Tag.COL_ID + " = ?";
		String[] whereArgs = {id+""};
		
		Cursor c = db.query(DBValue.Table_Tag.TABLE_NAME, DBValue.Table_Tag.TABLE_COLS, 
				whereClause, whereArgs, null, null, null);
		
		if (c.moveToNext()) {
			tag = getTagByCursor(c);
			getChildrenTags(tag);
		}
		
		c.close();
		return tag;
	}
	
	public void testAll(){
		db = dbUtil.getReadableDatabase();
		
		Cursor c = db.query(DBValue.Table_Tag.TABLE_NAME, DBValue.Table_Tag.TABLE_COLS, 
				null, null, null, null, null);
		
		while (c.moveToNext()) {
			int id = c.getInt(c.getColumnIndex(DBValue.Table_Tag.COL_ID));
			String name = c.getString(c.getColumnIndex(DBValue.Table_Tag.COL_USER_ACCOUNT));
			System.out.println("test all:"+id+","+name);
		}
		db.close();
	}
	
	public static final int LIMIT = 8;
	
	public TreeSet<Tag> getMore(int type, int id){
		TreeSet<Tag> tags = new TreeSet<Tag>();

		db = dbUtil.getReadableDatabase();
		
		String where = "";
		if(type == GetTagTransport.FLAG_REFRESH){
			where = " AND " + DBValue.Table_Tag.COL_SERVER_ID + "> "+id;
		}else{
			where = " AND " + DBValue.Table_Tag.COL_SERVER_ID + "< "+id;
		}

		String whereClause = DBValue.Table_Tag.COL_TYPE + " = " + Tag.TYPE_ROOT + 
				where + 
				" ORDER BY "  + DBValue.Table_Tag.COL_SERVER_ID + " DESC" +
				" Limit " + LIMIT;
		
		Cursor c = db.query(DBValue.Table_Tag.TABLE_NAME, DBValue.Table_Tag.TABLE_COLS, 
				whereClause, null, null, null, null);
		
		while (c.moveToNext()) {
			Tag tag = getTagByCursor(c);
			
			getChildrenTags(tag);
			tags.add(tag);
		}
		db.close();
		
		return tags;
	}

	public TreeSet<Tag> getAllTags(){
		TreeSet<Tag> tags = new TreeSet<Tag>();
		
		db = dbUtil.getReadableDatabase();

		String whereClause = DBValue.Table_Tag.COL_TYPE + " = " + Tag.TYPE_ROOT +
				" ORDER BY "  + DBValue.Table_Tag.COL_SERVER_ID + " DESC" +
				" Limit " + LIMIT;
		
		Cursor c = db.query(DBValue.Table_Tag.TABLE_NAME, DBValue.Table_Tag.TABLE_COLS, 
				whereClause, null, null, null, null);
		
		while (c.moveToNext()) {
			Tag tag = getTagByCursor(c);
			
			getChildrenTags(tag);
			tags.add(tag);
		}
		db.close();
		
		return tags;
	}
	
	public TreeSet<Tag> getMyTags(String account, int id){
		String w = " AND " + DBValue.Table_Tag.COL_SERVER_ID + " < "+id;
		TreeSet<Tag> tags = new TreeSet<Tag>();
		
		db = dbUtil.getReadableDatabase();

		String whereClause = DBValue.Table_Tag.COL_USER_ACCOUNT + " = ? AND " + 
					DBValue.Table_Tag.COL_TYPE + " = " + Tag.TYPE_ROOT + w;
		String[] whereArgs = {account};
		
		Cursor c = db.query(DBValue.Table_Tag.TABLE_NAME, DBValue.Table_Tag.TABLE_COLS, 
				whereClause, whereArgs, null, null, null);
		
		while (c.moveToNext()) {
			Tag tag = getTagByCursor(c);
			
			getChildrenTags(tag);
			tags.add(tag);
		}
		db.close();
		
		return tags;
	}
	
	private Tag getTagByCursor(Cursor c){
		Tag tag = new Tag();
		String account = c.getString(c.getColumnIndex(DBValue.Table_Tag.COL_USER_ACCOUNT));
		tag.setUserAccount(account);
		int sid = c.getInt(c.getColumnIndex(DBValue.Table_Tag.COL_SERVER_ID));
		tag.setServerId(sid);
		int id = c.getInt(c.getColumnIndex(DBValue.Table_Tag.COL_ID));
		tag.setId(id);
		String title = c.getString(c.getColumnIndex(DBValue.Table_Tag.COL_TITLE));
		tag.setTitle(title);
		String content = c.getString(c.getColumnIndex(DBValue.Table_Tag.COL_CONTENT));
		tag.setContent(content);
		int type = c.getInt(c.getColumnIndex(DBValue.Table_Tag.COL_TYPE));
		tag.setType(type);
		String uri = c.getString(c.getColumnIndex(DBValue.Table_Tag.COL_URI));
		tag.setImgUri(uri);
		String url = c.getString(c.getColumnIndex(DBValue.Table_Tag.COL_URL));
		tag.setImgUrl(url);
		int direction = c.getInt(c.getColumnIndex(DBValue.Table_Tag.COL_DIRECTION));
		tag.setDirection(direction);
		int x = c.getInt(c.getColumnIndex(DBValue.Table_Tag.COL_X));
		int y = c.getInt(c.getColumnIndex(DBValue.Table_Tag.COL_Y));
		tag.setLocation(new Point(x, y));
		int width = c.getInt(c.getColumnIndex(DBValue.Table_Tag.COL_WIDTH));
		tag.setWidth(width);
		int height = c.getInt(c.getColumnIndex(DBValue.Table_Tag.COL_HEIGHT));
		tag.setHeight(height);
		String time = c.getString(c.getColumnIndex(DBValue.Table_Tag.COL_TIME));
		tag.setTime(time);
		int likes = c.getInt(c.getColumnIndex(DBValue.Table_Tag.COL_LIKES));
		tag.setLikes(likes);
		String place = c.getString(c.getColumnIndex(DBValue.Table_Tag.COL_PLACE));
		tag.setPlace(place);
		int comments = c.getInt(c.getColumnIndex(DBValue.Table_Tag.COL_COMMENTS));
		tag.setComments(comments);
		
		return tag;
	}
}
