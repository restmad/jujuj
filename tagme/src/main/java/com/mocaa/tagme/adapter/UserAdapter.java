package com.mocaa.tagme.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mocaa.tagme.R;
import com.mocaa.tagme.activity.PersonalPageActivity;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.ImageCallback;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.friends.MyFollow;
import com.mocaa.tagme.friends.UserPool;
import com.mocaa.tagme.friends.MyFollow.OnFollowUpdateListener;
import com.mocaa.tagme.friends.UserPool.OnUserAddListener;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.transport.FollowRequest;
import com.mocaa.tagme.util.BitmapUtil;
import com.mocaa.tagme.view.MainView;

public abstract class UserAdapter extends BaseAdapter{

//	private ArrayList<String> result = new ArrayList<String>();
	protected Context context;
	protected UserPool userPool;
	protected User mUser;
	protected AsyncLoader aLoader;
	protected MyFollow myFollow;
	
	public UserAdapter(Context context, User u){
		this.context = context;
		this.mUser = u;
		userPool = new UserPool(context);
		aLoader = new AsyncLoader(context);
		myFollow = MyFollow.getInstance(context, u, aLoader, mOnFollowUpdateListener);
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
		return getListCount();
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
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_friend, null);
			tag.portraitIv = (ImageView) convertView.findViewById(R.id.friend_portrait);
			tag.nameTv = (TextView) convertView.findViewById(R.id.friend_name);
			tag.idTv = (TextView) convertView.findViewById(R.id.friend_id);
			tag.followTbn = (ToggleButton) convertView.findViewById(R.id.friend_follow_tb);
			convertView.setTag(tag);
		}else{
			tag = (ViewHolder) convertView.getTag();
		}

		final ViewHolder holder = tag;
		String userAccount = getUserAccount(position);
		if(userAccount != null){
			User user = userPool.get(userAccount, new OnUserAddListener(){

				@Override
				public void onUserAdded(User user) {
					setUser(holder, user);
					notifyDataSetChanged();
				}
				
			});
			if(user != null){
				setUser(holder, user);
			}
		}
		return convertView;
	}
	
	private void setUser(ViewHolder holder, final User user){
		if(user != null){
			final String userAccount = user.getUserAccount();
			if(userAccount.equals(mUser.getUserAccount())){
				holder.followTbn.setVisibility(View.GONE);
			}else{
				if(myFollow.followed(userAccount)){
					holder.followTbn.setChecked(false);
				}else{
					holder.followTbn.setChecked(true);
				}
			}
			holder.followTbn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					myFollow.asyncFollow(userAccount, 
							((CompoundButton) v).isChecked() ? 
									FollowRequest.M_UNFOLLOW:FollowRequest.M_FOLLOW,
									mOnFollowUpdateListener);
				}
				
			});
			holder.nameTv.setText(user.getUserName());
			holder.idTv.setText("@"+user.getUniqueId());
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
					((Activity) context).startActivityForResult(intent, MainView.REQUEST_VIEW_PERSONAL_PAGE);
					
				}
			});

		}
		
	}

	static class ViewHolder{
		ImageView portraitIv;
		TextView nameTv, idTv;
		ToggleButton followTbn;
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
	
	public abstract int getListCount();
	public abstract String getUserAccount(int position);
}
