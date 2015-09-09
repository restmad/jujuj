package com.mocaa.tagme.homepage;

import java.io.File;
import java.util.ArrayList;

import com.mocaa.tagme.R;
import com.mocaa.tagme.activity.EditImgActivity;
import com.mocaa.tagme.db.TagDao;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.dialog.LoadingDialog;
import com.mocaa.tagme.dialog.SelectImageDialog;
import com.mocaa.tagme.dialog.SelectImageDialog.BtnListener;
import com.mocaa.tagme.download.ImageLoaderImpl;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.mypage.MyPageAdapter;
import com.mocaa.tagme.transport.Connection;
import com.mocaa.tagme.transport.UploadTagTransport;
import com.mocaa.tagme.util.BitmapUtil;
import com.mocaa.tagme.util.ImageSelector;
import com.mocaa.tagme.view.MainView;
import com.soundcloud.android.crop.Crop;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

public abstract class AddingTagView extends MainView{

	protected TagDao dao;
	protected Tag mSelectedTag;
	private LoadingDialog dialog;
	private ImageSelector imgSelector;
	
	public AddingTagView(Context context, FrameLayout root, View titleBar, User u) {
		super(context, root, titleBar, u);
		dao = new TagDao(context);
		imgSelector = new ImageSelector(context);
	}
	
	public void setUser(User user){
		this.mUser = user;
	}

	@Override
	public abstract void show();

	@Override
	public abstract void hide();

	@Override
	public abstract void startMoving();

	@Override
	public abstract void stopMoving();

	public void selectGallery(){
		imgSelector.selectGallery();
    }  
	
	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		
		case REQUEST_NEW_TAG:
			/*
			 * adding a new tag in my page
			 */
			if (resultCode == Activity.RESULT_OK) { 
				Tag tag = (Tag) data.getParcelableExtra(GlobalDefs.EXTRA_TAG);
				System.out.println("uploading " + tag.getId());
				uploadToServer(tag);
			}else if(resultCode == Activity.RESULT_CANCELED) {
				/*
				 * delete the root
				 */
				Tag tag = (Tag) data.getParcelableExtra(GlobalDefs.EXTRA_TAG);
				System.out.println("deleting " + tag.getId());
				dao.deleteTagWithChildren(tag);
			}  
			return true;
		}
		
		imgSelector.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Crop.REQUEST_CROP) {
	        handleCrop(resultCode, data);
	    }
		return false;
	}
	
	private void handleCrop(int resultCode, Intent result) {
	    if (resultCode == Activity.RESULT_OK) {
	        
	        Tag tag = new Tag(Tag.TYPE_ROOT, mUser.getUserAccount());
			tag.setImgUri(imgSelector.getImgUri());
			
			/*
			 * save first, to get an ID
			 */
			tag.setUserAccount(UserPref.getUserAccount(context));
			dao.saveTagWithId(tag, true);
			System.out.println("creating " + tag.getId());
			Intent intent = new Intent(context, EditImgActivity.class);
		    intent.putExtra(GlobalDefs.EXTRA_TAG, tag);
		    ((Activity) context).startActivityForResult(intent, REQUEST_NEW_TAG);
	    } else if (resultCode == Crop.RESULT_ERROR) {
	        Toast.makeText(context, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
	    }
	}

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
					Tag tag = (Tag) msg.obj;
					addTag(tag);
					
					Toast.makeText(context, R.string.uploading_done, Toast.LENGTH_SHORT).show();
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
	
	public abstract void addTag(Tag tag);

	
}
