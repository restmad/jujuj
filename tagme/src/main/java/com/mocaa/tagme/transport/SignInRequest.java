package com.mocaa.tagme.transport;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mocaa.tagme.entity.User;

import android.content.Context;

public class SignInRequest implements Transport{

	@Override
	public Object getMsg(Context context, Connection conn, Object obj, String[] array) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		String account = array[0];
		params.add(new BasicNameValuePair("account", account));
		params.add(new BasicNameValuePair("pwd", array[1]));
		
		String str = conn.sendRequestForString("sign_in.php", params);
		
		try {
			JSONObject json = new JSONObject(str);
			JSONObject js = json.getJSONObject("User");
			
			String uid = js.getString("unique_id");
			int gender = js.getInt("gender");
			int following = js.getInt("following");
			int follower = js.getInt("follower");
			int likes = js.getInt("likes");
			int tags = js.getInt("tags");
			String portrait = js.getString("portrait");
			String user_name = js.getString("user_name");
			String place = js.getString("place");
			
			User user = new User(user_name, account, uid,
					place, portrait, gender, tags,
					following, follower, likes);
			
			
			return user;
		} catch (Exception e) {
			Integer result = -5;
			try {
				result = Integer.parseInt(str.trim());
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			}
			return result;
		}
	}

}
