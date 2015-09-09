package com.mocaa.tagme.friends;

import java.util.HashSet;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.mocaa.tagme.R;
import com.mocaa.tagme.activity.SearchingActivity;
import com.mocaa.tagme.db.FollowDao;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.view.MainView;

public class FriendsView extends MainView{

	private View view;
	private FriendsAdapter adapter;

	public FriendsView(Context context, FrameLayout root, View titleBar, User u) {
		super(context, root, titleBar, u);
		
		view = inflateView(context, u);
	}
	
	private View inflateView(Context context, User u){
		ListView listview = new ListView(context, null);
		adapter = new FriendsAdapter(context, u);
		listview.setAdapter(adapter);
		return listview;
	}

	@Override
	public void show() {
		root.removeAllViews();
		root.addView(view);
		adapter.updateData();
		btn.setVisibility(View.VISIBLE);
    	btn.setImageResource(R.drawable.mt_search);  
    	setTitle(context.getResources().getString(R.string.title_friends));
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startMoving() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopMoving() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onButtonClick(View v) {
		context.startActivity(new Intent(context, SearchingActivity.class));
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
		return false;
	}

}
