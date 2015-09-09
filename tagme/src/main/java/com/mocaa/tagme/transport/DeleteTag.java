package com.mocaa.tagme.transport;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

public class DeleteTag implements Transport{
	
	@Override
	public Object getMsg(Context context, Connection conn, Object obj, String[] array) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("tag_id", array[0]));
		params.add(new BasicNameValuePair("user_account", array[1]));
		
		int result = conn.sendRequestForInteger("delete_tag.php", params);
		
		return result;
	}


}
