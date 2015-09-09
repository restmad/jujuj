package com.mocaa.tagme.homepage;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;

import com.mocaa.tagme.db.LikeDao;
import com.mocaa.tagme.db.UserDao;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.NetworkCallback;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.transport.Connection;
import com.mocaa.tagme.transport.GetMyLikes;
import com.mocaa.tagme.transport.LikeRequest;

public class MyLikes {
	
	private static MyLikes instance;
	private AsyncLoader aLoader;
	private User mUser;
	private HashSet<Integer> likes;
	private LikeDao likeDao;
	private UserDao userDao;
	private Context context;

	public static MyLikes getInstance(Context context, User u){
		if(instance == null){
			instance = new MyLikes(context, u, null);
		}
		return instance;
	}
	
	public static MyLikes getInstance(Context context, User u, AsyncLoader aLoader){
		if(instance == null){
			instance = new MyLikes(context, u, aLoader);
		}
		instance.aLoader = aLoader;
		return instance;
	}
	
	private MyLikes(Context context, User u, AsyncLoader aLoader){
		this.context = context;
		mUser = u;
		likeDao = new LikeDao(context);
		userDao = new UserDao(context);
		likes = likeDao.getLikes(mUser.getUserAccount());
		if(aLoader != null){
			asyncGetMyLikes(aLoader);
		}else{
			getMyLikes();
		}
	}

	public static void destroy(){
		instance = null;
	}
	
	public HashSet<Integer> getLikes(){
		return likes;
	}

	private void asyncGetMyLikes(AsyncLoader aLoader){
		aLoader.downloadMessage(new GetMyLikes(), mUser.getUserAccount(), null, 
				new NetworkCallback(){

			@Override
			public void onLoaded(Object obj) {
				HashSet<Integer> list = (HashSet<Integer>) obj;
				if(list == null || list.size() == 0){
					//fail to load from server
				}else{
					if(list.equals(likes)){
						//do nothing
					}else{
						likeDao.syncWithServer(mUser.getUserAccount(), list);
						likes = list;
					}
				}		
			}
		});
	}

	private void getMyLikes(){
		HashSet<Integer> list = (HashSet<Integer>) new GetMyLikes().getMsg(context, 
				new Connection(), mUser.getUserAccount(), null);
		if(list == null || list.size() == 0){
			//fail to load from server
		}else{
			if(list.equals(likes)){
				//do nothing
			}else{
				likeDao.syncWithServer(mUser.getUserAccount(), list);
				likes = list;
			}
		}
	}
  
	public void asyncLike(final Tag tag, final String modi, final OnLikeChangedListener l){
		aLoader.downloadMessage(new LikeRequest(), null, 
				new String[]{mUser.getUserAccount(), tag.getServerId()+"", modi, tag.getUserAccount()}, 
				new NetworkCallback(){
					@Override
					public void onLoaded(Object obj) {
						if((Integer) obj > 0){
							int like = tag.getLikes();
							int ulike = mUser.getLikes();
							if(LikeRequest.M_LIKE.equals(modi)){
								likes.add(tag.getServerId());
								likeDao.like(mUser.getUserAccount(), tag.getServerId());
								ulike = ulike+1;
								like = like+1;
								mUser.setLikes(ulike);  
							}else{
								likes.remove((Integer)tag.getServerId());
								likeDao.cancelLike(mUser.getUserAccount(), tag.getServerId());
								ulike = ulike-1;
								like = like-1;
								ulike = Math.max(0, ulike);
								like = Math.max(0, like);
								mUser.setLikes(ulike);  
							}
							tag.setLikes(like);
							userDao.updateLikes(mUser);
						}
						if(l != null){
							l.onLikeChanged(modi);
						}
					}
			
		});
	}
	
	public interface OnLikeChangedListener{
		public void onLikeChanged(String modi);
	}
}
