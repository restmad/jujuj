package com.mocaa.tagme.transport;

import java.util.ArrayList;
import java.util.TreeSet;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mocaa.tagme.entity.Tag;

import android.content.Context;

public class GetTagInfo implements Transport{

	@Override
	public Object getMsg(Context context, Connection conn, Object obj,
			String[] array) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		
		TreeSet<Tag> tags = (TreeSet<Tag>) obj;
		String ary = "";
		for(Tag tag:tags){
			ary += tag.getServerId() + "-" ;
		}
		System.out.println(ary);
		params.add(new BasicNameValuePair("tag_id", ary));
		   
		JSONObject json = conn.sendRequestForObj("get_tag_info.php", params);
		try {
			JSONArray jsArray = json.getJSONArray("Infos");
			int size = jsArray.length();
			for(int i=0; i<size; i++){
				JSONObject js = jsArray.getJSONObject(i);
				int id = js.getInt("id");
				int comments = js.getInt("comments");
				int likes = js.getInt("likes");
				setTagInfo(tags, id, comments, likes);
			}
			return 1;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
		
	}

	private void setTagInfo(TreeSet<Tag> tags, int id, int comments, int likes){
		for(Tag tag:tags){
			if(tag.getServerId() == id){
				tag.setComments(comments);
				tag.setLikes(likes);
			}
		}
	}
}
