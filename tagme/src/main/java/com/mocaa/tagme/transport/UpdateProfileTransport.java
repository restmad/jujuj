package com.mocaa.tagme.transport;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.mocaa.tagme.db.TagDao;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;

import android.content.Context;
import android.graphics.Bitmap;

public class UpdateProfileTransport implements Transport{
	
	/**
	 * Object obj bitmap
	 */
	@Override
	public Object getMsg(Context context, Connection conn, Object obj,
			String[] array) {
		
		Bitmap bm = (Bitmap) obj;

		String url = "";
		if(bm != null){
			url = conn.uploadPic("upload_img.php", null, bm);
			if(url == null){
				return -1;
			}
		}
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userAccount", UserPref.getUserAccount(context)));
		params.add(new BasicNameValuePair("portraitUrl", url));
		
		try {
			params.add(new BasicNameValuePair("user_name", new String(array[0].getBytes("utf-8"))));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		params.add(new BasicNameValuePair("place", array[1]));
		params.add(new BasicNameValuePair("gender", array[2]));
		
		int result = conn.sendRequestForInteger("update_profile.php", params);
		if(result > 0){
			return url;
		}else{
			return result;
		}
	}

	
}
