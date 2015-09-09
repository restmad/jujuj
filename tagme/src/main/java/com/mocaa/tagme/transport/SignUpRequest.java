package com.mocaa.tagme.transport;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.mocaa.tagme.entity.User;

import android.content.Context;
import android.util.Log;

public class SignUpRequest implements Transport{

	@Override
	public Object getMsg(Context context, Connection conn, Object obj, String[] array) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
  
		String account = array[0];
		String name = getUserNameFromAccount(account);
		params.add(new BasicNameValuePair("userAccount", account));
		params.add(new BasicNameValuePair("pwd", array[1]));
		params.add(new BasicNameValuePair("userName", name));
		
		int result = conn.sendRequestForInteger("sign_up.php", params);
		
		if(result < 0){
			return result;
		}else{
			User user = new User();
			user.setUserAccount(account);
			user.setUserName(name);
			return user;
		}
		
	}

	private String getUserNameFromAccount(String account){
		int idx = account.indexOf('@');
		if(idx < 0 || idx >= account.length()){
			return "TagMe";
		}
		String name = account.substring(0, idx);
		return name;
	}
}
