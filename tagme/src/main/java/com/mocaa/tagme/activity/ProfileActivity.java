package com.mocaa.tagme.activity;

import java.io.File;

import com.mocaa.tagme.R;
import com.mocaa.tagme.db.UserDao;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.dialog.EditableDialog;
import com.mocaa.tagme.dialog.LoadingDialog;
import com.mocaa.tagme.download.ImageLoaderImpl;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.transport.Connection;
import com.mocaa.tagme.transport.UpDateUidTransport;
import com.mocaa.tagme.transport.UpdateProfileTransport;
import com.mocaa.tagme.util.BitmapUtil;
import com.mocaa.tagme.util.ImageSelector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.mocaa.tagme.view.PortraitView;
import com.soundcloud.android.crop.Crop;

public class ProfileActivity extends Activity{

	private Bitmap tempPortrait, portraitThumb;
	private User mUser;
	private PortraitView portraitIv;
	private TextView nameTv, uidTv, livingTv;
	private TextSwitcher genderTs;
	private LoadingDialog dialog;
	private UserDao userDao;
	private ImageSelector imgSelector;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		imgSelector = new ImageSelector(this);
		userDao = new UserDao(this);
		
		portraitIv = (PortraitView) findViewById(R.id.activity_profile_portrait);
		nameTv = (TextView) findViewById(R.id.activity_profile_user_name);
		uidTv = (TextView) findViewById(R.id.activity_profile_uid);
		livingTv = (TextView) findViewById(R.id.activity_profile_place);
		genderTs = (TextSwitcher) findViewById(R.id.activity_profile_gender);
		
