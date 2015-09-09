package com.mocaa.tagme.transport;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.mocaa.tagme.db.TagDao;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;

import android.content.Context;
import android.graphics.Bitmap;

public class UpDateUidTransport implements Transport{
	
	/**
	 * Object obj bitmap
	 */
	@Override
	public Object getMsg(Context context, Connection conn, Object obj,
			String[] array) {
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("account", array[0]));
		params.add(new BasicNameValuePair("uid", array[1]));
		
		int result = conn.sendRequestForInteger("update_uid.php", params);
		
		return result;
	}

	
}
