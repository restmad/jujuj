package com.mocaa.tagme.adapter;

import java.util.ArrayList;

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
import com.mocaa.tagme.friends.UserPool;
import com.mocaa.tagme.friends.UserPool.OnUserAddListener;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.homepage.HomeViewHolder;
import com.mocaa.tagme.transport.FollowRequest;
import com.mocaa.tagme.view.MainView;

public class SearchingAdapter extends UserAdapter{

	private ArrayList<String> result = new ArrayList<String>();
	
	public SearchingAdapter(Context context, User u){
		super(context, u);
	}
	
	public void setResult(ArrayList<String> result){
		this.result = result;
		notifyDataSetChanged();
	}
	
	@Override
	public int getListCount() {
		return result.size();
	}

	@Override
	public String getUserAccount(int position) {
		return result.get(position);
	}
	
}
