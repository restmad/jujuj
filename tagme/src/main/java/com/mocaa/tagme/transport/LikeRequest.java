package com.mocaa.tagme.transport;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.mocaa.tagme.entity.User;

public class LikeRequest implements Transport{
	
	public static final String M_LIKE = "1";
	public static final String M_DISLIKE = "-1";
	
	@Override
	public Object getMsg(Context context, Connection conn, Object obj, String[] array) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("user_account", array[0]));
		params.add(new BasicNameValuePair("tag_id", array[1]));
		params.add(new BasicNameValuePair("modify", array[2]));
		params.add(new BasicNameValuePair("like_account", array[3]));
		
		int result = conn.sendRequestForInteger("like.php", params);;
		
		return result;
	}

}