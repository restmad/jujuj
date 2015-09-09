package com.mocaa.tagme.notification;

import java.util.HashSet;
import java.util.TreeSet;

import com.mocaa.tagme.R;
import com.mocaa.tagme.activity.PersonalPageActivity;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.ImageCallback;
import com.mocaa.tagme.entity.Notification;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.friends.MyFollow;
import com.mocaa.tagme.friends.UserPool;
import com.mocaa.tagme.friends.MyFollow.OnFollowUpdateListener;
import com.mocaa.tagme.friends.UserPool.OnUserAddListener;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.util.BitmapUtil;
import com.mocaa.tagme.view.MainView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationAdapter extends BaseAdapter{

	protected UserPool userPool;
	protected User mUser;
	protected AsyncLoader aLoader;
	private MyFollow myFollow;
	private Context context;
	private TreeSet<Notification> list;
	
	public NotificationAdapter(Context context, User u, TreeSet<Notification> list){
		this.context = context;
		userPool = new UserPool(context);
		aLoader = new AsyncLoader(context);
		myFollow = MyFollow.getInstance(context, u, aLoader, mOnFollowUpdateListener);
		this.list = list;
	}

	private OnFollowUpdateListener mOnFollowUpdateListener = new OnFollowUpdateListener(){

		@Override
		public void onFollowUpdated() {
			notifyDataSetChanged();
		}
		  
	};
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
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
		ViewHolder tag = null;
		if(convertView == null){
			tag = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_notification, null);
			tag.portraitIv = (ImageView) convertView.findViewById(R.id.notification_portrait);
			tag.contentTv = (TextView) convertView.findViewById(R.id.notification_content);
			tag.timeTv = (TextView) convertView.findViewById(R.id.notification_time);
			convertView.setTag(tag);
		}else{
			tag = (ViewHolder) convertView.getTag();
		}

		final ViewHolder holder = tag;
		final Notification noti = (Notification)list.toArray()[position];
		String userAccount = noti.getUserAccount();
		if(userAccount != null){
			User user = userPool.get(userAccount, new OnUserAddListener(){

				@Override
				public void onUserAdded(User user) {
					setContent(holder, user, noti);
					notifyDataSetChanged();
				}
				
			});
			if(user != null){
				setContent(holder, user, noti);
			}
		}
		return convertView;
	}
	
	private void setContent(ViewHolder holder, final User user, Notification noti){
		if(user != null){
			
			Bitmap portrait = user.getPortraitThumb();
			if(portrait == null){
				loadUserPortrait(user, holder);
				holder.portraitIv.setImageResource(R.drawable.portrait_default_round);
			}else{
				holder.portraitIv.setImageBitmap(portrait);
			}
			holder.portraitIv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, PersonalPageActivity.class);
					intent.putExtra(GlobalDefs.EXTRA_USER, user);
					intent.putExtra(GlobalDefs.EXTRA_FOLLOW, 
							myFollow.followed(user.getUserAccount()));
					((Activity) context).startActivityForResult(intent, MainView.REQUEST_VIEW_PERSONAL_PAGE);
					
				}
			});
			holder.contentTv.setText(noti.getContent(context));
			holder.timeTv.setText(noti.getTime(context));
		}
		
	}

	static class ViewHolder{
		ImageView portraitIv;
		TextView contentTv, timeTv;
	}
	
	private void loadUserPortrait(final User user, final ViewHolder holder){
		aLoader.downloadImage(user.getPortraitUrl(), true, new ImageCallback(){

			@Override
			public void onImageLoaded(Bitmap bitmap, String imageUrl) {
				if(bitmap != null){
					bitmap = BitmapUtil.getPortraitBitmap(context, bitmap);
					
					user.setPortraitThumb(bitmap);
					holder.portraitIv.setImageBitmap(bitmap);
				}
			}
			
		});
	}
	
}
