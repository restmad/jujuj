package com.mocaa.tagme.share;

import java.util.ArrayList;

import com.mocaa.tagme.homepage.HomeViewHolder;

import android.content.Context;
import android.content.Intent;

public class ShareController {
	
	private ArrayList<Share> shares = new ArrayList<Share>();
	
	public ShareController(Context context){
		shares.add(new QZoneShare(context));
		shares.add(new WeiboShare(context));
		shares.add(new WechatShare(context));
		shares.add(new RenrenShare(context));
	}

	public void share(int id, HomeViewHolder holder){
		for(Share s:shares){
			if(s.getId() == id){
				s.share(holder);
			}
		}
	}

}
