package com.mocaa.tagme.friends;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.mocaa.tagme.db.FollowDao;
import com.mocaa.tagme.db.UserDao;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.NetworkCallback;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.test.Debug;
import com.mocaa.tagme.transport.FollowRequest;
import com.mocaa.tagme.transport.GetMyFollows;
import com.mocaa.tagme.transport.GetUserTransport;

import android.content.Context;

public class UserPool {
	
	private UserDao userDao;
	private HashMap<String, User> userPool = new HashMap<String, User>();
	private AsyncLoader aLoader;
	
	public UserPool(Context context){
		aLoader = new AsyncLoader(context);
		userDao = new UserDao(context);
		initUserPool();
	}
	
	private void initUserPool(){
		ArrayList<User> allUsers = userDao.getAllUsers();
		for(User u:allUsers){
			userPool.put(u.getUserAccount(), u);
		}
	}

	private void loadUser(String userAccount, final OnUserAddListener l){
		
		User user = userPool.get(userAccount);
		if(user == null){
			GetUserTransport getUser = new GetUserTransport();
			String[] array = new String[]{userAccount};
			
			aLoader.downloadMessage(getUser, null, array, 
					new NetworkCallback(){
	
				@Override
				public void onLoaded(Object obj) {
					User user = (User) obj;
					Debug.o("load user:"+user.getUserAccount());
					userPool.put(user.getUserAccount(), user);
					userDao.createUser(user);
					if(l != null){
						l.onUserAdded(user);
					}
				}
			});
		}
		else{
			if(l != null){
				l.onUserAdded(user);
			}
		}
		
	}

	public void putUser(User u){
		userPool.put(u.getUserAccount(), u);
	}

	public User get(String account, OnUserAddListener l){
		Debug.o("get user:"+account);
		User u = userPool.get(account);
		if(u == null){
			Debug.o("load user null:"+account);
			loadUser(account, l);
		}
		return u;
	}
	
	public interface OnUserAddListener{
		public void onUserAdded(User user);
	}
}
