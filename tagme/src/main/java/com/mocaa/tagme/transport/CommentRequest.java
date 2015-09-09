package com.mocaa.tagme.transport;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.mocaa.tagme.entity.Comments;
  
public class CommentRequest implements Transport{
	
	@Override
	public Object getMsg(Context context, Connection conn, Object obj, String[] array) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		Comments c = (Comments) obj;
  
		params.add(new BasicNameValuePair("my_account", c.getUserAccount()));
		params.add(new BasicNameValuePair("tag_user_account", c.getTagUserAccount()));
		params.add(new BasicNameValuePair("tag_id", ""+c.getTagId()));
		params.add(new BasicNameValuePair("content", c.getContent()));
		params.add(new BasicNameValuePair("reply_user_account", c.getReplyAccount()));
		
		int result = conn.sendRequestForInteger("comment.php", params);;
		
		return result;
	}

}