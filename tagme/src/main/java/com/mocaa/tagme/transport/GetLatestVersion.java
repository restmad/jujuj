package com.mocaa.tagme.transport;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

public class GetLatestVersion implements Transport{
	
	@Override
	public Object getMsg(Context context, Connection conn, Object obj, String[] array) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
  
		String result = conn.sendRequestForString("latest_version.php", params);
		if(result != null){
			result = result.trim();
		}
		
		return result;
	}


}
