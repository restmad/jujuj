package com.mocaa.tagme.mypage;

import com.mocaa.tagme.R;
import com.mocaa.tagme.activity.ProfileActivity;
import com.mocaa.tagme.activity.ViewTagActivity;
import com.mocaa.tagme.dialog.LoadingDialog;
import com.mocaa.tagme.dialog.NormalDialog;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.transport.Connection;
import com.mocaa.tagme.transport.UploadTagTransport;
import com.mocaa.tagme.view.MainView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;
  
public class MyPageView extends MainView{

	private View view;
	private PersonalPage personalPage;
	
	public MyPageView(Context context, FrameLayout root, View titleBar, User u) {
		super(context, root, titleBar, u);
		
		personalPage = new PersonalPage(context, mUser);
		personalPage.setProgressBar(titleBar.findViewById(R.id.main_loading_bar));
		
		view = inflateView();
	}

	public void setUser(User u){
		personalPage.setUser(u);
	}  
	
	private View inflateView(){
		View view = LayoutInflater.from(context).inflate(R.layout.layout_personal_page, null);
		personalPage.findAndSetUserInfo(view);
		GridView gridview = (GridView) view.findViewById(R.id.personal_page_gridview);
		gridview.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				showConfirmDialog(position);
				return true;
			}
			
		});
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Tag tag = personalPage.getTag(position);
				if(tag != null){
					if(tag.getServerId() <= 0){
						showUploadDialog(tag);
					}else{
						Intent intent = new Intent(context, ViewTagActivity.class);
						intent.putExtra(GlobalDefs.EXTRA_TAG, tag);
						intent.putExtra(GlobalDefs.EXTRA_USER, mUser);
						((Activity) context).startActivityForResult(intent, MainView.REQUEST_VIEW_TAG);
					}
				}
			}
		});
		
		view.findViewById(R.id.layout_user_portrait).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((Activity) context).startActivityForResult(
						new Intent(context, ProfileActivity.class), REQUEST_EDIT_PROFILE);
			}
		});
		return view;
	}

	private LoadingDialog dialog;
	private void showDialog(String text){
		if(dialog == null){
			dialog = LoadingDialog.createDialog(context);
		}
		dialog.setTipText(text);
		dialog.show();
	}

	private void uploadToServer(final Tag tag){
		showDialog(context.getResources().getString(R.string.uploading));
		new Thread(){
			public void run(){
				UploadTagTransport upload = new UploadTagTransport();
				int result = (Integer) upload.getMsg(
						context, new Connection(), tag, null);
				Message msg = new Message();
				msg.what = WHAT_UPLOAD_DONE;
				msg.arg1 = result;
				msg.obj = tag;
				mHandler.sendMessage(msg);
				
			}
		}.start();
	}
	
	private final int WHAT_UPLOAD_DONE = 1;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case WHAT_UPLOAD_DONE:
				if(msg.arg1 > 0){
					Toast.makeText(context, R.string.uploading_done, Toast.LENGTH_SHORT).show();
					personalPage.notifyDateSetChanged();
					if(mOnTagChangedListener != null){
						mOnTagChangedListener.onTagChanged((Tag) msg.obj, 
								OnTagChangedListener.FLAG_ADD);
					}
				}else{
					Toast.makeText(context, R.string.uploading_fail, Toast.LENGTH_SHORT).show();
				}
				if(dialog != null){
					dialog.dismiss();
					dialog = null;
				}
				break;
			}
		}
	};
	
	@SuppressWarnings("static-access")
	private void showConfirmDialog(final int position){
		NormalDialog.createDialog(context)
				.setOnListener(new NormalDialog.BtnListener(){
					@Override
					public void onClicked(int id) {
						if(id == NormalDialog.DONE){
							personalPage.delteTag(position, mOnTagChangedListener);
						}  
					}
				}).show();
	}

	@SuppressWarnings("static-access")
	private void showUploadDialog(final Tag tag){
		NormalDialog.createDialog(context)
				.setContentText(R.string.info_upload)
				.setOnListener(new NormalDialog.BtnListener(){
					@Override
					public void onClicked(int id) {
						if(id == NormalDialog.DONE){
							uploadToServer(tag);
						}  
					}
				}).show();
	}
	
	@Override
	public void show() {
		root.removeAllViews();
    	root.addView(view);
    	new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				personalPage.loadTags();
			}
		}, 500);
    	
//    	personalPage.setLineView(titleBar.findViewById(R.id.main_title_bar_line));
		btn.setVisibility(View.GONE);
    	setTitle(context.getResources().getString(R.string.title_my_tag));
	}

	@Override
	public void hide() {
//        personalPage.resetLineView();
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		if(REQUEST_EDIT_PROFILE == requestCode){
			if(resultCode == Activity.RESULT_OK){
				User u = data.getParcelableExtra(GlobalDefs.EXTRA_USER);
				setUser(u);
			}
		}
		return false;
	}

	@Override
	public void startMoving() {
		personalPage.startMoving();
	}

	@Override
	public void stopMoving() {
		personalPage.stopMoving();
	}
	
	public void addTag(Tag tag) {
		System.err.println("add tag:"+tag.getId());
		personalPage.addTag(tag);
	}
  
	@Override
	public void onButtonClick(View v) {
	}

	public void setOnTagChangedListener(OnTagChangedListener l) {
		this.mOnTagChangedListener = l;
	}

	private OnTagChangedListener mOnTagChangedListener;
	
	public interface OnTagChangedListener{
		public static final int FLAG_ADD = 0;
		public static final int FLAG_REMOVE = 1;
		public void onTagChanged(Tag tag, int flag);
	}
}
