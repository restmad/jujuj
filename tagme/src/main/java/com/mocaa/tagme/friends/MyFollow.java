package com.mocaa.tagme.friends;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;

import com.mocaa.tagme.db.FollowDao;
import com.mocaa.tagme.db.UserDao;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.NetworkCallback;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.transport.Connection;
import com.mocaa.tagme.transport.FollowRequest;
import com.mocaa.tagme.transport.GetMyFollows;

public class MyFollow {

	private static ArrayList<OnFollowUpdateListener> mListeners;
	private static MyFollow instance;
	private Context context;
	private FollowDao followDao;
	private HashSet<String> follows;
	private User mUser;
	private AsyncLoader aLoader;
	private UserDao userDao;
	
	public static MyFollow getInstance(Context context, User u){
		if(instance == null){
			instance = new MyFollow(context, u, null);
		}
		return instance;
	}
	
	public static MyFollow getInstance(Context context, User u, AsyncLoader aLoader, 
			OnFollowUpdateListener l){
		if(instance == null){
			instance = new MyFollow(context, u, aLoader);
		}
		instance.aLoader = aLoader;
		if(l != null){
			if(mListeners == null){
				mListeners = new ArrayList<OnFollowUpdateListener>();
			}
			mListeners.add(l);
		}
		return instance;
	}
	
	public static void destroy(){
		if(mListeners != null){
			mListeners.clear();
			mListeners = null;
		}
		instance = null;
	}
	
	private MyFollow(Context context, User u, AsyncLoader aLoader){
		this.context = context;
		this.mUser = u;
		followDao = new FollowDao(context);
		userDao = new UserDao(context);
		follows = followDao.getFollowing(u.getUserAccount());
		if(aLoader != null){
			asyncGetMyFollows(aLoader);
		}else{
			getMyFollows();
		}
	}

	private void asyncGetMyFollows(AsyncLoader aLoader){
		aLoader.downloadMessage(new GetMyFollows(), mUser.getUserAccount(), 
				null, new NetworkCallback(){
			@Override
			public void onLoaded(Object obj) {
				HashSet<String> list = (HashSet<String>) obj;
				if(list == null || list.size() == 0){
					//fail to load from server
				}else{
					if(list.equals(follows)){
						//do nothing
					}else{
						followDao.syncWithServer(mUser.getUserAccount(), list);
						follows = list;
					}
				}
			}
			
		});
		
		
	}

	private void getMyFollows(){
		HashSet<String> list = (HashSet<String>) new GetMyFollows().getMsg(context, 
				new Connection(), mUser.getUserAccount(), null);
		if(list == null || list.size() == 0){
			//fail to load from server
		}else{
			if(list.equals(follows)){
				//do nothing
			}else{
				followDao.syncWithServer(mUser.getUserAccount(), list);
				follows = list;
			}
		}
	}

	public void asyncFollow(final String followingAccount, final String modi,
			final OnFollowUpdateListener l){
		final String account = UserPref.getUserAccount(context);
		aLoader.downloadMessage(new FollowRequest(), null, 
				new String[]{account, followingAccount, modi}, 
				new NetworkCallback(){
					@Override
					public void onLoaded(Object obj) {
						if((Integer) obj > 0){
							if(FollowRequest.M_FOLLOW.equals(modi)){
								follows.add(followingAccount);
								followDao.follow(account, followingAccount);
								int uFollow = mUser.getFollowing()+1;
								mUser.setFollowing(uFollow);
							}else{
								follows.remove(followingAccount);
								followDao.cancelFollow(account, followingAccount);
								int uFollow = mUser.getFollowing()-1;
								uFollow = Math.max(0, uFollow);
								mUser.setFollowing(uFollow);
							}
							userDao.updateFollows(mUser);
						}
						if(l != null){
							l.onFollowUpdated();
						}
					}
			
		});
	}

	public boolean followed(String account){
		return follows.contains(account);
	}
	
	public HashSet<String> getFollows(){
		return follows;
	}
	
	public void updateFollows(){
		follows = followDao.getFollowing(mUser.getUserAccount());
	}
	public interface OnFollowUpdateListener{
		public void onFollowUpdated();
	}
}
