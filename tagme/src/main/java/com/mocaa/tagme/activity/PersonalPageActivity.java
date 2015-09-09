package com.mocaa.tagme.activity;

import com.mocaa.tagme.R;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.friends.MyFollow;
import com.mocaa.tagme.friends.UserPool;
import com.mocaa.tagme.friends.MyFollow.OnFollowUpdateListener;
import com.mocaa.tagme.friends.UserPool.OnUserAddListener;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.mypage.PersonalPage;
import com.mocaa.tagme.transport.FollowRequest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

public class PersonalPageActivity extends Activity{

	private User mUser;
	private PersonalPage personalPage;
	private UserPool userPool;
	private MyFollow myFollow;
	private ToggleButton followBtn;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_page);
		
		Intent intent = getIntent();
		
		Object u = intent.getParcelableExtra(GlobalDefs.EXTRA_USER);
		if(u == null){
			finish();
		}else{
			mUser = (User) u;
		}

		myFollow = MyFollow.getInstance(this, mUser, new AsyncLoader(this),
				mOnFollowUpdateListener);
		if(!mUser.isLoaded()){
			userPool = new UserPool(this);
			User user = userPool.get(mUser.getUserAccount(), new OnUserAddListener(){

				@Override
				public void onUserAdded(User user) {
					mUser.copy(user);
					findViewAndSetContent();
				}
				
			});
			if(user != null){
				mUser.copy(user);
				findViewAndSetContent();
			}
		}else{
			findViewAndSetContent();
		}
	}

	private OnFollowUpdateListener mOnFollowUpdateListener = new OnFollowUpdateListener() {
		@Override
		public void onFollowUpdated() {
			
		}
	};

	private void findViewAndSetContent(){
//		TextView nameTv = (TextView) findViewById(R.id.personal_page_btn_name);
//		nameTv.setText(mUser.getUserName());
//		TextView idTv = (TextView) findViewById(R.id.personal_page_btn_id);
//		idTv.setText("@"+mUser.getUniqueId());
		
		followBtn = (ToggleButton) findViewById(R.id.view_tag_follow);
		if(mUser.getUserAccount().equals(UserPref.getUserAccount(this))){
			followBtn.setVisibility(View.GONE);
		}else{
			boolean b = !myFollow.followed(mUser.getUserAccount());
			followBtn.setChecked(b);
		}
		
		personalPage = new PersonalPage(this, mUser);
//		personalPage.setProgressBar(findViewById(R.id.personal_page_loading_bar));
		personalPage.findAndSetUserInfo(findViewById(R.id.personal_page_content));
//    	personalPage.setLineView(findViewById(R.id.personal_page_title_bar_line));
		personalPage.loadTags();
	}

	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			onBack();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void back(View v){
		onBack();
		finish();
	}
	
	private void onBack(){
		Intent intent = new Intent();
		intent.putExtra(GlobalDefs.EXTRA_FOLLOW, !followBtn.isChecked());
		intent.putExtra(GlobalDefs.EXTRA_USER_ACCOUNT, mUser.getUserAccount());
        setResult(RESULT_OK, intent);
	}
}
