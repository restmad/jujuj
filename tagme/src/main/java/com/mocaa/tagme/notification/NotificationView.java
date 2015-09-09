package com.mocaa.tagme.notification;

import java.util.HashSet;
import java.util.TreeSet;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.mocaa.tagme.R;
import com.mocaa.tagme.activity.PersonalPageActivity;
import com.mocaa.tagme.activity.ViewTagActivity;
import com.mocaa.tagme.db.NotiDao;
import com.mocaa.tagme.entity.Notification;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.view.MainView;

public class NotificationView extends MainView{
	
	private View view;
	private NotificationAdapter adapter;
	private NotiDao dao;
	private TreeSet<Notification> list;

	public NotificationView(Context context, FrameLayout root, View titleBar, User u) {
		super(context, root, titleBar, u);
		view = inflateView();
	}
	
	public void addList(TreeSet<Notification> l){
		list.addAll(l);
		dao.addAll(l);
		adapter.notifyDataSetChanged();
	}
	
	private View inflateView(){
		dao = new NotiDao(context);
		View view = LayoutInflater.from(context).inflate(R.layout.view_notification, null);
		ListView listview = (ListView) view.findViewById(R.id.notification_listview);
		list = dao.getAll();
		adapter = new NotificationAdapter(context, mUser, list);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Notification noti = (Notification) list.toArray()[position];
				Intent intent = null;
				switch(noti.getType()){
				case Notification.TYPE_COMMENT:
				case Notification.TYPE_LIKE:
					intent = new Intent(context, ViewTagActivity.class);
					intent.putExtra(GlobalDefs.EXTRA_TAG, new Tag(noti.getTagId()));
					intent.putExtra(GlobalDefs.EXTRA_USER, mUser);
					break;
				case Notification.TYPE_FOLLOW:
					intent = new Intent(context, PersonalPageActivity.class);
					intent.putExtra(GlobalDefs.EXTRA_USER, new User(noti.getUserAccount()));
					break;
				}
				if(intent != null){
					context.startActivity(intent);
				}
				list.remove(noti);
				dao.delete(noti);
				adapter.notifyDataSetChanged();
			}
			
		});
		return view;
	}

	@Override
	public void show() {
		root.removeAllViews();
		root.addView(view);
		btn.setVisibility(View.VISIBLE);
		btn.setImageResource(R.drawable.ic_delete);
    	setTitle(context.getResources().getString(R.string.title_notifications));
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {

		return false;
	}

}
