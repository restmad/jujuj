package com.mocaa.tagme.transport;

import java.util.ArrayList;
import java.util.TreeSet;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;

import android.content.Context;
import android.util.Log;

public class GetSingleTag implements Transport{

	public static final int FLAG_REFRESH = 1; 
	public static final int FLAG_MORE = 2; 
	
	@Override
	public Object getMsg(Context context, Connection conn, Object obj, String[] array) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("id", (String) obj));
		
		JSONObject result = conn.sendRequestForObj("get_tag_by_id.php", params);
		
		try {
            JSONObject js = result.getJSONObject("Tag");
            Tag tag = getTag(js);
        	return tag;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private Tag getTag(JSONObject js){
    	try {
			int id = js.getInt("id");
			String userAccount = js.getString("user_account");
			String title = js.getString("title");
			String content = js.getString("content");
			int type = js.getInt("t_type");
			String img_url = js.getString("img_url");
			int direction = js.getInt("direction");
			int x = js.getInt("x");
			int y = js.getInt("y");
			int width = js.getInt("width");
			int height = js.getInt("height");
			String upload_time = js.getString("upload_time");
			int likes = js.getInt("likes");
			int comments = js.getInt("comments");
			String place = js.getString("place");
			
			ArrayList<Tag> cTags = new ArrayList<Tag>();
			if(!js.isNull("children")){
				JSONArray children = js.getJSONArray("children");
				int length = children.length();
				for(int i=0; i<length; i++){
					Tag child = getTag(children.getJSONObject(i));
					cTags.add(child);
				}
			}
    		Tag tag = new Tag(id, userAccount, title, content, type, x, y, width, height, 
    				upload_time, likes, comments, img_url, direction, cTags, place);
    		return tag;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return new Tag();
	}

}
