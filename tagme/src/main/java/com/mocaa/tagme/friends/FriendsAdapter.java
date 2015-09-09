package com.mocaa.tagme.friends;

import java.util.HashSet;

import com.mocaa.tagme.adapter.UserAdapter;
import com.mocaa.tagme.entity.User;

import android.content.Context;

public class FriendsAdapter extends UserAdapter{
	
	private HashSet<String> follows = new HashSet<String>();
	
	public FriendsAdapter(Context context, User u){
		super(context, u);
		follows.addAll(myFollow.getFollows());
	}
	
	@Override
	public int getListCount() {
		// TODO Auto-generated method stub
		return follows.size();
	}

	@Override
	public String getUserAccount(int position) {
		// TODO Auto-generated method stub
		return (String) follows.toArray()[position];
	}

	public void updateData() {
		myFollow.updateFollows();
		follows.clear();
		follows.addAll(myFollow.getFollows());
	}
	
}