		setContent();
	}
	
	private void setContent(){

		Intent intent = this.getIntent();
		mUser = intent.getParcelableExtra(GlobalDefs.EXTRA_USER);
		if(mUser == null){
			mUser = new UserPref(this).getMyProfile();
		}
		
		Bitmap bm = mUser.getPortraitThumb();
		if(bm == null){
			portraitIv.setImageResource(R.drawable.portrait_default_round);
			
		}else{
			portraitIv.setImageBitmap(bm);
		}
		nameTv.setText(mUser.getUserName());
		uidTv.setText(mUser.getUniqueId());
		livingTv.setText(mUser.getPlace());
		if(mUser.getGender() > 0){
			genderTs.showNext();
		}
	}
	
	public void click_portrait(View v){
		imgSelector.selectGallery();
	}

	public void click_user_name(View v){
		showEditingDialog(nameTv, getResources().getString(R.string.edit) + " " +
				getResources().getString(R.string.user_name),
				nameTv.getText().toString());
	}

	public void click_place(View v){
		showEditingDialog(livingTv, getResources().getString(R.string.edit) + " " +
				getResources().getString(R.string.living_in),
				livingTv.getText().toString());
	}

	public void click_gender(View v){
		genderTs.showNext();
	}
	
	public void back(View v){
		finish();
	}
	
	private void showEditingDialog(final TextView view, String title, String content){
		@SuppressWarnings("static-access")
		EditableDialog dailog = EditableDialog
				.createDialog(this)
				.setTitle(title)
				.setContentText(content)
				.setOnListener(new EditableDialog.BtnListener() {
					@Override
					public void onClicked(int id,  String text) {
						switch(id){
						case EditableDialog.DONE:
							view.setText(text);
							break;
						}
					}
				}); 
		dailog.setCanceledOnTouchOutside(true);
		dailog.show();
	}

	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		imgSelector.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Crop.REQUEST_CROP) {
	        handleCrop(resultCode, data);
	    }
		super.onActivityResult(requestCode, resultCode, data);  
	} 

	private void handleCrop(int resultCode, Intent result) {
	    if (resultCode == Activity.RESULT_OK) {
	    	mUser.setPortraitUrl(imgSelector.getImgUri());
	    	tempPortrait = ImageLoaderImpl.getBitmapFromFile(
	    			this, imgSelector.getImgUri());
	    	portraitThumb = BitmapUtil.getPortraitBitmap(
	    			ProfileActivity.this, tempPortrait);
			portraitIv.setImageBitmap(portraitThumb);
	    } else if (resultCode == Crop.RESULT_ERROR) {
	        Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
	    }
	}

	public void save(View v){
		showDialog(getResources().getString(R.string.uploading));
		new Thread(){
			public void run(){
				UpdateProfileTransport upload = new UpdateProfileTransport();
				
				String userName = nameTv.getText().toString();
				String livingIn = livingTv.getText().toString();
//				String uid = uidTv.getText().toString();
				int gender = Integer.parseInt(
						(String) genderTs.getCurrentView().getTag());
				
				Object result = upload.getMsg(
						ProfileActivity.this, new Connection(), tempPortrait, 
						new String[]{userName, livingIn, gender+""});

				if(result instanceof String){
					String url = (String) result;
					if(url == null || url.equals("")){
						//not update bitmap
					}else{
						mUser.setPortraitThumb(portraitThumb);
						mUser.setPortraitUrl(url);
					}
					mUser.setUserName(userName);
					mUser.setPlace(livingIn);
//					mUser.setUniqueId(uid);
					mUser.setGender(gender);
					mHandler.sendEmptyMessage(WHAT_UPDATE_SUCCEED);
				}else{
					mHandler.sendEmptyMessage(WHAT_UPDATE_FAIL);
				}
				
			}
		}.start();
	}

	private void showDialog(String text){
		if(dialog == null){
			dialog = LoadingDialog.createDialog(this);
		}
		dialog.setTipText(text);
		dialog.show();
	}

	private static final int WHAT_UPDATE_SUCCEED = 1;
	private static final int WHAT_UPDATE_FAIL = 2;
	private static final int WHAT_UPDATE_UID_SUCCEED = 3;
	private static final int WHAT_UPDATE_UID_FAIL = 4;
	public static final int RESULT_SIGN_OUT = 1001;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
				case WHAT_UPDATE_SUCCEED:
					userDao.updateUser(mUser);
					Intent data = new Intent();
					data.putExtra(GlobalDefs.EXTRA_USER, mUser);
					ProfileActivity.this.setResult(Activity.RESULT_OK, data);
					finish();
					break;
				case WHAT_UPDATE_FAIL:
					Toast.makeText(ProfileActivity.this, 
							ProfileActivity.this.getResources().getString(R.string.toast_update_fail), 
							Toast.LENGTH_SHORT).show();
					break;
				case WHAT_UPDATE_UID_SUCCEED:
					userDao.updateUid(mUser);
					uidTv.setText(mUser.getUniqueId());
					break;
				case WHAT_UPDATE_UID_FAIL:
					Toast.makeText(ProfileActivity.this, 
							ProfileActivity.this.getResources().getString(msg.arg1), 
							Toast.LENGTH_SHORT).show();
					break;
			}
			if(dialog != null){
				dialog.dismiss();
				dialog = null;
			}
		}
	};

	public void click_uid(View v){
		showEditUidDialog();
	}

	private void showEditUidDialog(){
		String title = getResources().getString(R.string.edit) + " " +
				getResources().getString(R.string.unique_id);
		String content = uidTv.getText().toString();
		@SuppressWarnings("static-access")
		EditableDialog dailog = EditableDialog
				.createDialog(this)
				.setTitle(title)
				.setContentText(content)
				.setOnListener(new EditableDialog.BtnListener() {
					@Override
					public void onClicked(int id,  String text) {
						switch(id){
						case EditableDialog.DONE:
							if(!text.equals(mUser.getUniqueId())){
								updateUid(text);
							}
							break;
						}
					}
				}); 
		dailog.setCanceledOnTouchOutside(true);
		dailog.show();
	}

	private void updateUid(final String uid){
		showDialog(getResources().getString(R.string.uploading));
		new Thread(){
			public void run(){
				UpDateUidTransport upload = new UpDateUidTransport();
				
				int result = (Integer) upload.getMsg(
						ProfileActivity.this, new Connection(), null, 
						new String[]{mUser.getUserAccount(), uid});
				
				if(result > 0){
					mUser.setUniqueId(uid);
					mHandler.sendEmptyMessage(WHAT_UPDATE_UID_SUCCEED);
				}else{
					Message msg = new Message();
					msg.what = WHAT_UPDATE_UID_FAIL;
					if(result == -2){
						msg.arg1 = R.string.uid_exist;
					}else{
						msg.arg1 = R.string.toast_update_fail;
					}
					mHandler.sendMessage(msg);
				}
				
			}
		}.start();
	}
	
	public void sign_out(View v){
		new UserPref(this).signOut();
		setResult(RESULT_SIGN_OUT);
		finish();
	}
}
