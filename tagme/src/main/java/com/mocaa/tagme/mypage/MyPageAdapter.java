package com.mocaa.tagme.mypage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.TreeSet;

import com.mocaa.tagme.R;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.ImageCallback;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.view.MainViewAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MyPageAdapter extends MainViewAdapter{
	
	private Context context;
	private TreeSet<Tag> tags;
	private int width;
	private AsyncLoader aLoader;
	
	public MyPageAdapter(Context context, TreeSet<Tag> tags, int width){
		this.context = context;
		this.tags = tags;
		this.width = width;
		aLoader = new AsyncLoader(context);
	}

	@Override
	public int getCount() {
		return tags == null ? 0:tags.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("my tags:"+position);
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(
					R.layout.layout_mytag_item, null);
			holder = new ViewHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.mytag_item_img);
			holder.upload = (ImageView) convertView.findViewById(R.id.mytag_item_upload);
			
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(
					width, width);
			convertView.setLayoutParams(params);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		Tag tag = ((Tag) tags.toArray()[position]);
		if(!lock){
			setImage(holder.img, tag);
			setUploadIcon(holder.upload, tag);
		}
		return convertView;
	}
	
	static class ViewHolder{
		ImageView img, upload;
	}
	
	private void setUploadIcon(ImageView v, Tag tag){
		if(tag.getServerId() <= 0){
			v.setVisibility(View.VISIBLE);
		}else{
			v.setVisibility(View.GONE);
		}
	}
	
	private void setImage(ImageView v, Tag tag){
		v.setImageResource(R.drawable.img_default);
		Bitmap img = tag.getImg(context);
		if(img != null){
			v.setImageBitmap(img);
		}else{
			loadTagImage(tag, v);
		}
	}

	private void loadTagImage(final Tag tag, final ImageView image){
		aLoader.downloadImage(tag.getImgUrl(), true, new ImageCallback(){

			@Override
			public void onImageLoaded(Bitmap bitmap, String imageUrl) {
				if(bitmap != null){
					System.out.println("set image 5");
					image.setImageBitmap(bitmap);
				}
			}
			
		});
	}
	
	private boolean lock = false;

	@Override
	public void startMoving() {
		lock = true;
	}

	@Override
	public void stopMoving() {
		lock = false;
	}

}
