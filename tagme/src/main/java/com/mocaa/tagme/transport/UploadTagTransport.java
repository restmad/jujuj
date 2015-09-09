package com.mocaa.tagme.transport;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.mocaa.tagme.db.TagDao;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.global.GlobalDefs;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class UploadTagTransport implements Transport{
	
	@Override
	public Object getMsg(Context context, Connection conn, Object obj,
			String[] array) {
		
		Tag tag = (Tag) obj;
		
		String url = "";
		if(tag.getImgUri() != null && !tag.equals("")){
			Bitmap bm = tag.getImg(context);
			GlobalDefs.o("uploading:"+tag.getTitle() +" "+ 
						tag.getImgUri() + " " + (bm == null));
			if(bm != null){
				url = conn.uploadPic("upload_img.php", null, bm);
			}
		}
		if(url != null){
			tag.setImgUrl(url);
		}else{
			return -1;
		}
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("content", tag.getContent()));
		params.add(new BasicNameValuePair("url", tag.getImgUrl()));
		params.add(new BasicNameValuePair("time", tag.getTime()));
		params.add(new BasicNameValuePair("title", tag.getTitle()));
		params.add(new BasicNameValuePair("user_account", tag.getUserAccount()));
		params.add(new BasicNameValuePair("direction", tag.getDirection()+""));
		params.add(new BasicNameValuePair("height", tag.getHeight()+""));
		params.add(new BasicNameValuePair("width", tag.getWidth()+""));
		params.add(new BasicNameValuePair("id", tag.getId()+""));
		params.add(new BasicNameValuePair("x", tag.getLocation().x+""));
		params.add(new BasicNameValuePair("y", tag.getLocation().y+""));
		params.add(new BasicNameValuePair("type", tag.getType()+""));
		params.add(new BasicNameValuePair("place", tag.getPlace()));
		
		int pId = -1;
		if(array != null && array.length > 0){
			try {
				pId = Integer.parseInt(array[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			if(pId < 0){
				return -1;
			}
		}
		params.add(new BasicNameValuePair("p_sid", ""+pId));
		tag.setPId(pId);
		
		int sId = conn.sendRequestForInteger("upload_tag.php", params);
		if(sId > 0){
			tag.setServerId(sId);
			new TagDao(context).connectWithServer(tag);
		}else{
			return sId;
		}
		
		for(Tag vo:tag.getTags()){
			int rs = (Integer) getMsg(context, conn, vo, new String[]{tag.getServerId()+""});
			if(rs < 0){
				return -1;
			}
		}
		
		return 1;
	}

	
}
