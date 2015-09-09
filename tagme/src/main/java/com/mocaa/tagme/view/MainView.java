package com.mocaa.tagme.view;



import com.mocaa.tagme.R;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.global.GlobalDefs;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class MainView {

	public static final int REQUEST_NEW_TAG = 3;
//	public static final int REQUEST_CROP = 5;
	
	public static final int REQUEST_EDIT_PROFILE = 4;
	public static final int REQUEST_VIEW_TAG = 11;
	public static final int REQUEST_VIEW_PERSONAL_PAGE = 12;
	
	protected Context context;
	protected FrameLayout root;
	protected ImageButton btn;
	protected DisplayMetrics dm;
	protected View titleBar;
	protected User mUser;

	public MainView(Context context, FrameLayout root, View titleBar, User u){
		this.context = context;
		this.root = root;
		this.titleBar = titleBar;
		this.mUser = u;
		if(titleBar != null){
			btn = (ImageButton) titleBar.findViewById(R.id.main_btn);
		}
	}
	
	protected void setTitle(String text){
		((TextView) titleBar.findViewById(R.id.main_title)).setText(text);
	}
	
	public void showView(){
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onButtonClick(v);
			}
		});
		show();
	}
	public abstract void show();
	public abstract void hide();
	public abstract void startMoving();
	public abstract void stopMoving();
	public abstract void onButtonClick(View v);
	/**
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param intent
	 * @return true consumed, false otherwise
	 */
	public abstract boolean onActivityResult(int requestCode, int resultCode, Intent intent);
}
