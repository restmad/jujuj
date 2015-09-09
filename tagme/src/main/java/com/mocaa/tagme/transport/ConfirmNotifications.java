package com.mocaa.tagme.transport;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

public class ConfirmNotifications implements Transport{

	@Override
	public Object getMsg(Context context, Connection conn, Object obj,
			String[] array) {

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		
		String ary = "";
		for(String str:array){
			ary += str + "-" ;
		}
		
		params.add(new BasicNameValuePair("ids", ary));

		int result = conn.sendRequestForInteger("confirm_notifications.php", params);
		
		return result;
	}

}
