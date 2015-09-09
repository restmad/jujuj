package com.mocaa.tagme.transport;

import java.util.ArrayList;
import java.util.TreeSet;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.entity.Comments;
import com.mocaa.tagme.entity.Notification;

import android.content.Context;

public class GetNotifications implements Transport{

	@Override
	public Object getMsg(Context context, Connection conn, Object obj,
			String[] array) {

		TreeSet<Notification> list = new TreeSet<Notification>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
  
		params.add(new BasicNameValuePair("my_account", UserPref.getUserAccount(context)));

		JSONObject json = conn.sendRequestForObj("get_notifications.php", params);
		try {   
			JSONArray js = json.getJSONArray("Notifications");
			int length = js.length();
			String[] ary = null;
			if(length > 0){
				ary = new String[length];
			}
			for(int i=0; i<length; i++){
				JSONObject c = js.getJSONObject(i);
				int id = c.getInt("id");
				ary[i] = id + "";
				String userAccount = c.getString("user_account");
				String userName = c.getString("user_name");
				String time = c.getString("time");
				System.out.println("noti time:"+time);
				int tagId = c.getInt("tag_id");
				int type = c.getInt("type");
				  
				list.add(new Notification(id, userAccount, userName, time, tagId, type));
			}
			new ConfirmNotifications().getMsg(context, conn, null, ary);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

}
