package com.mocaa.tagme.transport;

import java.util.ArrayList;
import java.util.TreeSet;

import com.mocaa.tagme.util.TimeUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mocaa.tagme.entity.Comments;

import android.content.Context;

public class GetComments implements Transport{

	@Override
	public Object getMsg(Context context, Connection conn, Object obj,
			String[] array) {

		TreeSet<Comments> list = new TreeSet<Comments>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		int tag_id = (Integer) obj;
		System.out.println("tagid:"+tag_id);
		params.add(new BasicNameValuePair("tag_id", ""+tag_id));

		JSONObject json = conn.sendRequestForObj("get_comments.php", params);
		try {
			JSONArray js = json.getJSONArray("Comments");
			int length = js.length();
			for(int i=0; i<length; i++){
				JSONObject c = js.getJSONObject(i);
				String userAccount = c.getString("user_account");
				String content = c.getString("content");
				String time = c.getString("time");
				String replyAccount = c.getString("reply_user_account");
				String tagUserAccount = c.getString("tag_user_account");
				Comments comment = new Comments(tag_id, tagUserAccount, userAccount,
						content, new TimeUtil(time), replyAccount);
				list.add(comment);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

}
