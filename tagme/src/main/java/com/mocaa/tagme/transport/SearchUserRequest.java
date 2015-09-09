package com.mocaa.tagme.transport;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mocaa.tagme.db.UserDao;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;

import android.content.Context;
import android.util.Log;

public class SearchUserRequest implements Transport{
	  
	@Override
	public Object getMsg(Context context, Connection conn, Object obj, String[] array) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
  
		params.add(new BasicNameValuePair("key", array[0]));
		
		ArrayList<String> results = new ArrayList<String>();
		try {
			JSONObject result = conn.sendRequestForObj("searching_users.php", params);
			JSONArray ja = result.getJSONArray("Users");
			int length = ja.length();
			for(int i=0; i<length; i++){
				JSONObject js = ja.getJSONObject(i);
				results.add(js.getString("account"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return results;
	}
	
}
