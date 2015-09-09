package com.mocaa.tagme.transport;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class GetMyFollows implements Transport{

	@Override
	public Object getMsg(Context context, Connection conn, Object obj,
			String[] array) {
		HashSet<String> list = new HashSet<String>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		String account = (String) obj;
		params.add(new BasicNameValuePair("account", account));
		   
		JSONObject json = conn.sendRequestForObj("get_my_follows.php", params);
		try {
			JSONArray js = json.getJSONArray("Follows");
			int length = js.length();
			for(int i=0; i<length; i++){
				list.add(js.getJSONObject(i).getString("following_account"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}

}
