package com.mocaa.tagme.view;

import com.mocaa.tagme.mypage.MyPageAdapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class MainViewAdapter extends BaseAdapter{

	@Override
	public abstract int getCount();

	@Override
	public abstract Object getItem(int position);

	@Override
	public abstract long getItemId(int position);

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);
	
	public abstract void startMoving();

	public abstract void stopMoving();
}
