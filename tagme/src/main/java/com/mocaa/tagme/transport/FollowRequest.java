package com.mocaa.tagme.transport;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.mocaa.tagme.entity.User;

public class FollowRequest implements Transport{
	
	public static final String M_FOLLOW = "1";
	public static final String M_UNFOLLOW = "-1";
	
	@Override
	public Object getMsg(Context context, Connection conn, Object obj, String[] array) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("account", array[0]));
		params.add(new BasicNameValuePair("following_account", array[1]));
		params.add(new BasicNameValuePair("modify", array[2]));
		
		int result = conn.sendRequestForInteger("follow.php", params);;
		
		return result;
	}

}