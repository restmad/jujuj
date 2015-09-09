package com.mocaa.tagme.share;

import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.homepage.HomeViewHolder;
import com.mocaa.tagme.util.BitmapUtil;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

public abstract class Share {

	protected Context context;
	public Share(Context context){
		this.context = context;
	}
	public abstract int getId();
	public void share(HomeViewHolder holder){
		holder.background.setDrawingCacheEnabled(true);
		holder.tagGroup.setDrawingCacheEnabled(true);
		Bitmap bm = BitmapUtil.drawImageOnImage(holder.background.getDrawingCache(), 
				holder.tagGroup.getDrawingCache());
				
		share(bm, holder.rootTag);
	}
	protected abstract void share(Bitmap bmp, Tag tag);
	
	
}
