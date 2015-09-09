package com.mocaa.tagme.activity;

import android.content.Context;

import com.mocaa.tagme.R;
import com.mocaa.tagme.db.UserDao;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.dialog.LoadingDialog;
import com.mocaa.tagme.download.ImageLoaderImpl;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.test.TestActivity;
import com.mocaa.tagme.transport.Connection;
import com.mocaa.tagme.transport.SignInRequest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

public class LaunchActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		initScreen();
		
		if(new UserPref(this).isFirstTime()){
			startActivity(new Intent(this, GuideActivity.class));
		}else{
			if(UserPref.signedIn(this)){
				startActivity(new Intent(this, MainActivity.class));
			}else{
				startActivity(new Intent(this, SignInActivity.class));
			}
		}
		finish();
		
//		autoSignIn();
	}

	private void initScreen(){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        GlobalDefs.setScreen(dm.widthPixels, dm.heightPixels, dm.density);
	}

	private void autoSignIn(){
		SignInRequest signin = new SignInRequest();
		Object result = signin.getMsg(this, new Connection(), 
				null, new String[]{"s@s.s", "s"});
		User user = (User) result;
		ImageLoaderImpl.loadBitmapFromUrl(user.getPortraitUrl(), this);
		dealSignInResult(user);
	}
	

	private void dealSignInResult(Object result){
		UserPref pref = new UserPref(this);
		if(result instanceof User){
			/*
			 * sign in
			 */
			pref.setUserAccount(((User) result).getUserAccount());
			pref.signIn();
			UserDao dao = new UserDao(this);
			dao.signInUser((User) result);
			
			
			startActivity(new Intent(this, MainActivity.class));
			finish();
			return;
		}else if(result instanceof Integer){
			int iResult = (Integer) result;
			if(iResult == -3){
				Toast.makeText(this, getResources().getString(R.string.toast_pwd_wrong), 
						Toast.LENGTH_SHORT).show();
			}else if(iResult == -4){
				Toast.makeText(this, getResources().getString(R.string.toast_account_not_exist), 
						Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, getResources().getString(R.string.toast_conn_error), 
						Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(this, getResources().getString(R.string.toast_conn_error), 
					Toast.LENGTH_SHORT).show();
		}
	}
	
}
