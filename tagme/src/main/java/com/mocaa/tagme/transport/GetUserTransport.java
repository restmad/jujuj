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

public class GetUserTransport implements Transport{
	  
	@Override
	public Object getMsg(Context context, Connection conn, Object obj, String[] array) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		String account = array[0];
		System.out.println("get user:"+account);
		params.add(new BasicNameValuePair("user_account", account));

		User user = new User();
		try {
			JSONObject js = conn.sendRequestForObj("get_user.php", params);
//			JSONObject js = result.getJSONObject("User");
            user.setUserName(js.getString("user_name"));
            user.setUserAccount(js.getString("account"));
            user.setFollower(js.getInt("follower"));
            user.setFollowing(js.getInt("following"));
            user.setLikes(js.getInt("likes"));
            user.setPlace(js.getString("place"));
            user.setPortraitUrl(js.getString("portrait"));
            user.setTags(js.getInt("tags"));
            user.setUniqueId(js.getString("unique_id"));
            user.setGender(js.getInt("gender"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return user;
	}
	
}
