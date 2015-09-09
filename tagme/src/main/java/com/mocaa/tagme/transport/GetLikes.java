package com.mocaa.tagme.transport;

import android.content.Context;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TreeSet;

public class GetLikes implements Transport{

	@Override
	public Object getMsg(Context context, Connection conn, Object obj,
			String[] array) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        System.out.println("get likes 1" + (String) obj);
		params.add(new BasicNameValuePair("tag_id", (String) obj));
        System.out.println("get likes 2");
        ArrayList<String> users = new ArrayList<String>();
		JSONObject json = conn.sendRequestForObj("get_likes.php", params);
        System.out.println("get likes 3");
		try {
			JSONArray jsArray = json.getJSONArray("Users");
			int size = jsArray.length();
			for(int i=0; i<size; i++){
				JSONObject js = jsArray.getJSONObject(i);
                String account = js.getString("user_account");
                users.add(account);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}

        return users;
	}

}
